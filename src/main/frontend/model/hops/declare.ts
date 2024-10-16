import { AnyHop, Hop } from '.';

export interface Type extends AnyHop {
	type: 'declare';
	declarations?: Record<string, string>;
	hops?: Hop[];
}

export const defaultConfig: Partial<Type> = {};

export const title = 'Declare Variables';

export function shortDescription(config: Type) {
	const size = (config.declarations && Object.keys(config.declarations).length) || 0;
	if (size > 0) {
		return `Declare ${size} Variable${size === 1 ? '' : 's'}`;
	}

	return title;
}

import icon from 'data-url:../../../../../docs/icons/control_knobs.svg';
export function iconFor(_config: Type) {
	return icon;
}
