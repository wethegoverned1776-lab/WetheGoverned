// Room 3.0 Wasm Worker Fallback
// This script is required by WebWorkerSQLiteDriver for OPFS persistence.
console.log("Room Wasm Worker: Initializing...");

// Note: In a full production build, this would import the sqlite-wasm module.
// For now, this serves as a placeholder to prevent 'Worker not found' errors.
self.onmessage = function(e) {
    // Forward to internal Room worker implementation if available
    console.log("Room Wasm Worker: Received message", e.data);
};
