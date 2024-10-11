import React, { FC, useContext, useEffect, useRef, useState } from 'react';

import { ScriptContext } from '../App';
import type { CoralIcon } from '../coral/custom-elements';

export type Options = [value: string, label: string, icon?: CoralIcon][];

export const Input: FC<{
	label?: string;
	value: string;
	name?: string;
	placeholder?: string;
	type?: HTMLInputElement['type'];
	onChange: (value: string) => void;
}> = ({ label, value: outsideValue, name, placeholder, type = 'text', onChange }) => {
	const scriptContext = useContext(ScriptContext);

	const hasChanged = useRef(false);
	const [value, setValue] = useState(outsideValue || '');
	useEffect(() => {
		hasChanged.current = false;
		setValue(outsideValue);
	}, [outsideValue]);

	const input = (
		<input
			is="coral-textfield"
			name={name}
			type={type}
			placeholder={placeholder}
			value={value}
			onChange={e => {
				hasChanged.current = true;
				setValue(e.target.value);
			}}
			onBlur={() => {
				if (hasChanged.current) {
					hasChanged.current = false;
					onChange(value);
					scriptContext.commit();
				}
			}}
		></input>
	);

	if (label) {
		return (
			<label>
				{label}: {input}
			</label>
		);
	}

	return input;
};
