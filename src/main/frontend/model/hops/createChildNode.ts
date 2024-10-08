import { AnyHop, Hop, ConflictResolutionStrategy } from '.';

export interface Type extends AnyHop {
	type: 'createChildNode';
	name: string;
	primaryType?: string;
	conflict: ConflictResolutionStrategy;
	hops?: Hop[];
}

export const defaultConfig: Partial<Type> = {
	conflict: 'ignore',
	name: 'child-name',
};

export const title = 'Create child node';

export function shortDescription(config: Type) {
	return `${title}${config.name ? ' ' + config.name : ''} with type: ${config.primaryType || 'nt:unstructured'}`;
}
