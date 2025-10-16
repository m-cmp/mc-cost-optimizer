import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App.jsx";
import "./index.css";

// Tabler CSS / JS (global styles)
import "@tabler/core/dist/css/tabler.min.css";
import "@tabler/core/dist/js/tabler.min.js";

// Save postMessage before React app starts
window.__INITIAL_POST_MESSAGE__ = null;

window.addEventListener("message", (event) => {
  if (event.data && event.data.accessToken) {
    console.log("✅ [Global] Initial message saved successfully", event.data);
    window.__INITIAL_POST_MESSAGE__ = event.data;
  }
});

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <App />
  </StrictMode>
);
