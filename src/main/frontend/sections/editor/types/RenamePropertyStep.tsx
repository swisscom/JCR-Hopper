import React, { forwardRef } from 'react';

import { Hop } from '../../../model/hops';
import { StepEditor } from '../../../widgets/StepEditor';

import { shortDescription, title, Type, DEV_NULL } from '../../../model/hops/renameProperty';
import { Help } from '../../../widgets/Help';
import { Input } from '../../../widgets/Input';
import { Conflict } from '../../../widgets/Conflict';

export const RenamePropertyStep = forwardRef<HTMLDivElement, { parentHops: Hop[]; hop: Type }>(function RenamePropertyStep({ parentHops, hop }, ref) {
	return (
		<StepEditor parentHops={parentHops} hop={hop} title={shortDescription(hop)} ref={ref}>
			<Input label="Property Name" value={hop.propertyName ?? ''} onChange={propertyName => (hop.propertyName = propertyName)} />
			<Input
				label={
					<>
						New Name of Property
						<br />
						<small>
							(use <code>{DEV_NULL}</code> to delete)
						</small>
					</>
				}
				value={hop.newName ?? ''}
				onChange={newName => (hop.newName = newName)}
			/>
			<Conflict
				label="If the source property does not exist"
				forceLabel="Ignore but do not warn"
				value={hop.doesNotExist ?? 'ignore'}
				onChange={doesNotExist => (hop.doesNotExist = doesNotExist)}
			/>
			<Conflict
				label="If the target property exists"
				forceLabel="Overwrite the existing property"
				value={hop.conflict ?? 'ignore'}
				onChange={conflict => (hop.conflict = conflict)}
			/>
			<Help title={title}>
				<h5>Current Name of the Property</h5>
				<p>The name of the property you want to change.</p>
				<h5>New Name of the Property</h5>
				<p>The new name of the property</p>
				<p>
					Setting <code>{DEV_NULL}</code> as the new name will delete the property.
				</p>
			</Help>
		</StepEditor>
	);
});
