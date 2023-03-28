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
    ;

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
    let video_player = document.getElementById("video");
    let audio_player = document.getElementById("audio");

    document.getElementById('get_video_button').addEventListener('click', function (e) {
        fetch('/getAudios?url=' + input.value).then(response => response.json()).then(response => {
            video_player.setAttribute('src', input.value);
            let audios = response.audios;
            video_player.addEventListener('timeupdate', function () {
                audios.forEach(audio => {
                    if (this.currentTime > audio.start && this.currentTime < audio.start + 0.250) {
                        audio_player.pause();
                        audio_player.setAttribute('src', '/playAudio?start=' + audio.start);
                        audio_player.load();
                        audio_player.play();
                    }
                });
                document.getElementById("timer").innerHTML = this.currentTime;
            });
            console.log(response.audios[0]);
        })
    });
});
