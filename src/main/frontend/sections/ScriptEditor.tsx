import React, { FC, useContext } from 'react';

import { styled } from 'goober';
import { ScriptContext } from '../App';
import { Select } from '../widgets/Select';
import { Parameter } from './editor/Parameter';
import { Pipeline } from './editor/Pipeline';
import { LogLevel } from '../model/LogLevel';
import { DropZone } from '../widgets/DropZone';

const Elm = styled('div')`
	grid-auto-rows: min-content 1fr min-content;
	overflow: auto;
	.parameters {
		display: flex;
		flex-direction: column;
		gap: 6px;
		> .param {
			display: flex;
			gap: 6px;
		}
		> button {
			align-self: end;
		}
	}
`;

const LOG_LEVEL_LABELS: [LogLevel, string][] = [
	['trace', 'TRACE'],
	['debug', 'DEBUG'],
	['info', 'INFO'],
	['warn', 'WARN'],
	['error', 'ERROR'],
];

export const ScriptEditor: FC = () => {
	const scriptContext = useContext(ScriptContext);
	const script = scriptContext.draft;

	function addParameter() {
		script.parameters.push({
			name: `param_${script.parameters.length + 1}`,
			defaultValue: '',
			type: 'text',
			evaluation: 'STRING',
		});
		scriptContext.commit();
	}

	return (
		<Elm className="script-editor">
			<DropZone dropZoneClass="hop-list">
				<fieldset className="field-container">
					<legend>Options</legend>
					<Select
						label="Log Level"
						list={LOG_LEVEL_LABELS}
						value={script.logLevel}
						onChange={val => (script.logLevel = val)}
					/>
				</fieldset>
				<div className="script">
					<Pipeline hops={script.hops} addButton={false} />
				</div>
				<fieldset className="parameters">
					<legend>Parameters</legend>
					{script.parameters.map((param, i) => (
						<Parameter key={i} i={i} param={param} />
					))}
					<button is="coral-button" icon="add" onClick={addParameter} />
				</fieldset>
			</DropZone>
		</Elm>
	);
};
