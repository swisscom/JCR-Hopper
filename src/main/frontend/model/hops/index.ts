import * as childNodes from './childNodes';
import * as copyNode from './copyNode';
import * as createChildNode from './createChildNode';
import * as declare from './declare';
import * as each from './each';
import * as filterNode from './filterNode';
import * as moveNode from './moveNode';
import * as nodeQuery from './nodeQuery';
import * as renameProperty from './renameProperty';
import * as reorderNode from './reorderNode';
import * as resolveNode from './resolveNode';
import * as runScript from './runScript';
import * as setProperty from './setProperty';
import * as tryAction from './try';

export const HOP_DEFINITIONS = {
	childNodes,
	copyNode,
	createChildNode,
	declare,
	each,
	filterNode,
	moveNode,
	nodeQuery,
	renameProperty,
	reorderNode,
	resolveNode,
	runScript,
	setProperty,
	try: tryAction,
} as const;

export type ConflictResolutionStrategy = 'ignore' | 'force' | 'throw';

export type ActionType = keyof typeof HOP_DEFINITIONS;

export interface AnyHop {
	type: ActionType;
}
export type Hop =
	| childNodes.Type
	| copyNode.Type
	| createChildNode.Type
	| declare.Type
	| each.Type
	| filterNode.Type
	| moveNode.Type
	| nodeQuery.Type
	| renameProperty.Type
	| reorderNode.Type
	| resolveNode.Type
	| runScript.Type
	| setProperty.Type
	| tryAction.Type;
