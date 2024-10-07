import React from 'react';
import { createRoot } from 'react-dom/client';
import { setup } from 'goober';
import { App } from './App';

setup(React.createElement);

function init() {
	const target = document.querySelector<HTMLElement>('.jcr-hopper-builder')!;
	const { runEndpoint } = target.dataset;
	const root = createRoot(target.parentElement!.parentElement!);
	root.render(
		<React.StrictMode>
			<App runEndpoint={runEndpoint!}></App>
		</React.StrictMode>,
	);
}

if (document.readyState === 'interactive') {
	init();
} else {
	document.addEventListener('DOMContentLoaded', init);
}
