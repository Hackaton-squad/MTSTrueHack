import json
import atexit
import torch
import time
from flask import Flask, request, jsonify
import multiprocessing as mp
from multiprocessing import current_process


app = Flask(__name__, static_url_path="")
queue = mp.Queue()

# Check that process is running
# back_queue = mp.Queue()

def process(queue):
    while url := queue.get():
        time.sleep(5)
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


n_proc = 2
processes = []

if __name__ == '__main__':
    atexit.register(close_running_processes)
    for _ in range(n_proc):
        print("Starting proc...")
        proc = mp.Process(target=process, args=[queue])
        processes.append(proc)
        proc.start()
    app.run(host='0.0.0.0', port=8081)

