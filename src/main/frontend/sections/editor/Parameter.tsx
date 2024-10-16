import React, { FC, useContext } from 'react';

import { styled } from 'goober';
import { ScriptContext } from '../../App';
import { Script } from '../../model/Script';
import { Select } from '../../widgets/Select';
import { Input } from '../../widgets/Input';

const Elm = styled('div')`
	display: flex;
	gap: 6px;
	> label > span {
		display: inline-block;
		width: 10ch;
	}
`;

export const Parameter: FC<{ param: Script['parameters'][0]; i: number }> = ({ i, param }) => {
	const scriptContext = useContext(ScriptContext);
	const script = scriptContext.draft;

	return (
		<Elm>
			<Input name="name" placeholder="Name" onChange={name => (param.name = name)} value={param.name} />
			<Input
				name="defaultValue"
				placeholder="Default Value"
				onChange={defaultValue => (param.defaultValue = defaultValue)}
				value={param.defaultValue}
			/>
			<Select
				list={[
					['text', 'Text'],
					['file', 'File'],
				]}
				value={param.type}
				onChange={val => (param.type = val)}
			/>
			<Select
				list={[
					['STRING', 'String'],
					['LINES', 'Array of Lines'],
					['TEMPLATE', 'JEXL String Template'],
					['EXPRESSION', 'JEXL Expression'],
				]}
				value={param.evaluation}
				onChange={val => (param.evaluation = val as typeof param.evaluation)}
			/>
			<button
				is="coral-button"
				icon="delete"
				onClick={() => {
					script.parameters.splice(i, 1);
					scriptContext.commit();
				}}
			/>
		</Elm>
	);
};
