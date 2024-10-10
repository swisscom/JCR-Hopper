import React, { FC, useContext, useEffect, useRef, useState } from 'react';

import { AnyHop, Hop } from '../../../model/hops';
import { StepEditor } from '../../../widgets/StepEditor';
import { ScriptContext } from '../../../App';

export const FallbackStep: FC<{ parentHops: Hop[]; hop: AnyHop }> = ({ parentHops, hop }) => {
	const scriptContext = useContext(ScriptContext);

	const { type: _, ...hopWithoutType } = hop;
	const cleaned = JSON.stringify(hopWithoutType, null, '  ');

	const [code, setCode] = useState(cleaned);
	const updated = useRef(false);

	useEffect(() => {
		setCode(cleaned);
		updated.current = false;
	}, [hop]);

	return (
		<StepEditor parentHops={parentHops} hop={hop} title={`Unknown hop type ${hop.type}`}>
			<textarea
				cols={120}
				rows={10}
				onInput={e => {
					updated.current = true;
					setCode(e.currentTarget.value);
				}}
				value={code}
				onBlur={() => {
					if (!updated.current) {
						// No change since last set
						return;
					}
					try {
						const editedSettings = JSON.parse(code);

						Object.assign(hop, editedSettings, { type: hop.type });
						scriptContext.commit();
					} catch (e) {
						// Not currently valid JSON
					}
				}}
				style={{ fontFamily: 'monospace' }}
			/>
		</StepEditor>
	);
};
