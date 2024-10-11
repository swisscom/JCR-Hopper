import React, { FC } from 'react';

import { Hop } from '../../model/hops';

import { ChildNodesStep } from './types/ChildNodesStep';
import { CopyNodeStep } from './types/CopyNodeStep';
import { DeclareStep } from './types/DeclareStep';

import { FallbackStep } from './types/FallbackStep';

export const PipelineStep: FC<{ parentHops: Hop[]; hop: Hop }> = ({ parentHops, hop }) => {
	switch (hop.type) {
		case 'childNodes':
			return <ChildNodesStep parentHops={parentHops} hop={hop} />;
		case 'copyNode':
			return <CopyNodeStep parentHops={parentHops} hop={hop} />;
		case 'declare':
			return <DeclareStep parentHops={parentHops} hop={hop} />;
		default:
			return <FallbackStep parentHops={parentHops} hop={hop} />;
	}
};
