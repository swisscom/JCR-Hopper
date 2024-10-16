import { AnyHop, ConflictResolutionStrategy } from '.';

export interface Type extends AnyHop {
	type: 'setProperty';
	propertyName: string;
	value: string;
	conflict?: ConflictResolutionStrategy;
}

export const defaultConfig: Partial<Type> = {
	conflict: 'ignore',
	propertyName: 'some-property',
	value: '"new-name-"+node.parent.parent.primaryNodeType.name',
};

export const title = 'Set a Property';

export function shortDescription(config: Type) {
	if (!config.propertyName || !config.value) {
		return title;
	}

	return `Set Property ${config.propertyName} to ${config.value}`;
}

import icon from 'data-url:../../../../../docs/icons/pencil.svg';
export function iconFor(_config: Type) {
	return icon;
}
