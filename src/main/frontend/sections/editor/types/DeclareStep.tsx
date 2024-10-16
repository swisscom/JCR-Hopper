import React, { forwardRef, Fragment, useContext } from 'react';
import { styled } from 'goober';

import { Hop } from '../../../model/hops';
import { StepEditor } from '../../../widgets/StepEditor';
import { ScriptContext } from '../../../App';

import { shortDescription, title, Type, iconFor } from '../../../model/hops/declare';
import { Help } from '../../../widgets/Help';
import { Input } from '../../../widgets/Input';

const Elm = styled('fieldset')`
	grid-template-columns: 1fr 2fr 1fr 2fr !important;
`;

export const DeclareStep = forwardRef<HTMLDivElement, { parentHops: Hop[]; hop: Type }>(function DeclareStep({ parentHops, hop }, ref) {
	const scriptContext = useContext(ScriptContext);

	const declarations = (hop.declarations ??= {});

	return (
		<StepEditor icon={iconFor(hop)} parentHops={parentHops} hop={hop} title={shortDescription(hop)} ref={ref}>
			<Elm className="field-container">
				<legend>Declarations</legend>
				{Object.entries(declarations)
					.sort()
					.map(([varName, varValue]) => (
						<Fragment key={varName}>
							<Input
								label="Name"
								placeholder="Variable Name"
								value={varName}
								onChange={newVar => {
									delete declarations[varName];
									if (newVar) {
										declarations[newVar] = varValue;
									}
								}}
							/>
							<Input
								label="Value"
								placeholder="Variable Value"
								value={varValue}
								onChange={newValue => {
									declarations[varName] = newValue;
								}}
							/>
						</Fragment>
					))}
			</Elm>
			<button
				style={{ justifySelf: 'start' }}
				is="coral-button"
				icon="boxAdd"
				onClick={() => {
					let name = 'variable';
					let i = 1;
					while (name in declarations) {
						name = 'variable' + i;
						i++;
					}
					declarations[name] = '';
					scriptContext.commit();
				}}
			/>
			<Help title={title}>
				<p>
					Makes the declared variables available in the descendant pipeline. Useful if you use the same expression many
					times. Note that the values are JEXL expressions already so you should not use <code>{'${}'}</code>.
				</p>
			</Help>
		</StepEditor>
	);
});
