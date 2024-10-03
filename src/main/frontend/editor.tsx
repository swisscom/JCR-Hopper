import React from "react";
import { createRoot } from "react-dom/client";
import { setup } from "goober";

setup(React.createElement);

function init() {
  const root = createRoot(document.querySelector(".jcr-hopper-builder")!);
  root.render(<React.StrictMode></React.StrictMode>);
}

if(document.readyState === 'interactive') {
  init();
} else {
  document.addEventListener('DOMContentLoaded', init);
}
