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

var getAudio = function textToSpeech(audio) {
    const params = new URLSearchParams();

    params.append('text', audio.text);
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
            // console.log(result);
            const dest = fs.createWriteStream('./public/media/' + Math.floor(audio.start / 1000) + '.mp3');
            return pipeline(result.body, dest);
        })
        .catch(err => console.error(err));
};

app.use(express.static("public"));
app.use(express.urlencoded({extended: true}));

app.get('/', (req, res) => {
    return res.redirect("index.html");
});

app.get('/startProcessVideo', (req, res) => {
    const params = new URLSearchParams();
    // params.append('url', req.query.url);
    // params.append('srturl', req.query.suburl);
    // params.append('hard', true);

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

        Promise.all(response.audios.map(getAudio))
            .then(result => {
                res.json(response)
            })
            .catch(err => console.error(err));
    });
});


app.get('/getAudios', (req, res) => {
    //Для нашего апи
    // const params = new URLSearchParams();
    // params.append('url', req.query.url);
    //
    // fetch('api', {
    //     method: 'get',
    //     body: params,
    //     headers: {
    //         // 'Content-Type': 'application/x-www-form-urlencoded',
    //         'Authorization': 'Api-Key ' + api_key,
    //     },
    // });

    let url = req.query.url;

    var timestamps = require('./timestamps.json')
    console.log(timestamps);

    Promise.all(timestamps.audios.map(getAudio))
        .then(result => {
            res.json(timestamps)
        })
        .catch(err => console.error(err));
});

app.get('/playAudio', (req, res) => {
    let audio_start_time = req.query.start;
    let audio_path = './public/media/' + audio_start_time + '.mp3';

    const rs = fs.createReadStream(audio_path);
    const { size } = fs.statSync(audio_path);

    res.setHeader("Content-Type", "audio/mpeg");
    res.setHeader("Content-Length", size);

    rs.pipe(res);
});

app.listen(PORT, () => {
    console.log(`Now listening on port ${PORT}`);
});