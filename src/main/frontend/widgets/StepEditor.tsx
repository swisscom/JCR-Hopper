import React, { FC, ReactNode, useContext } from 'react';

import { Hop } from '../model/hops';
import { styled } from 'goober';
import { ScriptContext } from '../App';

const Elm = styled('div')`
	margin-top: 4px;
	counter-increment: steps;
	position: relative;
	--accent-color: #5c5c5c;
	--contrast-color: white;

	> details > .edit .row {
		flex-wrap: nowrap;
		> :first-child {
			flex-shrink: 1;
		}
	}

	&.childNodes {
		--accent-color: #0eaba9;
	}
	&.copyNode {
		--accent-color: #db16a0;
	}
	&.createChildNode {
		--accent-color: #086adb;
	}
	&.declare {
		--accent-color: rgb(175, 175, 175);
		--contrast-color: black;
	}
	&.each {
		--accent-color: rgb(99, 28, 28);
	}
	&.filterNode {
		--accent-color: #5944c6;
	}
	&.moveNode {
		--accent-color: #a63297;
	}
	&.nodeQuery {
		--accent-color: #e61e64;
	}
	&.renameProperty {
		--accent-color: #ff8b2e;
		--contrast-color: black;
	}
	&.reorderNode {
		--accent-color: #f7fa31;
		--contrast-color: black;
	}
	&.resolveNode {
		--accent-color: #1b8712;
	}
	&.runScript {
		--accent-color: #3a6677;
	}
	&.setProperty {
		--accent-color: #a2cdf4;
		--contrast-color: black;
	}
	&.try {
		--accent-color: #b1b9be;
		--contrast-color: black;
	}

	details {
		border-radius: 4px;
		background-color: var(--accent-color);

		summary {
			display: grid;
			grid-template-columns: auto 1fr auto;
			color: var(--contrast-color);
			cursor: pointer;

			font-size: 18px;
			> * {
				font-size: inherit;
			}

			&::-webkit-details-marker {
				display: none;
			}

			&::before {
				display: inline-block;
				align-self: center;
				content: counter(steps);
				color: var(--contrast-color);
				min-width: 2em;
				text-align: center;
			}

			h2 {
				display: inline-block;
				white-space: nowrap;
				font-weight: normal;
				align-self: center;
				padding: 2px 5px;
				overflow: hidden;
				margin: 0;
				text-overflow: ellipsis;
			}

			.menu {
				display: grid;
				grid-auto-flow: column;
				gap: 6px;
				margin: 6px;
			}
		}
		.edit {
			margin-left: 4px;
			padding: 10px;
			padding-right: 10%;
			background: #ffffffce;
			display: grid;
			gap: 6px;
		}

		&[open] {
			summary {
				/* with multiple color stop lengths */
				background-image: repeating-linear-gradient(-45deg, transparent 0 8px, rgba(255, 255, 255, 0.2) 8px 16px);
				.menu .remove {
					border-radius: 0 4px 0 0;
				}
			}
		}
	}

	.sub-pipeline {
		padding-left: 2em;
		margin-top: -4px;
		padding-top: 4px;
		border-left: solid 4px var(--accent-color);
		border-radius: 0 0 0 4px;

		> .add-hop {
			display: inline-block;
			padding-top: 3px;
			margin: 16px 0 0 calc(-2em - 4px);
			background-color: var(--accent-color);
			border: none;
			border-radius: 0 4px 4px 4px;
			color: white;
			cursor: pointer;
		}
	}
`;

export const StepEditor: FC<{ parentHops: Hop[]; hop: Hop; title: string; children: ReactNode; pipeline?: ReactNode }> = ({
	parentHops,
	hop,
	title,
	children,
	pipeline,
}) => {
	const scriptContext = useContext(ScriptContext);

	const hopIndex = parentHops.indexOf(hop);

	function moveHop(isUp = false) {
		parentHops.splice(hopIndex, 1);
		parentHops.splice(hopIndex + (isUp ? -1 : 1), 0, hop);
		scriptContext.commit();
	}

	return (
		<Elm className={`hop-config ${hop.type}`}>
			<details open={false}>
				<summary draggable="true">
					<h2>{title}</h2>
					<div className="menu">
						<button
							is="coral-button"
							icon="arrowUp"
							disabled={hopIndex === 0 ? true : undefined}
							onClick={moveHop.bind(null, true)}
						></button>
						<button
							is="coral-button"
							icon="arrowDown"
							disabled={hopIndex >= parentHops.length - 1 ? true : undefined}
							onClick={moveHop.bind(null, false)}
						></button>
						<button
							is="coral-button"
							icon="duplicate"
							onClick={() => {
								parentHops.splice(hopIndex, 0, hop);
								scriptContext.commit();
							}}
						></button>
						<button
							is="coral-button"
							variant="warning"
							icon="delete"
							onClick={() => {
								parentHops.splice(hopIndex, 1);
								scriptContext.commit();
							}}
						></button>
					</div>
				</summary>
				<div className="edit">{children}</div>
			</details>
			{pipeline ? <div className="pipeline sub-pipeline">{pipeline}</div> : undefined}
		</Elm>
	);
};
