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

export const title = 'Create Node';

export function shortDescription(config: Type) {
	const isChild = !config.name?.startsWith('/');
	return `${isChild ? 'Create Child Node' : title}${config.name ? ' ' + config.name : ''} With Type: ${config.primaryType || 'nt:unstructured'}`;
}
