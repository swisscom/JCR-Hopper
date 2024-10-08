import React, { FC } from 'react';

import { Message } from '../../model/Run';

export const MessageOutput: FC<{ message: Message }> = ({ message }) => {
	return <pre>{JSON.stringify(message)}</pre>;
};
