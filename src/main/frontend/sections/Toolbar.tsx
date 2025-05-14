import React, { FC, useContext } from 'react';

import { styled } from 'goober';
import { useCopyToClipboard } from '@uidotdev/usehooks';

import { EnvironmentContext, ScriptContext } from '../App';
import { INITIAL_SCRIPT, Script } from '../model/Script';
import { Picker } from '../widgets/Picker';
import { Sample, SAMPLES } from '../model/samples';

const Elm = styled('div')`
	display: flex;
`;

export const Toolbar: FC = () => {
	const scriptContext = useContext(ScriptContext);
	const script = scriptContext.draft;

	const samples: Sample[] = fetchSamples();
	const [, copy] = useCopyToClipboard();

	function fetchSamples() {
		try {
			const environmentContext = useContext(EnvironmentContext);
			const dataSampleScripts = environmentContext.sampleScripts;
			if (Array.isArray(dataSampleScripts)) {
				const additionalSamples = dataSampleScripts.map(item => ({
					label: item['label'] ?? 'Sample',
					config: JSON.parse(item['configJson'] ?? '{}') as Script,
				}));
				return [...SAMPLES, ...additionalSamples];
			} else {
				return SAMPLES;
			}
		} catch (error) {
			console.error('Error fetching additional samples:', error);
			return SAMPLES;
		}
	}

	const handlePaste = async () => {
		try {
			const json = await navigator.clipboard.readText();
			const state = JSON.parse(json);
			if (typeof state !== 'object') throw new Error(`Pasted JSON is not an object: ${json}`);

			if (Array.isArray(state)) {
				script.hops.push(...state);
				scriptContext.commit();
			} else {
				scriptContext.replace({ ...INITIAL_SCRIPT, ...state });
			}
		} catch (e) {
			console.error('Could not paste script', e);
		}
	};

	const handleAddHop = (value: string) => {
		const sampleScript = samples[Number(value)]!.config;
		script.hops.push(...sampleScript.hops);
		script.parameters.push(...sampleScript.parameters);
		scriptContext.commit();
	};

	return (
		<Elm className="toolbar">
			<button
				is="coral-button"
				icon="copy"
				onClick={() => {
					copy(JSON.stringify(scriptContext.current));
				}}
			>
				Copy
			</button>
			<button is="coral-button" icon="paste" onClick={handlePaste}>
				Paste
			</button>
			<Picker
				buttonLabel="Add"
				title="Add Hop to Script"
				buttonAttributes={{ icon: 'addChildPanel', is: 'coral-button' }}
				picked={handleAddHop}
				items={samples.map(({ label }, i) => [String(i), label])}
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
