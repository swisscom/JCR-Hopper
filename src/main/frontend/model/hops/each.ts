import { AnyHop, Hop } from '.';

export interface Type extends AnyHop {
	type: 'each';
	expression: string;
	iterator?: string;
	assumeNodes?: boolean;
	hops?: Hop[];
}

export const defaultConfig: Partial<Type> = {
	expression: "['de', 'fr', 'it', 'en']",
};

export const title = 'Iterate';

export function shortDescription(config: Type) {
	return `${title} Over ${config.assumeNodes ? 'Nodes' : 'Values'}${config.expression ? ` Produced by ${config.expression}` : ''}`;
}

import icon from 'data-url:../../../../../docs/icons/loop.svg';
export function iconFor(_config: Type) {
	return icon;
}
