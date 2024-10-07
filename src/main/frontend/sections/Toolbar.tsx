import React, { FC, useContext } from 'react';

import { styled } from 'goober';
import { ScriptContext } from '../App';
import { INITIAL_SCRIPT } from '../model/Script';

const Elm = styled('div')`
	display: flex;
`;

export const Toolbar: FC = () => {
	const scriptContext = useContext(ScriptContext);

	console.log('Renderin toolbar with', [scriptContext.canUndo, scriptContext.canRedo]);

	return (
		<Elm className="toolbar">
			<button
				is="coral-button"
				icon="copy"
				onClick={() => {
					navigator.clipboard.writeText(JSON.stringify(scriptContext.current));
				}}
			>
				Copy
			</button>
			<button
				is="coral-button"
				icon="paste"
				onClick={async () => {
					try {
						const json = await navigator.clipboard.readText();
						const state = JSON.parse(json);
						// Sanity check
						if (typeof state !== 'object') {
							throw new Error(`Pasted JSON is not an object: ${json}`);
						}
						scriptContext.replace({ ...INITIAL_SCRIPT, ...state });
					} catch (e) {
						console.error('Could not paste script', e);
					}
				}}
			>
				Paste
			</button>
			<span className="flex-spacer"></span>
			<button is="coral-button" icon="undo" disabled={scriptContext.canUndo ? undefined : true} onClick={scriptContext.undo}>
				Undo
			</button>
			<button is="coral-button" icon="redo" disabled={scriptContext.canRedo ? undefined : true} onClick={scriptContext.redo}>
				Redo
			</button>
		</Elm>
	);
};
