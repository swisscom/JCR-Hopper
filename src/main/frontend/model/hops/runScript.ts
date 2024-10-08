import { AnyHop, Hop } from '.';

const SCRIPT_LANGUAGES = {
	jexl: 'JEXL',
	js: 'JavaScript',
};

export interface Type extends AnyHop {
	type: 'runScript';
	code: string;
	extension: keyof typeof SCRIPT_LANGUAGES;
	hops?: Hop[];
}

export const defaultConfig: Partial<Type> = {
	code: '',
	extension: 'js',
};

export const title = 'Run a script';

export function shortDescription(config: Type) {
	const lang = config.extension ?? 'js';
	const name = SCRIPT_LANGUAGES[lang] ?? lang.toUpperCase();

	const lines = config.code?.split(/\n/).filter(Boolean).length;
	if (!lines) {
		return `Run ${name}`;
	}

	return `Run ${lines} line${lines === 1 ? '' : 's'} of ${name}`;
}
