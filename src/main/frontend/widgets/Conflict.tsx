import React, { FC } from 'react';

import type { CoralIcon } from '../coral/custom-elements';
import { ConflictResolutionStrategy } from '../model/hops';
import { Select } from './Select';

export type Options = [value: string, label: string, icon?: CoralIcon][];

export const Conflict: FC<{
	label?: string;
	forceLabel?: string;
	value: ConflictResolutionStrategy;
	changed(newValue: ConflictResolutionStrategy): void;
}> = ({ label = 'Conflict Resolution', forceLabel = 'Force the given action', value, changed }) => {
	return (
		<label>
			{label}:{' '}
			<Select
				value={value}
				onChange={v => changed(v as ConflictResolutionStrategy)}
				list={[
					['ignore', 'Ignore conflict, abort the current action'],
					['throw', 'Throw an exception, stop pipeline'],
					['force', forceLabel],
				]}
			/>
		</label>
	);
};
