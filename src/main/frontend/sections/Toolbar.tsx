import React, { FC, useContext } from 'react';

import { styled } from 'goober';
import { ScriptContext } from '../App';
import { INITIAL_SCRIPT } from '../model/Script';
import { Picker } from '../widgets/Picker';
import { SAMPLES } from '../model/samples';

const Elm = styled('div')`
	display: flex;
`;

export const Toolbar: FC = () => {
	const scriptContext = useContext(ScriptContext);
	const script = scriptContext.draft;

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
			<Picker
				buttonLabel="Add"
				title="Add Hop to Script"
				buttonAttributes={{ icon: 'addChildPanel', is: 'coral-button' }}
				picked={value => {
					const sampleScript = SAMPLES[Number(value)]!.config;
					script.hops.push(...sampleScript.hops);
					script.parameters.push(...sampleScript.parameters);
					scriptContext.commit();
				}}
				items={SAMPLES.map(({ label }, i) => [String(i), label])}
			/>
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
