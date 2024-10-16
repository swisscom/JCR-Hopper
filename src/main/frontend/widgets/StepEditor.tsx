import React, { forwardRef, ReactNode, useContext } from 'react';

import { Hop } from '../model/hops';
import { styled } from 'goober';
import { ScriptContext } from '../App';
import { DRAGGED_HOP } from '../hooks/useDropTarget';
import { HOP_REFERENCE_TYPE } from './DropZone';
import { CoralIcon } from '../coral/custom-elements';

const Elm = styled('div', forwardRef)`
	margin-top: 6px;
	counter-increment: steps;
	position: relative;
	--accent-color: #5c5c5c;
	--contrast-color: white;

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

	> details {
		border-radius: 4px;
		background-color: var(--accent-color);

		summary {
			display: grid;
			grid-template-columns: auto 1fr auto;
			&:has(> coral-icon) {
				grid-template-columns: auto auto 1fr auto;
			}

			> coral-icon {
				align-self: center;
				margin: 0.2em;
				margin-bottom: 0;
			}

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
				align-self: center;
				content: counter(steps);
				color: var(--contrast-color);
				min-width: 2em;
				text-align: center;
			}

			h2 {
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
			padding-right: 30px;
			background: #ffffffce;
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
		counter-reset: steps;

		.add-hop {
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

export const StepEditor = forwardRef<
	HTMLDivElement,
	{ parentHops: Hop[]; hop: Hop; title: string; children: ReactNode; pipeline?: ReactNode; icon?: CoralIcon }
>(function StepEdtitor({ parentHops, hop, title, children, pipeline, icon }, ref) {
	const scriptContext = useContext(ScriptContext);

	const hopIndex = parentHops.indexOf(hop);

	function moveHop(isUp = false) {
		parentHops.splice(hopIndex, 1);
		parentHops.splice(hopIndex + (isUp ? -1 : 1), 0, hop);
		scriptContext.commit();
	}

	return (
		<Elm className={`hop-config ${hop.type}`} ref={ref}>
			<details open={false}>
				<summary
					draggable="true"
					onDragStart={event => {
						const dataTransfer = event.dataTransfer!;
						dataTransfer.effectAllowed = 'copyMove';
						dataTransfer.dropEffect = 'move';
						DRAGGED_HOP.hop = hop;
						DRAGGED_HOP.parentHops = parentHops;
						dataTransfer.setData(HOP_REFERENCE_TYPE, JSON.stringify(hop));
						dataTransfer.setDragImage(
							event.target as Element,
							event.nativeEvent.layerX,
							event.nativeEvent.layerY,
						);
					}}
				>
					{icon ? <coral-icon icon={icon}></coral-icon> : undefined}
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
				<div className="edit field-container">{children}</div>
			</details>
			{pipeline ? <div className="pipeline sub-pipeline">{pipeline}</div> : undefined}
		</Elm>
	);
});
