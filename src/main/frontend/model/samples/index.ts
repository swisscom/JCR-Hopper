import { Hop, HOP_DEFINITIONS } from '../hops';
import { Script } from '../Script';

export type Sample = { label: string; config: Script };

function suggestionFromDefinition(key: keyof typeof HOP_DEFINITIONS): Sample {
	return {
		config: { hops: [{ type: key, ...HOP_DEFINITIONS[key].defaultConfig } as Hop], logLevel: 'info', parameters: [] },
		label: HOP_DEFINITIONS[key].title,
	};
}

export const SAMPLES: Sample[] = [
	suggestionFromDefinition('nodeQuery'),
	suggestionFromDefinition('resolveNode'),
	suggestionFromDefinition('declare'),
	suggestionFromDefinition('each'),
	suggestionFromDefinition('runScript'),
];
