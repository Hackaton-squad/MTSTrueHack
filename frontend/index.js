const express = require('express'); //Import the express dependency
const app = express();              //Instantiate an express app, the main work horse of this server
const port = 5000;                  //Save the port number where your server will be listening


if (typeof document !== 'undefined') {
    document.getElementById('text_to_speech_button').onclick = function () {
        const params = new URLSearchParams();
        const text = 'Добрый день, у нас новая акция! Пицца Добряк сегодня за полцены, закажите по телефону 222. Уже ждем вашего заказа :) '

        params.append('text', text);
        params.append('voice', 'zahar');
        params.append('emotion', 'good');
        params.append('lang', 'ru-RU');
        params.append('speed', '1.0');
        params.append('format', 'oggopus');

        fetch('https://tts.api.cloud.yandex.net/speech/v1/tts:synthesize', {
            method: 'post',
            body: params,
            headers: {
                // 'Content-Type': 'application/x-www-form-urlencoded',
                'Authorization': 'Api-Key ' + api_key,
            },
        })
            .then(res => {
                console.log(res);
                // return res.json();
                const dest = fs.createWriteStream('./octocat.ogg');
                res.body.pipe(dest);
            })
            .catch(err => console.error(err));

    };
}

//Idiomatic expression in express to route and respond to a client request
app.get('/', (req, res) => {        //get requests to the root ("/") will route here
    //server responds by sending the index.html file to the client's browser
    res.sendFile('index.html', {root: __dirname});                                                 //the .sendFile method needs the absolute path to the file, see: https://expressjs.com/en/4x/api.html#res.sendFile
});

app.listen(port, () => {            //server starts listening for any attempts from a client to connect at port: {port}
    console.log(`Now listening on port ${port}`);
});