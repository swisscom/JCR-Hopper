import { Hop } from './hops';

export function jsonToHops(json: string): Hop[] {
	try {
		const val = JSON.parse(json);
		if (Array.isArray(val)) {
			// Assume hop list
			return val as Hop[];
		}
		if (typeof val === 'object') {
			// Guess the type from the shape
			if ('type' in val) {
				// Assume single hop
				return [val as Hop];
			}
			if ('hops' in val && Array.isArray(val.hops)) {
				// Assume script or list of hops
				return val.hops as Hop[];
			}
		}
		throw new Error(`Unknown shape of ${json}, cannot turn into hop list`);
	} catch (e) {
		console.error('Could not parse', json, e);
	}
	return [];
}
