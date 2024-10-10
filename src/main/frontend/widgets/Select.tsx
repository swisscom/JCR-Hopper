import React, { FC, useContext, useEffect, useRef } from 'react';

import { ScriptContext } from '../App';
import type { CoralIcon } from '../coral/custom-elements';

export type Options = [value: string, label: string, icon?: CoralIcon][];

export const Select: FC<{
	list: Options;
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
			{list.map(([val, label, icon]) => (
				<coral-select-item key={val} value={val}>
					{icon ? <coral-icon icon={icon} /> : undefined}
					{label}
				</coral-select-item>
			))}
		</coral-select>
	);
};
