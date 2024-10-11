import React, { FC, useContext, useEffect, useRef, useState } from 'react';

import { ScriptContext } from '../App';
import type { CoralIcon } from '../coral/custom-elements';

export type Options = [value: string, label: string, icon?: CoralIcon][];

export const Input: FC<{
	value: string;
	name?: string;
	placeholder?: string;
	onChange: (value: string) => void;
}> = ({ value: outsideValue, name, placeholder, onChange }) => {
	const scriptContext = useContext(ScriptContext);

	const hasChanged = useRef(false);
	const [value, setValue] = useState(outsideValue || '');
	useEffect(() => {
		hasChanged.current = false;
		setValue(outsideValue);
	}, [outsideValue]);

	return (
		<input
			is="coral-textfield"
			name={name}
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
};
