import React, { FC } from 'react';

import { Hop } from '../../../model/hops';
import { StepEditor } from '../../../widgets/StepEditor';

import { shortDescription, title, Type } from '../../../model/hops/each';
import { Help } from '../../../widgets/Help';
import { Input } from '../../../widgets/Input';
import { Pipeline } from '../Pipeline';
import { Switch } from '../../../widgets/Switch';

export const EachStep: FC<{ parentHops: Hop[]; hop: Type }> = ({ parentHops, hop }) => {
	return (
		<StepEditor parentHops={parentHops} hop={hop} title={shortDescription(hop)} pipeline={<Pipeline hops={(hop.hops ??= [])} />}>
			<Switch value={hop.assumeNodes ?? false} label="Iterate Nodes" onChange={assumeNodes => (hop.assumeNodes = assumeNodes)} />
			<label>
				JEXL Expression:{' '}
				<Input value={hop.expression ?? ''} onChange={expression => (hop.expression = expression)} placeholder="[]" />
			</label>
			<label>
				Iterator Name:{' '}
				<Input value={hop.iterator ?? ''} onChange={iterator => (hop.iterator = iterator)} placeholder="item" />
			</label>
			<Help title={title}>
				<h5>Iterate Nodes</h5>
				<p>
					If the values produced by your expression are either nodes or resolvable paths, set this to true to use these
					nodes as the context for the sub-pipeline.
				</p>
				<h5>JEXL Expression</h5>
				<p>The sub-pipeline given will be repeated for every element this expression produces.</p>
				<h5>Iterator</h5>
				<p>This is the name of the variable used to address the individual item.</p>
			</Help>
		</StepEditor>
	);
};
