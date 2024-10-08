import React, { FC, useEffect, useState } from 'react';
import { styled } from 'goober';

import { Run } from '../../model/Run';
import { MessageOutput } from './MessageOutput';

const Elm = styled('div')``;

export const RunOutput: FC<{ run: Run }> = ({ run }) => {
	const [messages, setMessages] = useState(run.messages);
	const [finished, setFinished] = useState(run.finished);

	useEffect(() => {
		run.messageHandler = message => {
			if (message === null) {
				setFinished(true);
			} else {
				setMessages(prev => [...prev, message]);
			}
		};
	}, [run]);

	return (
		<Elm>
			<h3>
				{finished ? undefined : (
					<>
						<coral-wait></coral-wait>{' '}
					</>
				)}
				{run.started.toLocaleString('de-CH')}
			</h3>

			{messages.map((msg, i) => (
				<MessageOutput key={i} message={msg} />
			))}
		</Elm>
	);
};
