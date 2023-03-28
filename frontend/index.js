const express = require('express');
const config = require('dotenv').config()
const app = express();

const PORT = process.env.PORT;
const YANDEX_TTS_API_KEY = process.env.YANDEX_TTS_API_KEY;

const fetch = require('node-fetch');
const {URLSearchParams} = require('url');
const fs = require('fs');
const {pipeline} = require('stream/promises');

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
            console.log(result);
            const dest = fs.createWriteStream('./public/media/' + audio.start + '.mp3');
            return pipeline(result.body, dest);
        })
        .catch(err => console.error(err));
};

app.use(express.static("public"));
app.use(express.urlencoded({extended: true}));

app.get('/', (req, res) => {
    return res.redirect("index.html");
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