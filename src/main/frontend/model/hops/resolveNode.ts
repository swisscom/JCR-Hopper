import { AnyHop, ConflictResolutionStrategy, Hop } from '.';

export interface Type extends AnyHop {
	type: 'resolveNode';
	name: string;
	conflict?: ConflictResolutionStrategy;
	hops?: Hop[];
}

export const defaultConfig: Partial<Type> = {
	conflict: 'ignore',
	name: 'child-name',
};

export const title = 'Resolve Specific Node';

export function shortDescription(config: Type) {
	return `${title} ${config.name ? config.name : ''}`;
}
