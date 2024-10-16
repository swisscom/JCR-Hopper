import React, { FC } from 'react';

import type { CoralIcon } from '../coral/custom-elements';
import { ConflictResolutionStrategy } from '../model/hops';
import { Select } from './Select';

export type Options = [value: string, label: string, icon?: CoralIcon][];

export const Conflict: FC<{
	label?: string;
	forceLabel?: string;
	value: ConflictResolutionStrategy;
	onChange(newValue: ConflictResolutionStrategy): void;
}> = ({ label = 'Conflict Resolution', forceLabel = 'Force the given action', value, onChange }) => {
	return (
		<Select
			value={value}
			label={label}
			onChange={v => onChange(v)}
			list={[
				['ignore', 'Ignore conflict, abort the current action'],
				['throw', 'Throw an exception, stop pipeline'],
				['force', forceLabel],
			]}
		/>
	);
};
