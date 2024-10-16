import React, { FC } from 'react';

import { useDropTarget } from '../../hooks/useDropTarget';

import { Hop } from '../../model/hops';

import { FallbackStep } from './FallbackStep';

import { ChildNodesStep } from './types/ChildNodesStep';
import { CopyNodeStep } from './types/CopyNodeStep';
import { CreateChildNodeStep } from './types/CreateChildNodeStep';
import { DeclareStep } from './types/DeclareStep';
import { EachStep } from './types/EachStep';
import { FilterNodeStep } from './types/FilterNodeStep';
import { MoveNodeStep } from './types/MoveNodeStep';
import { NodeQueryStep } from './types/NodeQueryStep';
import { RenamePropertyStep } from './types/RenamePropertyStep';
import { ReorderNodeStep } from './types/ReorderNodeStep';
import { ResolveNodeStep } from './types/ResolveNodeStep';
import { RunScriptStep } from './types/RunScriptStep';
import { SetPropertyStep } from './types/SetPropertyStep';
import { TryStep } from './types/TryStep';

export const PipelineStep: FC<{ parentHops: Hop[]; hop: Hop }> = ({ parentHops, hop }) => {
	const [ref] = useDropTarget<HTMLDivElement>(parentHops, parentHops.indexOf(hop) + 1);

	switch (hop.type) {
		case 'childNodes':
			return <ChildNodesStep parentHops={parentHops} hop={hop} ref={ref} />;
		case 'copyNode':
			return <CopyNodeStep parentHops={parentHops} hop={hop} ref={ref} />;
		case 'createChildNode':
			return <CreateChildNodeStep parentHops={parentHops} hop={hop} ref={ref} />;
		case 'declare':
			return <DeclareStep parentHops={parentHops} hop={hop} ref={ref} />;
		case 'each':
			return <EachStep parentHops={parentHops} hop={hop} ref={ref} />;
		case 'filterNode':
			return <FilterNodeStep parentHops={parentHops} hop={hop} ref={ref} />;
		case 'moveNode':
			return <MoveNodeStep parentHops={parentHops} hop={hop} ref={ref} />;
		case 'nodeQuery':
			return <NodeQueryStep parentHops={parentHops} hop={hop} ref={ref} />;
		case 'renameProperty':
			return <RenamePropertyStep parentHops={parentHops} hop={hop} ref={ref} />;
		case 'reorderNode':
			return <ReorderNodeStep parentHops={parentHops} hop={hop} ref={ref} />;
		case 'resolveNode':
			return <ResolveNodeStep parentHops={parentHops} hop={hop} ref={ref} />;
		case 'runScript':
			return <RunScriptStep parentHops={parentHops} hop={hop} ref={ref} />;
		case 'setProperty':
			return <SetPropertyStep parentHops={parentHops} hop={hop} ref={ref} />;
		case 'try':
			return <TryStep parentHops={parentHops} hop={hop} ref={ref} />;
		default:
			return <FallbackStep parentHops={parentHops} hop={hop} ref={ref} />;
	}
};
