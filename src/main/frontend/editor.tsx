import React from 'react';
import { createRoot } from 'react-dom/client';
import { setup } from 'goober';
import { App } from './App';

import { patchCoralUiCreateElement } from './coral/patchCoralUiCreateElement';

setup(React.createElement);

patchCoralUiCreateElement();

function init() {
	const target = document.querySelector<HTMLElement>('.jcr-hopper-builder')!;
	const { runEndpoint, validScriptingLanguages, sampleScripts } = target.dataset;
	const root = createRoot(target.parentElement!.parentElement!);
	root.render(
		<React.StrictMode>
			<App
				runEndpoint={runEndpoint!}
				validScriptingLanguages={JSON.parse(validScriptingLanguages || '{}')}
				sampleScripts={JSON.parse(sampleScripts || '[]')}
			></App>
		</React.StrictMode>,
	);
}

if (document.readyState === 'interactive') {
	init();
} else {
	document.addEventListener('DOMContentLoaded', init);
}
