import removeMinJs from './remove-min-js.json';
import replaceH1 from './replace-h1-with-h2.json';
import removeTempGulpPackages from './remove-gulp-packages.json';
import updateBuildTimestamp from './update-build-timestamp.json';
import findComponentAndPage from './find-component-and-page.json';
import createMissingJcrContentNodes from './create-missing-jcr-content-nodes.json';

import { Hop, HOP_DEFINITIONS } from '../hops';

type Sample = { label: string; config: Hop };

function suggestionFromDefinition(key: keyof typeof HOP_DEFINITIONS): Sample {
	return {
		config: { type: key, ...HOP_DEFINITIONS[key].defaultConfig } as Hop,
		label: HOP_DEFINITIONS[key].title,
	};
}

export const SAMPLES: Sample[] = [
	suggestionFromDefinition('nodeQuery'),
	suggestionFromDefinition('resolveNode'),
	suggestionFromDefinition('declare'),
	suggestionFromDefinition('each'),
	suggestionFromDefinition('runScript'),
	{
		config: removeMinJs as Hop,
		label: 'Sample: Remove all minified JavaScript files',
	},
	{
		config: replaceH1 as Hop,
		label: 'Sample: Replace H1 with H2 in text components',
	},
	{
		config: removeTempGulpPackages as Hop,
		label: 'Sample: Remove temporary packages created by gulp',
	},
	{
		config: updateBuildTimestamp as Hop,
		label: 'Sample: Update build timestamp',
	},
	{
		config: findComponentAndPage as Hop,
		label: 'Sample: Find component and containing page using JOIN',
	},
	{
		config: createMissingJcrContentNodes as Hop,
		label: 'Sample: Create missing jcr:content node for pages',
	},
];
