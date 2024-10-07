import React, { FC } from 'react';

import { styled } from 'goober';

const Elm = styled('div')`
	grid-auto-rows: min-content 1fr;
`;

export const Output: FC = () => {
	return (
		<Elm className="output">
			<h2>Output</h2>
		</Elm>
	);
};
