import cv2
import requests
from PIL import Image
import pysrt
from model import PROMPT
from exceptions import RetryableException


INTERVAL_IN_SECONDS = 10
MAX_INTERVAL_IN_SECONDS = 70
SUBTITLE_GAP_SECONDS_START = 1.0
SUBTITLE_GAP_SECONDS_END = 0.8
SUBTITLE_GAP_SECONDS_LEN = 0.5
METRIC_THRESHOLD = 0.8


def convert_timestamp_to_milliseconds(timestamp):
    return int(timestamp * 1000)


def load_video(link: str, path: str):
    try:
        r = requests.get(link, stream=True)

        with open(path, 'wb') as f:
            for chunk in r.iter_content(chunk_size=1024 * 1024):
                if chunk:
                    f.write(chunk)
    except Exception as exc:
        raise RetryableException("Error while loading video") from exc


def process_by_frames(path: str, callback, predict, metric, subtitle_path=None):
    cam = cv2.VideoCapture(path)

    (major_ver, minor_ver, subminor_ver) = cv2.__version__.split('.')

    if int(major_ver) < 3:
        fps = cam.get(cv2.cv.CV_CAP_PROP_FPS)
    else:
        fps = cam.get(cv2.CAP_PROP_FPS)

    intervals = []
    if subtitle_path is not None:
        intervals = subtitle_intervals(subtitle_path)

    currentframe = 0
    previousframe = 0
    previouscaption = ""
    while True:
        ret, frame = cam.read()
        if ret:

            if currentframe - previousframe > INTERVAL_IN_SECONDS * int(fps):
                img = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                image = Image.fromarray(img)
                if image.mode != "RGB":
                    image = image.convert(mode="RGB")

                timestamp = int(currentframe / fps) - SUBTITLE_GAP_SECONDS_LEN

                if belongs_to_interval(timestamp, intervals):
                    currentframe += 1
                    continue

                caption = predict(image)
                if len(caption) > 0:
                    caption = caption[0]
                if len(caption) > len(PROMPT):
                    caption = caption[len(PROMPT):]
                else:
                    currentframe += 1
                    continue

                if currentframe - previousframe < MAX_INTERVAL_IN_SECONDS * int(fps):
                    if metric.compute(predictions=[caption], references=[previouscaption])['meteor'] > METRIC_THRESHOLD:
                        currentframe += 1
                        continue

                callback(convert_timestamp_to_milliseconds(timestamp), caption)
                previousframe = currentframe
                previouscaption = caption

            currentframe += 1
        else:
            break

    cam.release()
    cv2.destroyAllWindows()


def belongs_to_interval(timestamp: int, intervals: [int, int]) -> bool:
    while len(intervals) > 0:
        start, end = intervals[0]
        if timestamp < start:
            return False
        if timestamp <= end:
            return True
        intervals = intervals[1:]
    return False


def subtitle_intervals(subtitle_path: str) -> [[int, int]]:
    subs = pysrt.open(subtitle_path)

    def to_second(t):
        seconds = (t.hour * 60 + t.minute) * 60 + t.second
        return seconds

    timestamps = []

    for sub in subs:
        start = to_second(sub.start.to_time())
        end = to_second(sub.end.to_time())
        timestamps.append([start - SUBTITLE_GAP_SECONDS_START, end + SUBTITLE_GAP_SECONDS_END])
    return timestamps
