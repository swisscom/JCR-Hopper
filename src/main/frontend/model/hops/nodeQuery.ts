import { AnyHop, Hop } from './index';

export interface Type extends AnyHop {
	type: 'nodeQuery';
	query: string;
	queryType: 'xpath' | 'JCR-SQL2';
	counterName?: string;
	selectorName?: string;
	limit?: number | undefined;
	offset?: number | undefined;
	hops: Hop[];
}

export const defaultConfig: Partial<Type> = {
	query: 'SELECT * FROM [cq:Page] AS page',
	queryType: 'JCR-SQL2',
};

export const title = 'Query JCR';

export const QUERY_TYPE_LABELS = {
	'JCR-SQL2': 'SQL2',
	xpath: 'XPath',
} as const;

export function shortDescription(config: Type) {
	const method = QUERY_TYPE_LABELS[config.queryType || defaultConfig.queryType];

	return `${title} Using ${method}${config.query ? ' for ' + config.query : ''}`;
}

import icon from 'data-url:../../../../../docs/icons/magnifying_glass_right.svg';
import iconWeird from 'data-url:../../../../../docs/icons/magnifying_glass_left.svg';
export function iconFor(config: Type) {
	return config.queryType === 'JCR-SQL2' ? icon : iconWeird;
}
