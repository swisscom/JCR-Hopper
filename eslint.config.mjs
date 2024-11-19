import react from 'eslint-plugin-react';
import reactHooks from 'eslint-plugin-react-hooks';
import typescriptEslint from '@typescript-eslint/eslint-plugin';
import { fixupPluginRules } from '@eslint/compat';
import globals from 'globals';
import tsParser from '@typescript-eslint/parser';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import js from '@eslint/js';
import { FlatCompat } from '@eslint/eslintrc';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const compat = new FlatCompat({
	baseDirectory: __dirname,
	recommendedConfig: js.configs.recommended,
	allConfig: js.configs.all,
});

export default [
	{
		ignores: ['build/**/*'],
	},
	...compat.extends('eslint:recommended', 'plugin:react/recommended', 'plugin:@typescript-eslint/recommended', 'prettier'),
	{
		plugins: {
			react,
			'react-hooks': fixupPluginRules(reactHooks),
			'@typescript-eslint': typescriptEslint,
		},

		languageOptions: {
			globals: {
				...globals.browser,
			},

			parser: tsParser,
			ecmaVersion: 'latest',
			sourceType: 'module',
		},

		settings: {
			react: {
				createClass: 'createReactClass',
				pragma: 'React',
				fragment: 'Fragment',
				version: 'detect',
			},

			componentWrapperFunctions: ['styled'],
		},

		rules: {
			'@typescript-eslint/no-non-null-assertion': 0,
			'@typescript-eslint/no-unused-vars': 0,
			'react/prop-types': 0,
		},
	},
];
