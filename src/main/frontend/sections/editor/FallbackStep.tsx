import React, { forwardRef, useContext } from 'react';

import { Hop } from '../../model/hops';
import { StepEditor } from '../../widgets/StepEditor';
import { ScriptContext } from '../../App';
import { CodeEditor } from '../../widgets/CodeEditor';

export const FallbackStep = forwardRef<HTMLDivElement, { parentHops: Hop[]; hop: Hop }>(function FallbackStep({ parentHops, hop }, ref) {
	const scriptContext = useContext(ScriptContext);

	const { type: hopType, ...hopWithoutType } = hop;
	const code = JSON.stringify(hopWithoutType, null, '  ');

	return (
		<StepEditor parentHops={parentHops} hop={hop} title={`Unknown Hop (${hopType})`} ref={ref}>
			<CodeEditor
				value={code}
				onChange={(value, hasError) => {
					if (hasError) {
						return;
					}
					try {
						Object.assign(hop, JSON.parse(value), { type: hopType });
						scriptContext.commit();
					} catch (e) {
						// Invalid JSON
					}
				}}
				language="json"
			/>
		</StepEditor>
	);
});
