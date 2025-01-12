let deferredPrompt;

self.addEventListener('install', (event) => {
    console.log('Service Worker installed.');
});

self.addEventListener('fetch', (event) => {
    // Simply fetch from the network
    event.respondWith(fetch(event.request));
});

window.addEventListener('beforeinstallprompt', (e) => {
    console.log('beforeinstallprompt event triggered');
    // Prevent the default prompt from appearing immediately
    e.preventDefault();
    deferredPrompt = e;

    // Optionally, show a custom install button
    const installButton = document.createElement('button');
    installButton.textContent = 'Install TheBox App';
    document.body.appendChild(installButton);

    installButton.addEventListener('click', () => {
        deferredPrompt.prompt();
        deferredPrompt.userChoice.then((choiceResult) => {
            if (choiceResult.outcome === 'accepted') {
                console.log('User accepted the install prompt');
            } else {
                console.log('User dismissed the install prompt');
            }
        });
    });
});