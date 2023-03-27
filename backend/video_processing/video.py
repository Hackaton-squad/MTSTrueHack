import requests
import cv2
from PIL import Image
import pysrt

INTERVAL_IN_SECONDS = 20
SUBTITLE_GAP_SECONDS = 1


def load_video(link: str, path: str):
    r = requests.get(link, stream=True)

    with open(path, 'wb') as f:
        for chunk in r.iter_content(chunk_size=1024 * 1024):
            if chunk:
                f.write(chunk)


def process_by_frames(path: str, callback, predict, subtitle_path=None):
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
    while True:
        ret, frame = cam.read()
        if ret:
            if currentframe % (int(fps) * INTERVAL_IN_SECONDS) == 0:
                img = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                image = Image.fromarray(img)
                if image.mode != "RGB":
                    image = image.convert(mode="RGB")

                timestamp = int(currentframe / fps)
                if belongs_to_interval(timestamp, intervals):
                    continue

                caption = predict(image)
                callback(timestamp, caption)

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
        timestamps.append([start - SUBTITLE_GAP_SECONDS, end + SUBTITLE_GAP_SECONDS])

