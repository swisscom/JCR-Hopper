import React, { FC, useContext, useEffect, useRef } from 'react';

import { ScriptContext } from '../App';

export const Select: FC<{
	list: [string, string][];
	value: string;
	onChange: (value: string) => void;
}> = ({ list, value, onChange }) => {
	const scriptContext = useContext(ScriptContext);

	const selectRef = useRef<HTMLSelectElement>(null);
	useEffect(() => {
		const sel = selectRef.current;
		if (!sel) {
			return;
		}

		const changed = () => {
			onChange(sel.value);
			scriptContext.commit();
		};

		sel.addEventListener('change', changed);

		return () => {
			sel.removeEventListener('change', changed);
		};
	}, [selectRef.current, scriptContext]);

	return (
		<coral-select ref={selectRef} value={value}>
			{list.map(([val, label]) => (
				<coral-select-item key={val} value={val}>
					{label}
				</coral-select-item>
			))}
		</coral-select>
	);
};
