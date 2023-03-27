import argparse

import requests
from furl import furl


def main_single(vid_path, server_url):
    predict_url = str(furl(server_url) / "process")
    r = requests.post(predict_url, json={'url': vid_path})
    print(r.status_code)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("vid", help="path to video")
    parser.add_argument("--url", help="url to server", default="http://localhost:8081")
    args = parser.parse_args()

    main_single(args.vid, args.url)