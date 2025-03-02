import { AnyHop, Hop } from '.';

export interface Type extends AnyHop {
	type: 'try';
	catchGeneric: boolean;
	hops?: Hop[];
}

export const defaultConfig: Partial<Type> = {
	catchGeneric: false,
};

export const title = 'Catch thrown errors';

export function shortDescription(config: Type) {
	return `${config.catchGeneric ? 'Catch All Thrown Errors' : 'Catch Pipeline Errors'}`;
}

import icon from 'data-url:../../../../../docs/icons/bang.svg';
export function iconFor(_config: Type) {
	return icon;
}
