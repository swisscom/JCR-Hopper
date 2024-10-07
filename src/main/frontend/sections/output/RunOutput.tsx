import React, { FC, useEffect, useState } from 'react';
import { styled } from 'goober';

import { Run } from '../../model/Run';

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
			<h3>{run.started.toLocaleString('de-CH')}</h3>
			{finished ? undefined : <coral-wait></coral-wait>}
			{messages.map((msg, i) => (
				<pre key={i}>{JSON.stringify(msg)}</pre>
			))}
		</Elm>
	);
};
