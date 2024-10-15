import React, { forwardRef } from 'react';

import { Hop } from '../../../model/hops';
import { StepEditor } from '../../../widgets/StepEditor';

import { shortDescription, title, Type } from '../../../model/hops/each';
import { Help } from '../../../widgets/Help';
import { Input } from '../../../widgets/Input';
import { Pipeline } from '../Pipeline';
import { Switch } from '../../../widgets/Switch';

export const EachStep = forwardRef<HTMLDivElement, { parentHops: Hop[]; hop: Type }>(function EachStep({ parentHops, hop }, ref) {
	return (
		<StepEditor
			parentHops={parentHops}
			hop={hop}
			title={shortDescription(hop)}
			pipeline={<Pipeline hops={(hop.hops ??= [])} />}
			ref={ref}
		>
			<Switch value={hop.assumeNodes ?? false} label="Iterate Nodes" onChange={assumeNodes => (hop.assumeNodes = assumeNodes)} />
			<Input
				label="JEXL Expression"
				value={hop.expression ?? ''}
				onChange={expression => (hop.expression = expression)}
				placeholder="[]"
			/>
			<Input label="Iterator Name" value={hop.iterator ?? ''} onChange={iterator => (hop.iterator = iterator)} placeholder="item" />
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
});
