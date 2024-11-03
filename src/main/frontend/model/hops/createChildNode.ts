import { AnyHop, Hop, ConflictResolutionStrategy } from '.';

export interface Type extends AnyHop {
	type: 'createChildNode';
	name: string;
	primaryType?: string;
	runOnExistingNode: boolean;
	conflict: ConflictResolutionStrategy;
	hops?: Hop[];
}

export const defaultConfig: Partial<Type> = {
	conflict: 'ignore',
	name: 'child-name',
	runOnExistingNode: false,
};

export const title = 'Create Node';

export function shortDescription(config: Type) {
	const isChild = !config.name?.startsWith('/');
	return `${isChild ? 'Create Child Node' : title}${config.name ? ' ' + config.name : ''} With Type: ${config.primaryType || 'nt:unstructured'}`;
}

import icon from 'data-url:../../../../../docs/icons/sparkler.svg';
export function iconFor(_config: Type) {
	return icon;
}
