//Create express server
const express = require('express'); //Import the express dependency
const app = express();              //Instantiate an express app, the main work horse of this server
const port = 5000;                  //Save the port number where your server will be listening
const {pipeline} = require('node:stream/promises');

//Create media-server
const ms = require('mediaserver');

//Create node-fetch variables to do requests to different APIs
const fetch = require('node-fetch');
const api_key = 'AQVN1Bkw_G0XP1HC1DKCj9mBNBPGubffIsFINtEM';

const {URLSearchParams} = require('url');
const fs = require('fs');
const load = require("audio-loader");
const play = require("audio-play");

var getAudio = function textToSpeech(audio) { // sample async action
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
            'Authorization': 'Api-Key ' + api_key,
        },
    })
        .then(result => {
            const dest = fs.createWriteStream('./public/media/' + audio.start + '.mp3');
            return pipeline(result.body, dest);
        })
        .catch(err => console.error(err));
};

app.use(express.static("public"));
app.use(express.urlencoded({extended: true}));

//Idiomatic expression in express to route and respond to a client request
app.get('/', (req, res) => {        //get requests to the root ("/") will route here
    //server responds by sending the index.html file to the client's browser
    return res.redirect("index.html");                                              //the .sendFile method needs the absolute path to the file, see: https://expressjs.com/en/4x/api.html#res.sendFile
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
    let audio_path = 'media/' + audio_start_time + '.mp3';
    //load('./public/media/' + audio_start_time + '.mp3').then(play);
    res.json({'audio' : audio_path});
});

app.listen(port, () => {            //server starts listening for any attempts from a client to connect at port: {port}
    console.log(`Now listening on port ${port}`);
});