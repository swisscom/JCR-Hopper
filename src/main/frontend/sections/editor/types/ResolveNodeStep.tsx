import React, { FC } from 'react';

import { Hop } from '../../../model/hops';
import { StepEditor } from '../../../widgets/StepEditor';

import { shortDescription, title, Type } from '../../../model/hops/resolveNode';
import { Help } from '../../../widgets/Help';
import { Input } from '../../../widgets/Input';
import { Pipeline } from '../Pipeline';
import { Conflict } from '../../../widgets/Conflict';

export const ResolveNodeStep: FC<{ parentHops: Hop[]; hop: Type }> = ({ parentHops, hop }) => {
	return (
		<StepEditor parentHops={parentHops} hop={hop} title={shortDescription(hop)} pipeline={<Pipeline hops={(hop.hops ??= [])} />}>
			<Input label="Path" value={hop.name ?? ''} onChange={name => (hop.name = name)} />
			<Conflict
				value={hop.conflict ?? 'ignore'}
				onChange={conflict => (hop.conflict = conflict)}
				label="If the node does not exist"
				forceLabel="Ignore but do not warn"
			/>
			<Help title={title}>
				<h5>Path</h5>
				<p>The absolute or relative path to an existing node.</p>
			</Help>
		</StepEditor>
	);
};
