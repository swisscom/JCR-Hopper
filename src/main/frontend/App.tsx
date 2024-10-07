import React, { FC, createContext } from 'react';

import { styled } from 'goober';
import { Toolbar } from './sections/Toolbar';
import { RunControls } from './sections/RunControls';
import { ScriptEditor } from './sections/ScriptEditor';
import { Output } from './sections/Output';
import { getInitialScript, Script, SESSION_STORAGE_KEY } from './model/Script';
import { HistoryUpdater, useHistoryImmutable } from './hooks/useHistoryImmutable';
import { useOnce } from './hooks/useOnce';

export const RunEndpointContext = createContext('');
export const ScriptContext = createContext<HistoryUpdater<Script>>(null!);

const RootElement = styled('div')`
	box-sizing: border-box;
	height: 100%;
	padding: 0.5em;
	gap: 1em;
	font-size: 12px;
	display: grid;
	grid-template-areas:
		'toolbar run-controls'
		'script-editor run-controls'
		'script-editor output';
	grid-template-rows: min-content min-content 1fr;
	grid-template-columns: 1fr 1fr;

	.flex-spacer {
		flex-grow: 1;
	}

	> * {
		display: grid;
		gap: 0.5em;
	}

	> .toolbar {
		grid-area: toolbar;
	}
	> .run-controls {
		grid-area: run-controls;
	}
	> .script-editor {
		grid-area: script-editor;
	}
	> .output {
		grid-area: output;
	}
`;

export const App: FC<{ runEndpoint: string }> = props => {
	const initialScript = useOnce(getInitialScript);

	const scriptContext = useHistoryImmutable(initialScript, current => {
		window.sessionStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(current));
	});

	return (
		<RunEndpointContext.Provider value={props.runEndpoint}>
			<ScriptContext.Provider value={scriptContext}>
				<RootElement>
					<Toolbar />
					<RunControls />
					<ScriptEditor />
					<Output />
				</RootElement>
			</ScriptContext.Provider>
		</RunEndpointContext.Provider>
	);
};
