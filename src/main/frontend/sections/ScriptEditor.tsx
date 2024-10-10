import React, { FC, useContext } from 'react';

import { styled } from 'goober';
import { ScriptContext } from '../App';
import { Select } from '../widgets/Select';
import { Parameter } from './editor/Parameter';
import { Pipeline } from './editor/Pipeline';

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
			<fieldset className="options">
				<legend>Options</legend>
				<label>
					Log Level:{' '}
					<Select
						list={[
							['trace', 'TRACE'],
							['debug', 'DEBUG'],
							['info', 'INFO'],
							['warn', 'WARN'],
							['error', 'ERROR'],
						]}
						value={script.logLevel}
						onChange={val => (script.logLevel = val as typeof script.logLevel)}
					/>
				</label>
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
		</Elm>
	);
};
