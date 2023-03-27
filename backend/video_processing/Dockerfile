FROM python:3.8

COPY requirements.txt .
RUN pip3 install -r requirements.txt

RUN mkdir /app
WORKDIR /app

COPY server.py .
COPY exceptions.py .
COPY video.py .
#COPY vgg16.pt .

#ENTRYPOINT ["python3", "server.py"]
COPY supervisord.conf /etc/supervisord.conf

ENTRYPOINT ["supervisord", "-c", "/etc/supervisord.conf"]