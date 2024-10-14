import { AnyHop, ConflictResolutionStrategy } from '.';

export interface Type extends AnyHop {
	type: 'reorderNode';
	before: string;
	conflict?: ConflictResolutionStrategy;
}

export const defaultConfig: Partial<Type> = {
	before: '',
	conflict: 'ignore',
};

export const title = 'Reorder Node';

export function shortDescription(config: Type) {
	if (!config.before?.trim()) {
		return 'Order Node Last';
	}

	return `${title} Before ${config.before}`;
}
