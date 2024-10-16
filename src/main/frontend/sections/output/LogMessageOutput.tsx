import React, { FC } from 'react';

import { styled } from 'goober';
import { useToggle } from '@uidotdev/usehooks';

import { LogMessage } from '../../model/Run';

const Elm = styled('div')`
	display: grid;
	grid-template-columns: min-content 1fr min-content;
	align-items: baseline;
	gap: 6px;
	> .level {
		text-transform: uppercase;
		display: inline-block;
		border: 1px solid #00000022;
		padding: 0 0.3em;
		border-radius: 8px;
		background: #ddebf7;
		&.debug {
			background: #5b9bd5;
		}
		&.info {
			background: #70ad47;
		}
		&.warn {
			background: #ffc000;
		}
		&.error {
			background: #ff3300;
			color: white;
		}
	}
	> .message {
		word-break: break-all;
	}
	> .ex-toggle {
		font-size: 1.4em;
		font-weight: bold;
		background: #ff3300;
		color: white;
		display: inline-block;
		border: 0;
		aspect-ratio: 1;
		border-radius: 50%;
		padding: 0 6px;
		text-align: center;
	}
	&:has(.exception) > .ex-toggle {
		color: #ff3300;
		background: white;
	}
	> .exception {
		grid-column: span 3;
		white-space: pre-wrap;
		word-break: break-all;
		font-family: monospace;
	}
`;

export const LogMessageOutput: FC<{ message: LogMessage }> = ({ message }) => {
	const [expanded, toggleExpanded] = useToggle(false);

	return (
		<Elm>
			<span className={`level ${message.level}`}>{message.level}</span>
			<span className="message">{message.message}</span>
			{message.exception ? (
				<button className="ex-toggle" onClick={() => toggleExpanded()}>
					!!
				</button>
			) : undefined}
			{expanded ? <div className="exception">{message.exception}</div> : undefined}
		</Elm>
	);
};
