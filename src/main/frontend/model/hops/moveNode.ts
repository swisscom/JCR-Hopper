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
		return 'Remove Current Node';
	}

	return `${title} to ${config.newName}${config.newName[0] === '/' ? '' : ' of Parent'}`;
}

import icon from 'data-url:../../../../../docs/icons/desert_island.svg';
import iconDelete from 'data-url:../../../../../docs/icons/bomb.svg';
export function iconFor(config: Type) {
	return config.newName === DEV_NULL ? iconDelete : icon;
}
