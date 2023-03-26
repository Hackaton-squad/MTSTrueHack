//Create express server
const express = require('express'); //Import the express dependency
const app = express();              //Instantiate an express app, the main work horse of this server
const port = 5000;                  //Save the port number where your server will be listening

//Create media-server
const ms = require('mediaserver');

//Create node-fetch variables to do requests to different APIs
const fetch = require('node-fetch');
const api_key = 'AQVN1Bkw_G0XP1HC1DKCj9mBNBPGubffIsFINtEM';

const {URLSearchParams} = require('url');
const fs = require('fs');
const load = require("audio-loader");
const play = require("audio-play");

//Idiomatic expression in express to route and respond to a client request
app.get('/', (req, res) => {        //get requests to the root ("/") will route here
    //server responds by sending the index.html file to the client's browser
    res.sendFile('index.html', {root: __dirname});                                                 //the .sendFile method needs the absolute path to the file, see: https://expressjs.com/en/4x/api.html#res.sendFile
});

app.get('/getAudio', (req, res) => {
    const params = new URLSearchParams();
    const text = 'Вам посылка от кота Матроскина :) '

    params.append('text', text);
    params.append('voice', 'zahar');
    params.append('emotion', 'good');
    params.append('lang', 'ru-RU');
    params.append('speed', '1.0');
    params.append('format', 'mp3');

    fetch('https://tts.api.cloud.yandex.net/speech/v1/tts:synthesize', {
        method: 'post',
        body: params,
        headers: {
            // 'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': 'Api-Key ' + api_key,
        },
    })
        .then(result => {
            console.log(result);
            res.send(JSON.stringify({created: 'success'}))
            // return res.json();
            const dest = fs.createWriteStream('./octocat.mp3');
            const r = result.body.pipe(dest);
            r.on('finish', function () {
                const play = require('audio-play');
                const load = require('audio-loader');

                load('./octocat.mp3').then(play);
            });
        })
        .catch(err => console.error(err));
});

app.get('/playAudio', (req, res) => {
    ms.pipe(req, res, "octocat.ogg");
});

app.listen(port, () => {            //server starts listening for any attempts from a client to connect at port: {port}
    console.log(`Now listening on port ${port}`);
});