import { AnyHop, ConflictResolutionStrategy } from '.';
import { DEV_NULL } from './renameProperty';

export interface Type extends AnyHop {
	type: 'moveNode';
	newName: string;
	conflict?: ConflictResolutionStrategy;
}

export const defaultConfig: Partial<Type> = {
	conflict: 'ignore',
	newName: '${node.name}-new-suffix',
};

export const title = 'Move Node';

export function shortDescription(config: Type) {
	if (!config.newName) {
		return title;
	}

	if (config.newName === DEV_NULL) {
		return 'Remove current node';
	}

	return `${title} to ${config.newName}${config.newName[0] === '/' ? '' : ' of parent'}`;
}
