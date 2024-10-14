import React, { FC, useContext } from 'react';

import { Hop, HOP_DEFINITIONS, HopType } from '../../model/hops';
import { PipelineStep } from './PipelineStep';
import { Picker } from '../../widgets/Picker';
import { ScriptContext } from '../../App';

export const Pipeline: FC<{ hops: Hop[]; addButton?: boolean }> = ({ hops, addButton = true }) => {
	const scriptContext = useContext(ScriptContext);

	return (
		<>
			{hops.map((hop, i) => (
				<PipelineStep parentHops={hops} key={i} hop={hop} />
			))}
			{addButton ? (
				<>
					<Picker
						buttonAttributes={{ className: 'add-hop' }}
						buttonLabel={<coral-icon icon="addCircle"></coral-icon>}
						placement="right"
						title="Hop"
						items={Object.entries(HOP_DEFINITIONS).map(([type, definition]) => [type, definition.title])}
						picked={(type: HopType) => {
							const hop: Hop = {
								type,
								...HOP_DEFINITIONS[type],
							} as Hop;
							hops.push(hop);
							scriptContext.commit();
						}}
					/>
				</>
			) : undefined}
		</>
	);
};
