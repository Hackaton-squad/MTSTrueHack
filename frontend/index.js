const express = require('express');
const config = require('dotenv').config()
const app = express();

const PORT = process.env.PORT;
const YANDEX_TTS_API_KEY = process.env.YANDEX_TTS_API_KEY;
const TRUE_BACKEND_URL = process.env.TRUE_BACKEND_URL;

const fetch = require('node-fetch');
const {URLSearchParams} = require('url');
const fs = require('fs');
const {pipeline} = require('stream/promises');
const {json} = require("express");

var getAudio = function textToSpeech(audioStartTime, audioText, res) {
    const params = new URLSearchParams();

    params.append('text', audioText);
    params.append('voice', 'zahar');
    params.append('emotion', 'good');
    params.append('lang', 'ru-RU');
    params.append('speed', '1.0');
    params.append('format', 'mp3');

    return fetch('https://tts.api.cloud.yandex.net/speech/v1/tts:synthesize', {
        method: 'post',
        body: params,
        headers: {
            // 'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': 'Api-Key ' + YANDEX_TTS_API_KEY,
        },
    })
        .then(result => {
            const audio_path = './public/media/' + Math.floor(audioStartTime / 1000) + '.mp3';
            const dest = fs.createWriteStream(audio_path);
            const r = result.body.pipe(dest);
            r.on('finish', function () {
                const rs = fs.createReadStream(audio_path);
                const { size } = fs.statSync(audio_path);

                res.setHeader("Content-Type", "audio/mpeg");
                res.setHeader("Content-Length", size);

                rs.pipe(res);
            });
        })
        .catch(err => console.error(err));
};

app.use(express.static("public"));
app.use(express.urlencoded({extended: true}));

app.get('/', (req, res) => {
    return res.redirect("index.html");
});

app.get('/startProcessVideo', (req, res) => {

    fetch(TRUE_BACKEND_URL +'/process', {
        method: 'post',
        body: JSON.stringify({
            'url' : req.query.url,
            'srturl': req.query.suburl,
            'hard': false,
        }),
        headers: {
            'Content-Type': 'application/json',
        },
    }).then(() => {
        console.log("Послали")
        res.json({});
    });
});

app.get('/loadAudio', (req, res) => {

    fetch(TRUE_BACKEND_URL + '/download?url=' + req.query.url + "&start=" + req.query.start + "&end=" + req.query.end + "&lang=RU", {
        method: 'get',
        // params: params,
    }).then(response => response.json()).then(response => {
        console.log("Response:", response)

        // Promise.all(response.audios.map(getAudio))
        //     .then(result => {
        //         res.json(response)
        //     })
        //     .catch(err => console.error(err));
        //var timestamps = require('./timestamps.json');
        res.json(response);
    });
});

app.get('/playAudio', (req, res) => {
    let audio_start_time = req.query.start;
    let audio_text = req.query.text;

    getAudio(audio_start_time, audio_text, res);
});

app.listen(PORT, () => {
    console.log(`Now listening on port ${PORT}`);
});