import React, { FC } from 'react';

import { styled } from 'goober';

import { FileMessage, MimeType } from '../../model/Run';
import { CoralIcon } from '../../coral/custom-elements';

const Elm = styled('div')`
	background: #666;
	color: white;
	padding: 1em;
	white-space: pre-wrap;
	word-break: break-all;
	border: 2px solid #0002;
	border-radius: 12px;
	display: grid;
	gap: 6px;
	grid-template-areas: 'icon name name download' 'icon type size download';
	grid-template-columns: max-content max-content 1fr max-content;
	> coral-icon {
		grid-area: icon;
		position: relative;
		top: 0.3em;
	}
	> .name {
		grid-area: name;
	}
	> .type {
		grid-area: type;
	}
	> .size {
		grid-area: size;
	}
	> a {
		grid-area: download;
	}
`;

function iconFor(mimeType: MimeType): CoralIcon {
	const [kind, ...rest] = mimeType.split('/');
	let [type] = rest;
	[type] = type!.split(';');
	let icon: CoralIcon = 'file';
	if (kind === 'application') {
		icon = 'fileCode';
		if (type === 'pdf') {
			icon = 'filePDF';
		}
		if (type === 'json') {
			icon = 'fileJson';
		}
		if (type === 'xhtml+xml') {
			icon = 'fileHTML';
		}
		if (['zip', 'vnd.rar', 'x-compressed', 'x-gzip-compressed'].includes(type!)) {
			icon = 'fileZip';
		}
	}
	if (kind === 'text') {
		icon = 'fileTxt';
		if (type === 'csv') {
			icon = 'fileCSV';
		}
		if (type === 'html') {
			icon = 'fileHTML';
		}
	}
	if (kind === 'image') {
		icon = 'image';
	}

	return icon;
}

export const FileMessageOutput: FC<{ message: FileMessage }> = ({ message }) => {
	const icon = iconFor(message.mime);
	const blob = message.blob!;

	return (
		<Elm>
			<coral-icon icon={icon} size="L" />
			<span className="name">{message.name}</span>
			<small className="type">{message.mime}</small>
			<small className="size">{blob.size} bytes</small>
			<a href={URL.createObjectURL(blob)} download={message.name}>
				<button type="button" is="coral-button" icon="download">
					Download
				</button>
			</a>
		</Elm>
	);
};
