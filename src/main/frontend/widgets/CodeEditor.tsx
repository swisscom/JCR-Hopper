import React, { useEffect, useId, useRef, useState } from 'react';

import { useDebounce } from '@uidotdev/usehooks';

import { Editor } from '@monaco-editor/react';
import type * as monaco from 'monaco-editor';

export const CodeEditor: React.FC<{
	value: string;
	changed(val: string, hasErrors: boolean): void;
	language: 'json' | 'groovy' | 'jexl';
}> = ({ value: outsideValue, changed, language }) => {
	const editorRef = useRef<monaco.editor.IStandaloneCodeEditor | null>(null);

	const hasError = useRef(false);
	const [value, setValue] = useState(outsideValue);
	const prevValue = useRef<string>(outsideValue);
	const debouncedValue = useDebounce(prevValue.current, 600);

	// Call the change listener when the debounced value changes
	useEffect(() => {
		if (outsideValue !== debouncedValue) {
			changed(debouncedValue, hasError.current);
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
			}}
			height={250}
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
