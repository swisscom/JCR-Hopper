import { AnyHop, Hop } from '.';

export interface Type extends AnyHop {
	type: 'filterNode';
	expression: string;
	hops?: Hop[];
}

export const defaultConfig: Partial<Type> = {
	expression: 'jcr:val(node, "some-property") == "some-value"',
};

export const title = 'Filter node';

export function shortDescription(config: Type) {
	return `Check if node matches ${config.expression || 'an expression'}`;
}
