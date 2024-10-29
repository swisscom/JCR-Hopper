import { LogLevel } from './LogLevel';

export interface LogMessage {
	type: 'log';
	level: LogLevel;
	message: string;
	exception?: string;
}

export type MimeType = `${'text' | 'application' | 'image' | 'video' | 'audio'}/${string}` | `text/${string};charset=utf-8`;

export interface FileMessage {
	type: 'file';
	name: string;
	mime: MimeType;
	data?: string;
	blob?: Blob;
}

export type PrintMessage = string;

function decodeBase64(base64: string) {
	const raw = window.atob(base64);
	const rawLength = raw.length;
	const array = new Uint8Array(new ArrayBuffer(rawLength));

	for (let i = 0; i < rawLength; i++) {
		array[i] = raw.charCodeAt(i);
	}
	return array;
}

const DECODER = new TextDecoder();
const LINE_SEPARATOR = '\n'.charCodeAt(0);

function concat(...arrs: Uint8Array[]) {
	const length = arrs.reduce((len, arr) => len + arr.byteLength, 0);
	const result = new Uint8Array(length);
	let offset = 0;
	for (const arr of arrs) {
		result.set(arr, offset);
		offset += arr.byteLength;
	}
	return result;
}

async function* streamingFetch<T>(response: Response) {
	const { body } = response;
	if (!body) {
		console.error('Response does not have a body');
		return undefined;
	}
	// Attach Reader
	const reader = body.getReader();
	let currentChunk = Uint8Array.from([]);
	// Iterate over HTTP chunks
	while (true) {
		// wait for next chunk
		const { done, value } = await reader.read();
		if (value) {
			// Add the value we have just received
			currentChunk = concat(currentChunk, value);
		}
		// Iterate over JSON lines
		while (true) {
			// Search for the next line break
			let newlineIndex = currentChunk.indexOf(LINE_SEPARATOR);
			if (newlineIndex === -1 && done) {
				// If the stream is done, there may not be a line break at the end
				newlineIndex = currentChunk.byteLength - 1;
			}
			if (newlineIndex === -1) {
				// The current chunk does not end in a line break, await the next chunk before proceeding.
				break;
			}
			// Dec
			const text = DECODER.decode(currentChunk.subarray(0, newlineIndex + 1));
			currentChunk = currentChunk.slice(newlineIndex + 1);
			try {
				yield JSON.parse(text) as T;
			} catch (e) {
				console.error('Handling the response value', text, 'failed with', e);
			}
		}
		if (done) {
			break;
		}
	}
}

export type Message = LogMessage | FileMessage | PrintMessage;

export class Run {
	public readonly started = new Date();
	public readonly messages: Message[] = [];
	public finished = false;

	public messageHandler?: (message: Message | null) => void;

	constructor(response: Promise<Response>) {
		this.loadMessages(response).catch(e => console.error('Error loading messages', e));
	}

	private async loadMessages(response: Promise<Response>) {
		for await (const message of streamingFetch<Message>(await response)) {
			if (typeof message !== 'string') {
				if (message.type === 'file') {
					const data = message.data ? decodeBase64(message.data) : new Uint8Array(0);
					message.blob = new Blob([data], { type: message.mime });
					delete message.data;
				}
			}
			this.messageHandler?.(message);
			this.messages.push(message);
		}
		this.messageHandler?.(null);
		this.finished = true;
	}
}
