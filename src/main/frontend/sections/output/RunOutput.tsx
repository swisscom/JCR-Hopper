import React, { FC, useEffect, useState } from 'react';
import { styled } from 'goober';

import { Run } from '../../model/Run';
import { MessageOutput } from './MessageOutput';

const Elm = styled('details')`
	border: 1px solid #c0c0c0;
	border-radius: 6px;
	padding: 1em;
	margin-bottom: 6px;
	> summary {
		display: grid;
		align-items: baseline;
		grid-auto-flow: column;
		grid-template-columns: 22px 1fr;
		&::before {
			content: '▶';
		}
		h3 {
			margin: 0;
		}
		margin: 0.5em 0;
	}
	&[open] > summary::before {
		content: '▼';
	}
	> .content {
		display: grid;
		gap: 6px;
	}
`;

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
		<Elm open>
			<summary>
				<h3>
					{finished ? (
						<span className="finished">✅</span>
					) : (
						<>
							<coral-wait></coral-wait>
						</>
					)}{' '}
					{run.started.toLocaleString()}
				</h3>
			</summary>
			<div className="content">
				{messages.map((msg, i) => (
					<MessageOutput key={i} message={msg} />
				))}
			</div>
		</Elm>
	);
};
