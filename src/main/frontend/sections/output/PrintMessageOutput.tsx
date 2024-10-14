import React, { FC } from 'react';

import { styled } from 'goober';

import { PrintMessage } from '../../model/Run';

const Elm = styled('div')`
	background: #ccc;
	padding: 0.4em;
	white-space: pre-wrap;
	word-break: break-all;
	border: 2px solid #0002;
	border-radius: 12px;
`;

export const PrintMessageOutput: FC<{ message: PrintMessage }> = ({ message }) => {
	return <Elm>{message}</Elm>;
};
