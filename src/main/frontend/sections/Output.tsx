import React, { FC } from 'react';

import { styled } from 'goober';
import { Run } from '../model/Run';
import { RunOutput } from './output/RunOutput';

const Elm = styled('div')`
	display: block;
	overflow: auto;
`;

export const Output: FC<{ runs: Run[] }> = ({ runs }) => {
	return (
		<Elm className="output">
			<h2>Output</h2>
			{runs.map(run => (
				<RunOutput key={run.started.getTime()} run={run} />
			))}
		</Elm>
	);
};
