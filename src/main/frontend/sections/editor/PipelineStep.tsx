import React, { FC } from 'react';
import { Hop } from '../../model/hops';
import { FallbackStep } from './types/FallbackStep';
import { DeclareStep } from './types/DeclareStep';
import { ChildNodesStep } from './types/ChildNodesStep';

export const PipelineStep: FC<{ parentHops: Hop[]; hop: Hop }> = ({ parentHops, hop }) => {
	switch (hop.type) {
		case 'childNodes':
			return <ChildNodesStep parentHops={parentHops} hop={hop} />;
		case 'declare':
			return <DeclareStep parentHops={parentHops} hop={hop} />;
		default:
			return <FallbackStep parentHops={parentHops} hop={hop} />;
	}
};
