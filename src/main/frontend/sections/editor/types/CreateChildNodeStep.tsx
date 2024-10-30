import React, { forwardRef } from 'react';

import { Hop } from '../../../model/hops';
import { StepEditor } from '../../../widgets/StepEditor';

import { shortDescription, title, Type, iconFor } from '../../../model/hops/createChildNode';
import { Help } from '../../../widgets/Help';
import { Input } from '../../../widgets/Input';
import { Pipeline } from '../Pipeline';
import { Conflict } from '../../../widgets/Conflict';
import { Switch } from '../../../widgets/Switch';

export const CreateChildNodeStep = forwardRef<HTMLDivElement, { parentHops: Hop[]; hop: Type }>(function CreateChildNodeStep(
	{ parentHops, hop },
	ref,
) {
	return (
		<StepEditor
			icon={iconFor(hop)}
			parentHops={parentHops}
			hop={hop}
			title={shortDescription(hop)}
			pipeline={<Pipeline hops={(hop.hops ??= [])} />}
			ref={ref}
		>
			<Input label="Child Name" value={hop.name ?? ''} onChange={name => (hop.name = name)} placeholder="name" />
			<Input
				label="JCR Primary Type"
				value={hop.primaryType ?? ''}
				onChange={primaryType => (hop.primaryType = primaryType)}
				placeholder="nt:unstructured"
			/>
			<Conflict
				label="If the target node exists"
				forceLabel="Replace the target node"
				ignoreLabel="Ignore conflict"
				value={hop.conflict ?? 'ignore'}
				onChange={conflict => (hop.conflict = conflict)}
			/>
			{hop.conflict == 'ignore' ? (
				<Switch
					label="Run on existing node"
					value={hop.runOnExistingNode}
					disabled={!hop.hops?.length}
					onChange={runOnExistingNode => (hop.runOnExistingNode = runOnExistingNode)}
				/>
			) : undefined}
			<Help title={title}>
				<h5>Name of New Child Node</h5>
				<p>The name of the child node to be created.</p>
				<p>
					Can be prepended with a path to an existing resource. Relative and absolute paths are allowed. Paths to
					non-existing parents will throw an exception.
				</p>
				<p>
					For example, a value of <code>node1/newNodeName</code> will create the node <code>newNodeName</code> as a
					child of the existing <code>node1</code> child of the current node and throw if <code>node1</code> doesn’t
					exist.
				</p>
				<h5>jcr:primaryType of new node</h5>
				<p>
					The primary type to set on the new node. If left empty, defaults to <code>nt:unstructured</code>
				</p>
				<h5>If the target node exists</h5>
				<p>How to handle the case where the target node already exists.</p>
				<h5>Run on existing node</h5>
				<p>
					Whether to run the descendant hops if the target node already existed and no new node could be created (only
					applicable if “If the target node exists” is set to “Ignore conflict”).
				</p>
			</Help>
		</StepEditor>
	);
});
