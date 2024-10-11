import React, { FC } from 'react';

import { Hop } from '../../../model/hops';
import { StepEditor } from '../../../widgets/StepEditor';

import { shortDescription, title, Type } from '../../../model/hops/reorderNode';
import { Help } from '../../../widgets/Help';
import { Input } from '../../../widgets/Input';
import { Conflict } from '../../../widgets/Conflict';

export const ReorderNodeStep: FC<{ parentHops: Hop[]; hop: Type }> = ({ parentHops, hop }) => {
	return (
		<StepEditor parentHops={parentHops} hop={hop} title={shortDescription(hop)}>
			<Input label="Name of Sibling" value={hop.before ?? ''} onChange={before => (hop.before = before)} />
			<Conflict
				label="If no sibling with the provided name exists"
				forceLabel="Ignore but do not warn"
				value={hop.conflict ?? 'ignore'}
				onChange={conflict => (hop.conflict = conflict)}
			/>
			<Help title={title}>
				<h5>Name of Sibling</h5>
				<p>The name of the sibling you wish your current node to appear before.</p>
				<p>
					Must be the name of an existing node located under the same parent or empty to order the current node last in
					the list of siblings.
				</p>
			</Help>
		</StepEditor>
	);
};
