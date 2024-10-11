import React, { FC } from 'react';

import { Hop } from '../../../model/hops';
import { StepEditor } from '../../../widgets/StepEditor';

import { shortDescription, Type } from '../../../model/hops/createChildNode';
import { Help } from '../../../widgets/Help';
import { Input } from '../../../widgets/Input';
import { Pipeline } from '../Pipeline';
import { Conflict } from '../../../widgets/Conflict';

export const CreateChildNodeStep: FC<{ parentHops: Hop[]; hop: Type }> = ({ parentHops, hop }) => {
	return (
		<StepEditor parentHops={parentHops} hop={hop} title={shortDescription(hop)} pipeline={<Pipeline hops={(hop.hops ??= [])} />}>
			<label>
				Child Name: <Input value={hop.name ?? ''} onChange={name => (hop.name = name)} placeholder="name" />
			</label>
			<label>
				JCR Primary Type:{' '}
				<Input
					value={hop.primaryType ?? ''}
					onChange={primaryType => (hop.primaryType = primaryType)}
					placeholder="nt:unstructured"
				/>
			</label>
			<Conflict
				label="If the target node exists"
				forceLabel="Replace the target node"
				value={hop.conflict ?? 'ignore'}
				changed={conflict => (hop.conflict = conflict)}
			/>
			<Help title="Child Nodes">
				<h5>Name of New Child Node</h5>
				<p>The name of the child node to be created.</p>
				<p>
					Can be prepended with a path to an existing resource. Relative and absolute paths are allowed. Paths to
					non-existing parents will throw an exception.
				</p>
				<p>
					For example, a value of <code className="code font--serif">node1/newNodeName</code> will create the node{' '}
					<code className="code font--serif">newNodeName</code> as a child of the existing{' '}
					<code className="code font--serif">node1</code> child of the current node and throw if{' '}
					<code className="code font--serif">node1</code> doesn’t exist.
				</p>
				<h5>jcr:primaryType of new node</h5>
				<p>
					The primary type to set on the new node. If left empty, defaults to{' '}
					<code className="code font--serif">nt:unstructured</code>
				</p>
				<h5>If the target node exists</h5>
				<p>
					How to handle the case where the target node already exists. Note that choosing “Ignore conflict” will use the
					existing node to run descendent pipeline steps on. To stop the descendent pipeline from running in this case,
					choose “Throw an exception” and place this step inside a “Catch Pipeline Errors” step.
				</p>
			</Help>
		</StepEditor>
	);
};
