import React, { FC, PropsWithChildren } from 'react';
import type { DragEvent } from 'react';

import { styled } from 'goober';

const Elm = styled('div')`
	.drop-target {
		&::before {
			content: '';
			display: block;
			height: 2px;
			margin: 2px 0;
			background-color: rgba(0, 0, 0, 0.3);
			position: absolute;
			left: 0;
			right: 0;
		}

		&.hop-list::before {
			top: -5px;
		}

		&.hop-list:empty::before {
			top: 0;
		}

		&.hop-config::before {
			bottom: -5px;
		}
	}
`;

export const DROPPED_METHOD_KEY = Symbol('dropped');

declare global {
	interface Element {
		[DROPPED_METHOD_KEY]?(data: DataTransfer, isCopy: boolean): void;
	}
}

let currentDropZone: Element | undefined;

function setDropZone(dropZone?: Element | null) {
	if (dropZone === currentDropZone) {
		return;
	}
	if (currentDropZone) {
		currentDropZone.classList.remove('drop-target');
	}
	if (dropZone) {
		dropZone.classList.add('drop-target');
	}
	currentDropZone = dropZone || undefined;
}

let currentDraggable: Element | undefined;

function dragStart(event: DragEvent) {
	if (event.eventPhase !== Event.BUBBLING_PHASE) {
		return;
	}
	currentDraggable = event.target as Element;
}

function dragEnd() {
	setDropZone();
	currentDraggable = undefined;
}

export const HOP_REFERENCE_TYPE = 'application/vnd.jcrhopper.hop-reference';
export const JSON_TYPES = ['text/plain', 'application/json', HOP_REFERENCE_TYPE];

export function isValidData(data: DataTransfer) {
	return [...data.items].some(item => JSON_TYPES.includes(item.type));
}

function dragOver(dropZoneClass: string, event: DragEvent) {
	const data = event.dataTransfer!;
	if (!isValidData(data)) {
		setDropZone();
		return;
	}

	const target = event.target as Node;
	let closestDropZone: Element | null = target.nodeType === 1 ? (target as Element) : target.parentElement;
	while (
		// We have a valid drop zone
		closestDropZone &&
		// The drop zone is not of the desired type
		(!closestDropZone.classList.contains(dropZoneClass) ||
			// The current drop zone is inside the draggable
			// (allowed for copy but denied for move)
			(event.dataTransfer!.dropEffect !== 'copy' &&
				// FIXME: Abstract away this implementation detail
				currentDraggable?.parentElement?.parentElement?.contains(closestDropZone)))
	) {
		// Search for the next outer target
		closestDropZone = closestDropZone.parentElement;
	}

	if (!closestDropZone) {
		setDropZone(closestDropZone);
		return;
	}

	// Find the correct position inside the drop zone
	const y = event.nativeEvent.layerY;
	const children = Array.from(closestDropZone.children);
	for (const child of children) {
		if (!child.contains(target)) {
			continue;
		}
		// Place drop before or after element?
		const isBefore = y < child.clientHeight / 2;
		if (isBefore) {
			closestDropZone = child.previousElementSibling || closestDropZone;
		} else {
			closestDropZone = child;
		}
		break;
	}

	setDropZone(closestDropZone);

	event.preventDefault();
}

function notifyDrop(data: DataTransfer, isCopy = false) {
	currentDropZone?.[DROPPED_METHOD_KEY]?.(data, isCopy);
}

function dropped(event: DragEvent) {
	if (!currentDropZone) {
		return;
	}

	const data = event.dataTransfer!;

	// Try to get JSON data from native drag (file/text)
	if (!isValidData(data)) {
		setDropZone();
		return;
	}

	event.preventDefault();

	const isCopy = data.dropEffect === 'copy';
	notifyDrop(data, isCopy);
	dragEnd();
}

export const DropZone: FC<PropsWithChildren<{ dropZoneClass: string }>> = ({ dropZoneClass, children }) => {
	const overHandler = dragOver.bind(null, dropZoneClass);

	return (
		<Elm
			onDragStart={dragStart}
			onDragOver={overHandler}
			onDragEnter={overHandler}
			onDragExit={setDropZone.bind(null, undefined)}
			onDragEnd={dragEnd}
			onDropCapture={dropped}
		>
			{children}
		</Elm>
	);
};
