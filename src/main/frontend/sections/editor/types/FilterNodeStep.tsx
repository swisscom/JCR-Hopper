import React, { FC } from 'react';

import { Hop } from '../../../model/hops';
import { StepEditor } from '../../../widgets/StepEditor';

import { shortDescription, title, Type } from '../../../model/hops/filterNode';
import { Help } from '../../../widgets/Help';
import { Input } from '../../../widgets/Input';
import { Pipeline } from '../Pipeline';

export const FilterNodeStep: FC<{ parentHops: Hop[]; hop: Type }> = ({ parentHops, hop }) => {
	return (
		<StepEditor parentHops={parentHops} hop={hop} title={shortDescription(hop)} pipeline={<Pipeline hops={(hop.hops ??= [])} />}>
			<Input
				label="JEXL Expression"
				value={hop.expression ?? ''}
				onChange={expression => (hop.expression = expression)}
				placeholder="[]"
			/>
			<Help title={title}>
				<h5>Filter Expression</h5>
				<p>
					The expression that needs to evaluate to <code>true</code> to apply the sub-pipeline to the current node.
				</p>
				<p>This field expects an expression, thus surrounding the expression with {'${}'} is invalid.</p>
				<p>
					With the word <code>node</code> you can reference the current node.
				</p>
			</Help>
		</StepEditor>
	);
};
