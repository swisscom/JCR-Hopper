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

export const title = 'Get child nodes';

export function shortDescription(config: Type) {
	if (config.namePattern) {
		return `${title} matching pattern ${config.namePattern}`;
	}

	return 'Select all child nodes';
}
