export default {
	/*
	 * Resolve and load @commitlint/config-conventional from node_modules.
	 * Referenced packages must be installed
	 */
	extends: ['@commitlint/config-conventional'],

	/*
	 * Any rules defined here will override rules from @commitlint/config-conventional
	 */
	rules: {
		'scope-enum': [2, 'always', ['editor', 'meta', 'runner']],
		'scope-empty': [0, 'never'],
	},
};
