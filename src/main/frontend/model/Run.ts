import { LogLevel } from './LogLevel';

interface LogMessage {
	type: 'log';
	level: LogLevel;
	message: string;
	exception?: string;
}

interface FileMessage {
	type: 'file';
	name: string;
	mime: string;
	data?: string;
	blob?: Blob;
}

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

async function* streamingFetch<T>(response: Response) {
	const { body } = response;
	if (!body) {
		console.error('Response does not have a body');
		return undefined;
	}
	// Attach Reader
	const reader = body.getReader();
	while (true) {
		// wait for next encoded chunk
		const { done, value } = await reader.read();
		// check if stream is done
		if (done) break;
		try {
			// Decodes data chunk and yields it
			const text = DECODER.decode(value).split('\n');
			for (const t of text) {
				const data = JSON.parse(t) as T;
				yield data;
			}
		} catch (e) {
			console.error('Handling the response value', value, 'failed with', e);
		}
	}
}

type PrintMessage = string;

type Message = LogMessage | FileMessage | PrintMessage;

export class Run {
	public readonly started = new Date();
	public readonly messages: Message[] = [];
	public finished = false;

	public messageHandler?: (message: Message | null) => void;

	constructor(response: Response) {
		this.loadMessages(response).catch(e => console.error('Error loading messages', e));
	}

	private async loadMessages(response: Response) {
		for await (const message of streamingFetch<Message>(response)) {
			if (typeof message !== 'string') {
				if (message.type === 'file' && message.data) {
					message.blob = new Blob([decodeBase64(message.data)], { type: message.mime });
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
