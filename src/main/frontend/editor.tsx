import React from "react";
import { createRoot } from "react-dom/client";
import { setup } from "goober";
import { App } from "./App";

setup(React.createElement);

function init() {
	const target = document.querySelector<HTMLElement>(".jcr-hopper-builder")!;
	const { runEndpoint } = target.dataset;
	const root = createRoot(target.parentElement!.parentElement);
	root.render(<App runEndpoint={runEndpoint}></App>);
}

if (document.readyState === "interactive") {
	init();
} else {
	document.addEventListener("DOMContentLoaded", init);
}
