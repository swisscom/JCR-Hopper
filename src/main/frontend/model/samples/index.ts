import removeMinJs from './remove-min-js.json';
import replaceH1 from './replace-h1-with-h2.json';
import removeTempGulpPackages from './remove-gulp-packages.json';
import updateBuildTimestamp from './update-build-timestamp.json';
import findComponentAndPage from './find-component-and-page.json';
import createMissingJcrContentNodes from './create-missing-jcr-content-nodes.json';
import migrateResourceType from './migrate-resource-type.json';
import migratePageTemplate from './migrate-page-template.json';
import batchSearchAndReplace from './batch-search-and-replace.json';
import checkHardcodedUrls from './check-hardcoded-urls.json';
import removeDeprecatedLegacyUrls from './remove-deprecated-legacy-urls.json';
import deleteNodes from './delete-nodes.json';
import renameProperty from './rename-property.json';
import addOrReplaceProperty from './add-or-replace-property.json';
import addNode from './add-node.json';
import renameNode from './rename-node.json';

import { Hop, HOP_DEFINITIONS } from '../hops';
import { Script } from '../Script';

type Sample = { label: string; config: Script };

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
	{
		config: removeMinJs as Script,
		label: 'Sample: Remove all minified JavaScript files',
	},
	{
		config: replaceH1 as Script,
		label: 'Sample: Replace H1 with H2 in text components',
	},
	{
		config: removeTempGulpPackages as Script,
		label: 'Sample: Remove temporary packages created by gulp',
	},
	{
		config: updateBuildTimestamp as Script,
		label: 'Sample: Update build timestamp',
	},
	{
		config: findComponentAndPage as Script,
		label: 'Sample: Find component and containing page using JOIN',
	},
	{
		config: createMissingJcrContentNodes as Script,
		label: 'Sample: Create missing jcr:content node for pages',
	},
	{
		config: migrateResourceType as Script,
		label: 'Sample: Migrate Resource Type',
	},
	{
		config: migratePageTemplate as Script,
		label: 'Sample: Migrate Page Template',
	},
	{
		config: batchSearchAndReplace as Script,
		label: 'Sample: Batch search and replace',
	},
	{
		config: checkHardcodedUrls as Script,
		label: 'Sample: Check hardcoded urls',
	},
	{
		config: removeDeprecatedLegacyUrls as Script,
		label: 'Sample: Remove deprecated legacy urls',
	},
	{
		config: addOrReplaceProperty as Script,
		label: 'Sample: Add or replace property',
	},
	{
		config: renameProperty as Script,
		label: 'Sample: Rename property',
	},
	{
		config: addNode as Script,
		label: 'Sample: Add node',
	},
	{
		config: renameNode as Script,
		label: 'Sample: Rename node',
	},
	{
		config: deleteNodes as Script,
		label: 'Sample: Delete nodes',
	},
];
