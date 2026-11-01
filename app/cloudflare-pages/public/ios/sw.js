const CACHE = "buct-ios-v2";
const ASSETS = ["/ios/","/ios/app.css","/ios/app.js","/ios/icon-180.png","/ios/manifest.webmanifest"];
self.addEventListener("install", event => event.waitUntil(caches.open(CACHE).then(cache => cache.addAll(ASSETS)).then(() => self.skipWaiting())));
self.addEventListener("activate", event => event.waitUntil(caches.keys().then(keys => Promise.all(keys.filter(k => k.startsWith("buct-ios-") && k !== CACHE).map(k => caches.delete(k)))).then(() => self.clients.claim())));
self.addEventListener("fetch", event => {
  if (event.request.method !== "GET" || !new URL(event.request.url).pathname.startsWith("/ios/")) return;
  event.respondWith(caches.match(event.request, {ignoreSearch:true}).then(cached => cached || fetch(event.request)));
});