import React, { FC } from 'react';

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

export const PipelineStep: FC<{ parentHops: Hop[]; hop: Hop }> = ({ parentHops, hop }) => {
	switch (hop.type) {
		case 'childNodes':
			return <ChildNodesStep parentHops={parentHops} hop={hop} />;
		case 'copyNode':
			return <CopyNodeStep parentHops={parentHops} hop={hop} />;
		case 'createChildNode':
			return <CreateChildNodeStep parentHops={parentHops} hop={hop} />;
		case 'declare':
			return <DeclareStep parentHops={parentHops} hop={hop} />;
		case 'each':
			return <EachStep parentHops={parentHops} hop={hop} />;
		case 'filterNode':
			return <FilterNodeStep parentHops={parentHops} hop={hop} />;
		case 'moveNode':
			return <MoveNodeStep parentHops={parentHops} hop={hop} />;
		case 'nodeQuery':
			return <NodeQueryStep parentHops={parentHops} hop={hop} />;
		case 'renameProperty':
			return <RenamePropertyStep parentHops={parentHops} hop={hop} />;
		case 'reorderNode':
			return <ReorderNodeStep parentHops={parentHops} hop={hop} />;
		case 'resolveNode':
			return <ResolveNodeStep parentHops={parentHops} hop={hop} />;
		case 'runScript':
			return <RunScriptStep parentHops={parentHops} hop={hop} />;
		default:
			return <FallbackStep parentHops={parentHops} hop={hop} />;
	}
};
