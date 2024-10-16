import { useContext, useEffect, useRef } from 'react';

import { Hop } from '../model/hops';

import { ScriptContext } from '../App';
import { DROPPED_METHOD_KEY, HOP_REFERENCE_TYPE, JSON_TYPES } from '../widgets/DropZone';
import { Script } from '../model/Script';

import { HistoryUpdater } from './useHistoryImmutable';
import { jsonToHops } from '../model/jsonToHops';

export const DRAGGED_HOP: { hop: Hop | null; parentHops: Hop[] | null } = {
	hop: null,
	parentHops: null,
};

export function dataToString(jsonData: DataTransferItem): Promise<string> {
	return new Promise((resolve, _reject) => {
		if (jsonData.kind === 'string') {
			jsonData.getAsString(str => resolve(str));
		} else if (jsonData.kind === 'file') {
			const jsonFile = jsonData.getAsFile()!;
			const reader = new FileReader();
			reader.addEventListener('load', () => {
				resolve(reader.result as string);
			});
			reader.readAsText(jsonFile);
		}
	});
}

function handleHopDrop(hopData: string, targetHopList: Hop[], targetHopPosition: number, scriptContext: HistoryUpdater<Script>, isCopy = false) {
	const { hop, parentHops } = DRAGGED_HOP;
	DRAGGED_HOP.hop = null;
	DRAGGED_HOP.parentHops = null;
	if (isCopy) {
		// Use JSON representation of dragged hop to avoid cycles in case a hop was copied into itself
		targetHopList.splice(targetHopPosition, 0, JSON.parse(hopData) as Hop);
		scriptContext.commit();
		return;
	}

	if (!hop || !parentHops) {
		console.error('Origin of dragged hop', hopData, 'not known');
		return;
	}

	const prevPosition = parentHops.indexOf(hop);
	const isSameList = targetHopList === parentHops;
	if (isSameList) {
		if (targetHopPosition === prevPosition || targetHopPosition === prevPosition + 1) {
			// Item was moved before or after itself ➞ nothing changes
			return;
		}
	}
	//
	parentHops.splice(prevPosition, 1);
	if (isSameList && targetHopPosition > prevPosition) {
		// Account for the removed element
		targetHopPosition--;
	}

	targetHopList.splice(targetHopPosition, 0, hop);
	scriptContext.commit();
}

async function handleFileDrop(data: DataTransfer, targetHopList: Hop[], targetHopPosition: number, scriptContext: HistoryUpdater<Script>) {
	const items = [...data.items];
	let item: DataTransferItem | undefined;
	for (const type of JSON_TYPES) {
		item = items.find(it => it.type === type);
		if (item) {
			break;
		}
	}

	if (!item) {
		console.error('Data', data, 'do not contain usable items');
		return;
	}

	const json = await dataToString(item);
	const hops = jsonToHops(json);

	targetHopList.splice(targetHopPosition, 0, ...hops);
	scriptContext.commit();
}

export function useDropTarget<E extends HTMLElement>(hops: Hop[], position = 0) {
	const scriptContext = useContext(ScriptContext);
	const ref = useRef<E>(null);

	useEffect(() => {
		if (ref.current) {
			ref.current[DROPPED_METHOD_KEY] = (data, isCopy) => {
				if (data.types.includes(HOP_REFERENCE_TYPE)) {
					handleHopDrop(data.getData(HOP_REFERENCE_TYPE), hops, position, scriptContext, isCopy);
				} else {
					void handleFileDrop(data, hops, position, scriptContext);
				}
			};
		}
	}, [hops, position, ref.current]);

	return [ref] as const;
}
