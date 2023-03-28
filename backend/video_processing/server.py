import argparse
import atexit
from enum import Enum
from flask import Flask, request, jsonify
from furl import furl
import multiprocessing as mp
from video import load, process_by_frames
import os
import requests
import uuid
import evaluate
from model import Model
from exceptions import RetryableException

app = Flask(__name__, static_url_path="")

n_proc = 1
processes = []
queue = mp.Queue()
barrier = mp.Barrier(n_proc + 1)


class Status(Enum):
    NOT_PROCESSED = "NOT_PROCESSED"
    PROCESSED = "PROCESSED"


def process(queue, server_url):
    model = Model()
    meteor = evaluate.load('meteor')
    status_url = str(furl(server_url) / "status")
    upload_url = str(furl(server_url) / "upload")

    def callback(timestamp, caption):
        print(f"{timestamp}: {caption}")
        #requests.post(upload_url, json={'url': url, 'start': timestamp, 'sentence': caption})

    print("All files downloaded")
    barrier.wait()

    while pair := queue.get():
        try:
            video_file = str(uuid.uuid4())
            subtitles_file = str(uuid.uuid4())

            url, srt = pair

            print("Loading video...")
            load(url, video_file)
            print("Loading subtitles...")
            if len(srt) > 0:
                load(srt, subtitles_file)
            else:
                subtitles_file = None

            process_by_frames(video_file, callback=callback, predict=model.predict_caption, metric=meteor, subtitle_path=subtitles_file)

            os.remove(video_file)
            if subtitles_file is not None:
                os.remove(subtitles_file)
        except RetryableException as retr:
            error_msg = str(retr)
            #requests.post(status_url, json={'url': url, 'processing': Status.NOT_PROCESSED.value})
        except Exception as e:
            error_msg = str(e)
            #requests.post(status_url, json={'url': url, 'processing': Status.NOT_PROCESSED.value})
        else:
            pass
            #requests.post(status_url, json={'url': url, 'processing': Status.PROCESSED.value})


@app.route("/process", methods=['POST'])
def handle_request():
    data = request.get_json(force=True)
    url = data['url']
    srt = data['srturl']

    queue.put((url, srt))
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
