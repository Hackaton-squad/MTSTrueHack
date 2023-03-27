import argparse
import atexit
from enum import Enum
from flask import Flask, request, jsonify
from furl import furl
import multiprocessing as mp
from video import load_video, process_by_frames
import os
import requests
import uuid
from model import Model
from exceptions import RetryableException

app = Flask(__name__, static_url_path="")

n_proc = 2
processes = []
queue = mp.Queue()
barrier = mp.Barrier(n_proc + 1)

class Status(Enum):
    NOT_PROCESSED = "NOT_PROCESSED"
    PROCESSED = "PROCESSED"


def process(queue, server_url):
    model = Model()
    status_url = str(furl(server_url) / "status")

    def callback(timestamp, caption):
        print(f"Timestamp: {timestamp}, caption: {caption}")
        requests.post(status_url, json={'url': url, 'start': timestamp, 'sentence': caption})

    barrier.wait()

    while url := queue.get():
        try:
            filename = str(uuid.uuid4())

            load_video(url, filename)

            # TODO - while loading video also load subtitles, then pass them to subtitle_path
            process_by_frames(filename, callback=callback, predict=model.predict_caption, subtitle_path=None)
            os.remove(filename)
        except RetryableException as retr:
            error_msg = str(retr)
            requests.post(status_url, json={'url': url, 'processing': Status.NOT_PROCESSED.value})
        except Exception as e:
            error_msg = str(e)
            requests.post(status_url, json={'url': url, 'processing': Status.NOT_PROCESSED.value})
        else:
            requests.post(status_url, json={'url': url, 'processing': Status.PROCESSED.value})


@app.route("/process", methods=['POST'])
def handle_request():
    data = request.get_json(force=True)
    url = data['url']

    queue.put(url)
    return jsonify(success=True)


def close_running_processes():
    for p in processes:
        p.join()
    print("Processes complete, finishing...")


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("--url", help="url to backend", default="http://localhost:8080")
    args = parser.parse_args()

    atexit.register(close_running_processes)
    for _ in range(n_proc):
        print("Starting proc...")
        proc = mp.Process(target=process, args=[queue, args.url])
        processes.append(proc)
        proc.start()
    barrier.wait()
    app.run(host='0.0.0.0', port=8081)
