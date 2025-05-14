import React, { FC, FormEvent, useContext, useRef } from 'react';

import { styled } from 'goober';
import { EnvironmentContext, ScriptContext } from '../App';

const Elm = styled('form', React.forwardRef)`
	grid-auto-flow: row;
	> .options {
		display: flex;
		gap: 0.5em;
	}
`;

export const RunControls: FC<{ runWith: (data: FormData) => Promise<void> }> = ({ runWith }) => {
	const { current: script } = useContext(ScriptContext);
	const environmentContext = useContext(EnvironmentContext);

	const formRef = useRef<HTMLFormElement>(null);

	async function run(e: FormEvent) {
		e.preventDefault();
		const data = new FormData(formRef.current!);
		for (const key of Array.from(data.keys())) {
			const val = data.get(key);
			if (!val || (val instanceof File && !val.name)) {
				data.delete(key);
			}
		}
		data.append('_script', JSON.stringify(script));

		await runWith(data);
	}

	return (
		<Elm
			className="run-controls"
			ref={formRef}
			method="POST"
			action={environmentContext.runEndpoint}
			encType="multipart/form-data"
			onSubmit={run}
		>
			{script.parameters.length ? (
				<fieldset>
					<legend>Arguments</legend>
					<div className="arguments field-container">
						{script.parameters.map(({ name, type }) => (
							<React.Fragment key={name}>
								<label>
									<span>{name}: </span>
									<input is="coral-textfield" type={type} name={name} />
								</label>
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
