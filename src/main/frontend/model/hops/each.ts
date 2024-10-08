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
	return `Iterate over ${config.assumeNodes ? 'nodes' : 'values'} produced by ${config.expression}`;
}
