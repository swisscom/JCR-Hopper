import { LogLevel } from './LogLevel';

export interface Parameter {
	name: string;
	defaultValue: string;
	type: string;
}

interface AnyHop {
	type: string;
}

export interface Script {
	logLevel: LogLevel;
	parameters: Parameter[];
	hops: AnyHop[];
}

export const SESSION_STORAGE_KEY = 'jcr-hopper.script';

export const INITIAL_SCRIPT: Script = {
	logLevel: 'info',
	hops: [],
	parameters: [],
};

export function getInitialScript(): Script {
	const stored = window.sessionStorage.getItem(SESSION_STORAGE_KEY);
	if (stored) {
		try {
			return JSON.parse(stored);
		} catch (e) {
			console.warn('Unable to restore script', stored, 'from session, got', e);
		}
	}
	return INITIAL_SCRIPT;
}
