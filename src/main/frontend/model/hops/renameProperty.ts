import { AnyHop, ConflictResolutionStrategy } from '.';

export interface Type extends AnyHop {
	type: 'renameProperty';
	propertyName: string;
	newName: string;
	doesNotExist?: ConflictResolutionStrategy;
	conflict?: ConflictResolutionStrategy;
}

export const defaultConfig: Partial<Type> = {
	newName: '${node.parent.name}-prop',
	propertyName: 'some-property',
};

export const title = 'Rename Property';

export const DEV_NULL = '/dev/null';

export function shortDescription(config: Type) {
	if (!config.newName || !config.propertyName) {
		return `${title}`;
	}

	if (config.newName === DEV_NULL) {
		return `Delete Property ${config.propertyName}`;
	}

	return `${title} ${config.propertyName} to ${config.newName}`;
}

import icon from 'data-url:../../../../../docs/icons/speech_bubble.svg';
import iconDelete from 'data-url:../../../../../docs/icons/bomb.svg';
export function iconFor(config: Type) {
	return config.newName === DEV_NULL ? iconDelete : icon;
}
