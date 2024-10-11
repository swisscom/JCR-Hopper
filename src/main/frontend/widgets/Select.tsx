import React, { FC, useContext, useEffect, useRef } from 'react';

import { ScriptContext } from '../App';
import type { CoralIcon } from '../coral/custom-elements';

export type Options<V extends string = string> = [value: V, label: string, icon?: CoralIcon][];

type SelectFC<V extends string = string> = FC<{
	label?: string;
	list: Options<V>;
	value: V;
	onChange: (value: V) => void;
}>;

export const Select: <V extends string = string>(...args: Parameters<SelectFC<V>>) => ReturnType<SelectFC<V>> = ({
	label,
	list,
	value,
	onChange,
}) => {
	const scriptContext = useContext(ScriptContext);

	const selectRef = useRef<HTMLSelectElement>(null);
	useEffect(() => {
		const sel = selectRef.current;
		if (!sel) {
			return;
		}

		const changed = () => {
			onChange(sel.value as typeof value);
			scriptContext.commit();
		};

		sel.addEventListener('change', changed);

		return () => {
			sel.removeEventListener('change', changed);
		};
	}, [selectRef.current, scriptContext]);

	const select = (
		<coral-select ref={selectRef} value={value}>
			{list.map(([val, label, icon]) => (
				<coral-select-item key={val} value={val}>
					{icon ? <coral-icon icon={icon} /> : undefined}
					{label}
				</coral-select-item>
			))}
		</coral-select>
	);

	if (label) {
		return (
			<label>
				{label}: {select}
			</label>
		);
	}
	return select;
};
