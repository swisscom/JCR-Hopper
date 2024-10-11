import React, { FC, useContext } from 'react';

import { Hop } from '../../model/hops';
import { StepEditor } from '../../widgets/StepEditor';
import { ScriptContext } from '../../App';
import { CodeEditor } from '../../widgets/CodeEditor';

export const FallbackStep: FC<{ parentHops: Hop[]; hop: Hop }> = ({ parentHops, hop }) => {
	const scriptContext = useContext(ScriptContext);

	const { type: hopType, ...hopWithoutType } = hop;
	const code = JSON.stringify(hopWithoutType, null, '  ');

	return (
		<StepEditor parentHops={parentHops} hop={hop} title={`Unknown Hop (${hopType})`}>
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
};
