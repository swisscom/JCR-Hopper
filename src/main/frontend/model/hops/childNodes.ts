import { AnyHop, Hop } from '.';

export interface Type extends AnyHop {
	type: 'childNodes';
	namePattern?: string;
	counterName?: string;
	hops?: Hop[];
}

export const defaultConfig: Partial<Type> = {
	namePattern: 'prefix*',
};

export const title = 'Get Child Nodes';

export function shortDescription(config: Type) {
	if (config.namePattern) {
		return `${title} Matching Pattern ${config.namePattern}`;
	}

	return 'Select All Child Nodes';
}

import icon from 'data-url:../../../../../docs/icons/arrow_curve_down.svg';
export function iconFor(_config: Type) {
	return icon;
}
