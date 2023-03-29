/*!
* Start Bootstrap - Freelancer v7.0.6 (https://startbootstrap.com/theme/freelancer)
* Copyright 2013-2022 Start Bootstrap
* Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-freelancer/blob/master/LICENSE)
*/
//
// Scripts
// 

window.addEventListener('DOMContentLoaded', event => {

    // Navbar shrink function
    var navbarShrink = function () {
        const navbarCollapsible = document.body.querySelector('#mainNav');
        if (!navbarCollapsible) {
            return;
        }
        if (window.scrollY === 0) {
            navbarCollapsible.classList.remove('navbar-shrink')
        } else {
            navbarCollapsible.classList.add('navbar-shrink')
        }

    };

    // Shrink the navbar 
    navbarShrink();

    // Shrink the navbar when page is scrolled
    document.addEventListener('scroll', navbarShrink);

    // Activate Bootstrap scrollspy on the main nav element
    const mainNav = document.body.querySelector('#mainNav');
    if (mainNav) {
        new bootstrap.ScrollSpy(document.body, {
            target: '#mainNav',
            offset: 72,
        });
    }

    // Collapse responsive navbar when toggler is visible
    const navbarToggler = document.body.querySelector('.navbar-toggler');
    const responsiveNavItems = [].slice.call(
        document.querySelectorAll('#navbarResponsive .nav-link')
    );
    responsiveNavItems.map(function (responsiveNavItem) {
        responsiveNavItem.addEventListener('click', () => {
            if (window.getComputedStyle(navbarToggler).display !== 'none') {
                navbarToggler.click();
            }
        });
    });

    let input = document.getElementById('input_url');
    let input_sub = document.getElementById('input_url_sub');
    let videoPlayer = document.getElementById("video");
    let audioPlayer = document.getElementById("audio");
    let filmsUrls = [
        'https://storage.yandexcloud.net/mts-hackaton-backet/How%20I%20met%20your%20mother/S1SE1/How.I.Met.Your.Mother.s1.e01.Rus.%5BKuraj-Bambey.Ru%5D.mp4',
        'https://storage.yandexcloud.net/mts-hackaton-backet/How%20I%20met%20your%20mother/S1SE2/How.I.Met.Your.Mother.s1.e02.Rus.%5BKuraj-Bambey.Ru%5D.mp4',
        'https://storage.yandexcloud.net/mts-hackaton-backet/How%20I%20met%20your%20mother/S1SE3/How.I.Met.Your.Mother.s1.e03.Rus.%5BKuraj-Bambey.Ru%5D.mp4',
        'https://storage.yandexcloud.net/mts-hackaton-backet/How%20I%20met%20your%20mother/S1SE4/How.I.Met.Your.Mother.s1.e04.Rus.%5BKuraj-Bambey.Ru%5D.mp4',
        'https://storage.yandexcloud.net/mts-hackaton-backet/How%20I%20met%20your%20mother/S1SE4/How.I.Met.Your.Mother.s1.e04.Rus.%5BKuraj-Bambey.Ru%5D.mp4',
        'https://storage.yandexcloud.net/mts-hackaton-backet/How%20I%20met%20your%20mother/S1SE6/How.I.Met.Your.Mother.s1.e06.Rus.%5BKuraj-Bambey.Ru%5D.mp4'
    ];
    let filmsSubs = [
        'https://drive.google.com/uc?export=download&id=1vr_sz3WKSOQVyuqjpCWjt6lrOUYdMh3M',
        'https://drive.google.com/uc?export=download&id=1h6Udpyb6cFfUxceo1Uq4g1yoWom9hliM',
        'https://drive.google.com/uc?export=download&id=18hbklSUUfVo0GvYTW_Bqeze7hb-lJKUT',
        'https://drive.google.com/uc?export=download&id=1wqAkp0D3gQ45oMSqbNrhcnx2nLpAssJr',
        'https://drive.google.com/uc?export=download&id=1l7L66SQB-GkKj4lYGtgMT5vWdf9m6gDd',
        'https://drive.google.com/uc?export=download&id=18-k2veEpbE-ETRTMWbUYN_PEgXMKHgwe'
    ];
    let getAudios = function getAudios(url, suburl) {
        fetch('/startProcessVideo?url=' + url + "&suburl=" + suburl)
            .then(() => {
                fetch('/loadAudio?url=' + input.value + "&start=" + 0 + "&end=" + (-1))
                    .then(response => response.json())
                    .then(response => {
                            videoPlayer.setAttribute('src', input.value);
                            let audios = response.audios;
                            console.log(response.audios[0]);
                            videoPlayer.audios = audios
                            videoPlayer.counter = 0
                            videoPlayer.addEventListener('timeupdate', function (evt) {
                                // if ((Math.ceil(this.currentTime * 4) % 10) === 0) {
                                //     console.log("Query")
                                //     fetch('/loadAudio?url=' + input.value + "&start=" + 0 + "&end=" + (-1)).then(response => response.json()).then(
                                //         response => {
                                //             evt.currentTarget.audios = response.audios;
                                //         }
                                //     );
                                // }

                                evt.currentTarget.counter += 1
                                evt.currentTarget.audios.forEach(audio => {

                                    // console.log(Math.floor(this.currentTime * 100))
                                    // console.log(Math.floor(audio.start / 1000) * 100)
                                    // console.log(Math.floor(audio.start / 1000) * 100 + 25)
                                    // console.log(Math.floor(this.currentTime * 100) >= Math.floor(audio.start / 1000) * 100)
                                    // console.log(Math.floor(this.currentTime * 100) <= Math.floor(audio.start / 1000) * 100 + 25)
                                    // console.log("")

                                    let a = Math.floor(this.currentTime)
                                    console.log(a)
                                    console.log(Math.floor(audio.start / 1000))
                                    console.log(a === Math.floor(audio.start / 1000))

                                    if (a === Math.floor(audio.start / 1000)) {
                                        audioPlayer.pause();
                                        audioPlayer.setAttribute('src', '/playAudio?start=' + audio.start);
                                        audioPlayer.load();
                                        audioPlayer.play();
                                    }

                                    // if (Math.floor(this.currentTime * 100) >= Math.floor(audio.start / 1000) * 100 && Math.floor(this.currentTime * 100) <=Math.floor(audio.start / 1000) * 100 + 25) {
                                    //
                                    // }
                                });
                                document.getElementById("timer").innerHTML = this.currentTime;
                            });
                            console.log(response.audios[0]);
                        }
                    )
            });
    };

    document.getElementById('get_video_button').addEventListener('click', function (e) {
        getAudios(input.value, input_sub.value)
    });

    document.getElementById('portfolio-btn-1').addEventListener('click', function (e) {
        getAudios(filmsUrls[0], filmsSubs[0])
    });

    document.getElementById('portfolio-btn-2').addEventListener('click', function (e) {
        getAudios(filmsUrls[1], filmsSubs[1])
    });

    document.getElementById('portfolio-btn-3').addEventListener('click', function (e) {
        getAudios(filmsUrls[2], filmsSubs[2])
    });

    document.getElementById('portfolio-btn-4').addEventListener('click', function (e) {
        getAudios(filmsUrls[3], filmsSubs[3])
    });

    document.getElementById('portfolio-btn-5').addEventListener('click', function (e) {
        getAudios(filmsUrls[4], filmsSubs[4])
    });

    document.getElementById('portfolio-btn-6').addEventListener('click', function (e) {
        getAudios(filmsUrls[5], filmsSubs[5])
    });

    let filmsButtons = document.querySelectorAll('portfolio-item');

    filmsButtons.forEach((filmButton, index) => {
        filmButton.addEventListener('click', function (e) {
            getAudios(filmsUrls[index], filmsSubs[index]);
        });
    });
});
