import React, { useEffect, useId, useRef, useState } from 'react';

import { useDebounce } from '@uidotdev/usehooks';

import { Editor } from '@monaco-editor/react';
import type * as monaco from 'monaco-editor';

export const CodeEditor: React.FC<{
	value: string;
	onChange(val: string, hasErrors: boolean): void;
	language: 'json' | 'groovy' | 'jexl' | 'sql' | 'js';
	lines?: number;
}> = ({ value: outsideValue, onChange, language, lines = 13 }) => {
	const editorRef = useRef<monaco.editor.IStandaloneCodeEditor | null>(null);

	const hasError = useRef(false);
	const [value, setValue] = useState(outsideValue);
	const prevValue = useRef<string>(outsideValue);
	const debouncedValue = useDebounce(prevValue.current, 600);

	// Call the change listener when the debounced value changes
	useEffect(() => {
		if (outsideValue !== debouncedValue) {
			onChange(debouncedValue, hasError.current);
		}
	}, [debouncedValue, hasError.current]);

	// Allow value updates from outside
	useEffect(() => {
		if (editorRef.current && outsideValue !== prevValue.current) {
			prevValue.current = outsideValue;
			editorRef.current.setValue(outsideValue);
		}
	}, [outsideValue]);

	return (
		<Editor
			onMount={ed => (editorRef.current = ed)}
			defaultValue={value}
			defaultLanguage={language}
			defaultPath={`inmemory://model/${useId()}.${language}`}
			theme="light"
			options={{
				formatOnType: true,
				scrollBeyondLastLine: false,
				minimap: {
					enabled: false,
				},
				lineNumbers: 'off',
			}}
			height={lines * 20}
			onValidate={markers => {
				hasError.current = markers.length > 0;
			}}
			onChange={val => {
				prevValue.current = val ?? '';
				setValue(val ?? '');
			}}
		/>
	);
};
