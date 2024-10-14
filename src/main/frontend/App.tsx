import React, { FC, createContext } from 'react';

import { styled } from 'goober';
import { Toolbar } from './sections/Toolbar';
import { Runner } from './sections/Runner';

import { getInitialScript, Script, SESSION_STORAGE_KEY } from './model/Script';
import { HistoryUpdater, useHistoryImmutable } from './hooks/useHistoryImmutable';
import { useOnce } from './hooks/useOnce';
import { ScriptEditor } from './sections/ScriptEditor';

export const RunEndpointContext = createContext('');
export const ScriptContext = createContext<HistoryUpdater<Script>>(null!);

const RootElement = styled('div')`
	position: relative;
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
	grid-template-columns: 2fr 1fr;

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

	.field-container {
		display: grid;
		grid-template-columns: max-content 1fr;
		gap: 6px;
		> label {
			display: contents;
			> span {
				align-content: center;
			}
			> input,
			> coral-select {
				width: auto;
			}
		}
		> section,
		> fieldset {
			grid-column: span 2;
		}
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
					<ScriptEditor />
					<Runner />
				</RootElement>
			</ScriptContext.Provider>
		</RunEndpointContext.Provider>
	);
};
