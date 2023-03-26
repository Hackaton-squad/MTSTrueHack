import json
import atexit
import torch
import time
from flask import Flask, request, jsonify
import multiprocessing as mp
from multiprocessing import current_process
from video import load_video, process_by_frames
import os
import uuid
from model import Model, Model2

app = Flask(__name__, static_url_path="")
n_proc = 2
processes = []
queue = mp.Queue()
barrier = mp.Barrier(n_proc + 1)

# Check that process is running
# back_queue = mp.Queue()


def callback(timestamp, caption):
    # TODO - request to backend
    print(f"Timestamp: {timestamp}, caption: {caption}")


def process(queue):
    time.sleep(5)
    barrier.wait()
    model = Model()

    while url := queue.get():
        time.sleep(5)

        filename = str(uuid.uuid4())

        load_video(url, filename)
        process_by_frames(filename, callback=callback, predict=model.predict_caption)
        os.remove(filename)

        # convert video to images
        # call model
        # request to backend

        # Check that process is running
        # print(current_process().name, url)
        # back_queue.put(current_process().name + url)


@app.route("/process", methods=['POST'])
def handle_request():
    data = request.get_json(force=True)
    url = data['url']

    queue.put(url)
    # Check that process is running
    # val = back_queue.get()
    # return jsonify(val)
    return jsonify(success=True)


def close_running_processes():
    for p in processes:
        p.join()
    print("Processes complete, finishing...")


if __name__ == '__main__':
    atexit.register(close_running_processes)
    for _ in range(n_proc):
        print("Starting proc...")
        proc = mp.Process(target=process, args=[queue])
        processes.append(proc)
        proc.start()
    barrier.wait()
    app.run(host='0.0.0.0', port=8081)
