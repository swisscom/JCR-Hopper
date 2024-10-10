import React, { FC } from 'react';
import { Hop } from '../../model/hops';
import { FallbackStep } from './types/FallbackStep';

export const PipelineStep: FC<{ parentHops: Hop[]; hop: Hop }> = ({ parentHops, hop }) => {
	switch (hop.type) {
		default:
			return <FallbackStep parentHops={parentHops} hop={hop} />;
	}
};
