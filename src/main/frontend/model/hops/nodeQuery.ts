import { AnyHop, Hop } from './index';

export interface Type extends AnyHop {
	type: 'nodeQuery';
	query: string;
	queryType: 'xpath' | 'JCR-SQL2';
	counterName?: string;
	limit?: number;
	offset?: number;
	hops: Hop[];
}

export const defaultConfig: Partial<Type> = {
	query: 'SELECT * FROM [cq:Page] as page WHERE …',
	queryType: 'JCR-SQL2',
};

export const title = 'Query JCR';

const QUERY_TYPE_LABELS = {
	'JCR-SQL2': 'SQL2',
	xpath: 'XPath',
};

export function shortDescription(config: Type) {
	const method = QUERY_TYPE_LABELS[config.queryType || defaultConfig.queryType];

	return `${title} using ${method}${config.query ? ' for ' + config.query : ''}`;
}
