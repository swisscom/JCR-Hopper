import React, { FC } from 'react';
import { Hop } from '../../model/hops';
import { PipelineStep } from './PipelineStep';

export const Pipeline: FC<{ hops: Hop[] }> = ({ hops }) => {
	return (
		<>
			{hops.map((hop, i) => (
				<PipelineStep parentHops={hops} key={i} hop={hop} />
			))}
		</>
	);
};
