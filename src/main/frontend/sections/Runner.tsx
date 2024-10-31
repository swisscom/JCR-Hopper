import React, { FC, useContext, useState } from 'react';

import { EnvironmentContext } from '../App';
import { Output } from './Output';
import { Run } from '../model/Run';
import { RunControls } from './RunControls';

export const Runner: FC = () => {
	const environmentContext = useContext(EnvironmentContext);

	const [runs, setRuns] = useState<Run[]>([]);

	async function runWith(data: FormData) {
		const response = fetch(environmentContext.runEndpoint, {
			method: 'POST',
			body: data,
		});
		setRuns([...runs, new Run(response)]);
	}

	return (
		<>
			<RunControls runWith={runWith} />
			<Output runs={runs} />
		</>
	);
};
