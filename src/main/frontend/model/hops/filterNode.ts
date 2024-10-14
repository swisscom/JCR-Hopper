import { AnyHop, Hop } from '.';

export interface Type extends AnyHop {
	type: 'filterNode';
	expression: string;
	hops?: Hop[];
}

export const defaultConfig: Partial<Type> = {
	expression: 'jcr:val(node, "some-property") == "some-value"',
};

export const title = 'Filter Node';

export function shortDescription(config: Type) {
	return `Check if Node Matches ${config.expression || 'an Expression'}`;
}
