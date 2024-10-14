import { AnyHop, Hop, ConflictResolutionStrategy } from '.';

export interface Type extends AnyHop {
	type: 'copyNode';
	newName: string;
	conflict?: ConflictResolutionStrategy;
	hops?: Hop[];
}

export const defaultConfig: Partial<Type> = {
	conflict: 'ignore',
	newName: '${node.name}-new-suffix',
};

export const title = 'Copy Node';

export function shortDescription(config: Type) {
	if (!config.newName) {
		return title;
	}

	return `${title} to ${config.newName}${config.newName[0] === '/' ? '' : ' of Parent'}`;
}
