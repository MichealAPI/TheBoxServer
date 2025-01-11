const joinFullScreenBtn = document.getElementById('joinFullscreen');
const exitFullScreenBtn = document.getElementById('exitFullscreen');
const fullscreenElement = document.getElementById('fullscreen');
const imageElement = document.getElementById('fullscreen-image');

let isFullscreen = false;

joinFullScreenBtn.addEventListener('click', () => {

    imageElement.style.backgroundImage = `url(${joinFullScreenBtn.getAttribute('data-image')})`;
    toggleFullScreen();

});

exitFullScreenBtn.addEventListener('click', () => {
    toggleFullScreen();
});

function toggleFullScreen() {

    fullscreenElement.classList.toggle('d-none');
    isFullscreen = !isFullscreen;

}

/* Handle the Escape key */
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && isFullscreen) {
        toggleFullScreen();
    }
});

/* Handle clicking in the fullscreen area, not over the image */
fullscreenElement.addEventListener('click', () => {
    if (isFullscreen) {
        toggleFullScreen();
    }
});