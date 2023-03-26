import requests
import cv2
from PIL import Image

INTERVAL_IN_SECONDS = 5


def load_video(link, path):
    # create response object 
    r = requests.get(link, stream=True)

    # download started 
    with open(path, 'wb') as f:
        for chunk in r.iter_content(chunk_size=1024 * 1024):
            if chunk:
                f.write(chunk)


def process_by_frames(path, callback, predict):
    cam = cv2.VideoCapture(path)

    (major_ver, minor_ver, subminor_ver) = cv2.__version__.split('.')

    if int(major_ver) < 3:
        fps = cam.get(cv2.cv.CV_CAP_PROP_FPS)
    else:
        fps = cam.get(cv2.CAP_PROP_FPS)

    currentframe = 0
    while True:
        ret, frame = cam.read()
        if ret:
            if currentframe % (int(fps) * INTERVAL_IN_SECONDS) == 0:
                img = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                image = Image.fromarray(img)
                if image.mode != "RGB":
                    image = image.convert(mode="RGB")

                timestamp = currentframe / fps

                caption = predict(image)
                callback(timestamp, caption)

            currentframe += 1
        else:
            break

    cam.release()
    cv2.destroyAllWindows()
