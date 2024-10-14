import React, { FC } from 'react';

import { Message } from '../../model/Run';
import { PrintMessageOutput } from './PrintMessageOutput';
import { FileMessageOutput } from './FileMessageOutput';
import { LogMessageOutput } from './LogMessageOutput';

export const MessageOutput: FC<{ message: Message }> = ({ message }) => {
	if (typeof message === 'string') {
		return <PrintMessageOutput message={message} />;
	}
	if (message.type === 'file') {
		return <FileMessageOutput message={message} />;
	}
	return <LogMessageOutput message={message} />;
};
