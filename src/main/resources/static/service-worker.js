self.addEventListener('install', (event) => {
    console.log('Service Worker installed.');
});

self.addEventListener('fetch', (event) => {
    // Simply fetch from the network
    event.respondWith(fetch(event.request));
});