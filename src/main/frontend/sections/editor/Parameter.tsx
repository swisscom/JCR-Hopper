import React, { FC, useContext, useEffect, useState } from 'react';

import { styled } from 'goober';
import { ScriptContext } from '../../App';
import { Script } from '../../model/Script';
import { Select } from '../../widgets/Select';

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

	const [name, setName] = useState(param.name || '');
	useEffect(() => {
		setName(param.name);
	}, [param.name]);

	const [defaultValue, setDefaultValue] = useState(param.defaultValue || '');
	useEffect(() => {
		setDefaultValue(param.defaultValue);
	}, [param.defaultValue]);

	return (
		<Elm>
			<label>
				<span>Param {i + 1}: </span>
				<input
					is="coral-textfield"
					placeholder="Name"
					name="name"
					value={name}
					onChange={e => {
						setName(e.target.value);
					}}
					onBlur={e => {
						param.name = e.target.value;
						scriptContext.commit();
					}}
				></input>
			</label>
			<input
				is="coral-textfield"
				placeholder="Default Value"
				name="defaultValue"
				value={defaultValue}
				onChange={e => {
					setDefaultValue(e.target.value);
				}}
				onBlur={e => {
					param.defaultValue = e.target.value;
					scriptContext.commit();
				}}
			></input>
			<Select
				list={[
					['text', 'Text'],
					['file', 'File'],
				]}
				value={param.type}
				onChange={val => (param.type = val)}
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
