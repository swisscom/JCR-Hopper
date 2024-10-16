import { AnyHop } from '.';

export const SCRIPT_LANGUAGES = {
	jexl: 'JEXL',
	js: 'JavaScript',
};

export interface Type extends AnyHop {
	type: 'runScript';
	code: string;
	extension: keyof typeof SCRIPT_LANGUAGES;
	putLocalsBackIntoScope?: boolean;
}

export const defaultConfig: Partial<Type> = {
	code: '',
	extension: 'js',
	putLocalsBackIntoScope: true,
};

export const title = 'Run a Script';

export function shortDescription(config: Type) {
	const lang = config.extension ?? 'js';
	const name = SCRIPT_LANGUAGES[lang] ?? lang.toUpperCase();

	const lines = config.code?.split(/\n/).filter(Boolean).length;
	if (!lines) {
		return `Run ${name}`;
	}

	return `Run ${lines} Line${lines === 1 ? '' : 's'} of ${name}`;
}

import defaultIcon from 'data-url:../../../../../docs/icons/pink_potion.svg';
import jsIcon from 'data-url:../../../../../docs/icons/yellow_potion.svg';
import jexlIcon from 'data-url:../../../../../docs/icons/blue_potion.svg';

export function iconFor(config: Type) {
	let result = defaultIcon;
	if (config.extension === 'js') {
		result = jsIcon;
	}
	if (config.extension === 'jexl') {
		result = jexlIcon;
	}
	return result;
}
