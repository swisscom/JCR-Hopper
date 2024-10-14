import React, { FC, useContext, useEffect, useRef } from 'react';

import type { CoralIcon } from '../coral/custom-elements';
import { ScriptContext } from '../App';

export type Options = [value: string, label: string, icon?: CoralIcon][];

export const Switch: FC<{
	label?: string;
	value: boolean;
	onChange(newValue: boolean): void;
}> = ({ label = 'Conflict Resolution', value, onChange }) => {
	const scriptContext = useContext(ScriptContext);

	const switchRef = useRef<HTMLInputElement>(null);
	useEffect(() => {
		const swi = switchRef.current;
		if (!swi) {
			return;
		}

		const changed = () => {
			onChange(swi.checked);
			scriptContext.commit();
		};

		swi.addEventListener('change', changed);

		return () => {
			swi.removeEventListener('change', changed);
		};
	}, [switchRef.current, scriptContext]);

	return (
		<label>
			<span>{label}: </span>
			<coral-switch ref={switchRef} checked={value ? true : undefined} />
		</label>
	);
};
