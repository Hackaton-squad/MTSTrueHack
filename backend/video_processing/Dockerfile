FROM python:3.8

COPY requirements.txt .
RUN pip3 install -r requirements.txt


RUN mkdir /app
WORKDIR /app

# for loading big model
# COPY download.sh .
# RUN bash download.sh

RUN apt-get update && apt-get install -y python3-opencv

COPY server.py .
COPY exceptions.py .
COPY video.py .
COPY model.py .

#ENTRYPOINT ["python3", "server.py"]
COPY supervisord.conf /etc/supervisord.conf

ENTRYPOINT ["supervisord", "-c", "/etc/supervisord.conf"]