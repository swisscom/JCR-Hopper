import React, { FC } from 'react';

import { Hop } from '../../../model/hops';
import { StepEditor } from '../../../widgets/StepEditor';

import { shortDescription, title, Type } from '../../../model/hops/setProperty';
import { Help } from '../../../widgets/Help';
import { Input } from '../../../widgets/Input';
import { Conflict } from '../../../widgets/Conflict';

export const SetPropertyStep: FC<{ parentHops: Hop[]; hop: Type }> = ({ parentHops, hop }) => {
	return (
		<StepEditor parentHops={parentHops} hop={hop} title={shortDescription(hop)}>
			<Input label="Name" value={hop.propertyName ?? ''} onChange={propertyName => (hop.propertyName = propertyName)} />
			<Input label="Value" value={hop.value ?? ''} onChange={value => (hop.value = value)} />
			<Conflict
				label="If the property exists already"
				forceLabel="Overwrite the existing property"
				value={hop.conflict ?? 'ignore'}
				onChange={conflict => (hop.conflict = conflict)}
			/>
			<Help title={title}>
				<h5>Name</h5>
				<p>The name of the property to set a value for.</p>
				<p>When the property does not exist it will simply create the property with the specified value.</p>
				<p>But if the property already exist use the conflict resolution to decide what to do.</p>
				<h5>Value</h5>
				<p>
					The value of the new property as a JEXL expression. Property type will be chosen to best match the resulting
					value. The following types (or arrays/lists thereof) are supported:
					<ul className="list">
						<li>Value (value will be stored as-is)</li>
						<li>String</li>
						<li>Boolean</li>
						<li>Double</li>
						<li>Long</li>
						<li>Binary</li>
						<li>Integer (value type will be LONG)</li>
						<li>Float (value type will be DOUBLE)</li>
						<li>Byte (value type will be LONG)</li>
						<li>BigDecimal (value type will be DECIMAL)</li>
						<li>Node (value type will be REFERENCE)</li>
						<li>Calendar (value type will be DATE)</li>
						<li>InputStream (value type will be BINARY)</li>
					</ul>
					If the value is of none of the above types, its string representation will be used.
				</p>
			</Help>
		</StepEditor>
	);
};
