import React, { FC, useContext, useState } from 'react';

import { RunEndpointContext } from '../App';
import { Output } from './Output';
import { Run } from '../model/Run';
import { RunControls } from './RunControls';

export const Runner: FC = () => {
	const endpoint = useContext(RunEndpointContext);

	const [runs, setRuns] = useState<Run[]>([]);

	async function runWith(data: FormData) {
		const response = await fetch(endpoint, {
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
