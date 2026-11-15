// ResizeHub service worker - ads paused for AdSense review (2026-11-16).
// Unregisters the old Monetag push worker for returning visitors.
// Restore the original sw.js from files/ads-paused-backup-2026-11-16/ after AdSense approval.
self.addEventListener('install', () => self.skipWaiting());
self.addEventListener('activate', (event) => {
  event.waitUntil(self.registration.unregister());
});
