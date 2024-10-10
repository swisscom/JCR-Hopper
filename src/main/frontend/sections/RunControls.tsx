import React, { FC, FormEvent, useContext, useId, useRef } from 'react';

import { styled } from 'goober';
import { RunEndpointContext, ScriptContext } from '../App';

const Elm = styled('form', React.forwardRef)`
	grid-auto-flow: row;
	.arguments {
		display: grid;
		grid-template-columns: 1fr 3fr;
		> input {
			width: auto;
		}
	}
	> .options {
		display: flex;
		gap: 0.5em;
	}
`;

export const RunControls: FC<{ runWith: (data: FormData) => Promise<void> }> = ({ runWith }) => {
	const { current: script } = useContext(ScriptContext);
	const endpoint = useContext(RunEndpointContext);

	const formRef = useRef<HTMLFormElement>(null);

	async function run(e: FormEvent) {
		e.preventDefault();
		const data = new FormData(formRef.current!);
		data.append('_script', JSON.stringify(script));

		await runWith(data);
	}

	const id = useId();

	return (
		<Elm className="run-controls" ref={formRef} method="POST" action={endpoint} encType="multipart/form-data" onSubmit={run}>
			{script.parameters.length ? (
				<fieldset>
					<legend>Arguments</legend>
					<div className="arguments">
						{script.parameters.map(({ name, type }, i) => (
							<React.Fragment key={name}>
								<label htmlFor={`${id}_${i}`}>{name}: </label>
								<input id={`${id}_${i}`} is="coral-textfield" type={type} name={name} />
							</React.Fragment>
						))}
					</div>
				</fieldset>
			) : undefined}
			<div className="options">
				<span className="flex-spacer"></span>
				<coral-checkbox name="_commit" value="true">
					<coral-checkbox-label>Commit Changes</coral-checkbox-label>
				</coral-checkbox>
				<button type="submit" is="coral-button" icon="playCircle" iconsize="S">
					Run
				</button>
			</div>
		</Elm>
	);
};
