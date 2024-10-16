import React, { FC, forwardRef, useContext } from 'react';

import { styled } from 'goober';

import { Hop, HOP_DEFINITIONS, HopType } from '../../model/hops';
import { PipelineStep } from './PipelineStep';
import { Picker } from '../../widgets/Picker';
import { ScriptContext } from '../../App';
import { useDropTarget } from '../../hooks/useDropTarget';

const Elm = styled('div', forwardRef)`
	position: relative;
	min-height: 1.5em;
`;

export const Pipeline: FC<{ hops: Hop[]; addButton?: boolean }> = ({ hops, addButton = true }) => {
	const scriptContext = useContext(ScriptContext);
	const [ref] = useDropTarget<HTMLDivElement>(hops, 0);

	return (
		<>
			<Elm className="hop-list" ref={ref}>
				{hops.map((hop, i) => (
					<PipelineStep parentHops={hops} key={i} hop={hop} />
				))}
			</Elm>
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
								...HOP_DEFINITIONS[type].defaultConfig,
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
