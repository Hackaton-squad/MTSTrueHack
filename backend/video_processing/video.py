import cv2
from PIL import Image
import pysrt
from model import PROMPT


INTERVAL_IN_SECONDS = 10
MAX_INTERVAL_IN_SECONDS = 70
SUBTITLE_GAP_SECONDS_START = 1.0
SUBTITLE_GAP_SECONDS_END = 0.5
SUBTITLE_GAP_SECONDS_LEN = 0.5


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
    previousframe = 0
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
                    #  and currentframe - previousframe < MAX_INTERVAL_IN_SECONDS * int(fps):
                    currentframe += 1
                    continue

                caption = predict(image)
                if len(caption) > 0:
                    caption = caption[0]
                if len(caption) > len(PROMPT):
                    callback(timestamp, caption[len(PROMPT):])
                previousframe = currentframe

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
