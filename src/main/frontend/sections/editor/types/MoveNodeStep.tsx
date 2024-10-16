import React, { forwardRef } from 'react';

import { Hop } from '../../../model/hops';
import { StepEditor } from '../../../widgets/StepEditor';

import { shortDescription, title, Type, iconFor } from '../../../model/hops/moveNode';
import { Help } from '../../../widgets/Help';
import { Input } from '../../../widgets/Input';
import { Conflict } from '../../../widgets/Conflict';
import { DEV_NULL } from '../../../model/hops/renameProperty';

export const MoveNodeStep = forwardRef<HTMLDivElement, { parentHops: Hop[]; hop: Type }>(function MoveNodeStep({ parentHops, hop }, ref) {
	return (
		<StepEditor icon={iconFor(hop)} parentHops={parentHops} hop={hop} title={shortDescription(hop)} ref={ref}>
			<Input
				label={
					<>
						New Name
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
				label="If the target node exists"
				forceLabel="Replace the target node"
				value={hop.conflict ?? 'ignore'}
				onChange={conflict => (hop.conflict = conflict)}
			/>
			<Help title={title}>
				<p>Move the current node to a new destination</p>
				<h5>New Name</h5>
				<p>The target name you want to move the current node to.</p>
				<p>
					Can also contain additional path segments. Can be absolute or relative (in which case it is relative to the{' '}
					<em>parent</em> of the source node).
					<br />
					The last path segment will be the name of the new node, the rest will point to its parent.
					<br />
					Thus, <code>newNodeName</code>, <code>./newNodeName</code>, <code>/absolute/path/to/newNodeName</code>,{' '}
					<code>relative/path/to/newNodeName</code>, and <code>./relative/path/to/newNodeName</code> are all valid
					values.
				</p>
				<p>
					The effective parent must already exist. Example: Target is <code>node1/newNodeName</code>
				</p>
				<ul className="list">
					<li>
						If the path <code>{'${node.parent.path}'}/node1</code> exists , <code>newNodeName</code> will be
						created as a child of the <code>node1</code> sibling of the source
					</li>
					<li>
						If <code>{'${node.parent.path}'}/node1</code> does not exist, the operation will throw.
					</li>
				</ul>
				<p>
					Setting <code>{DEV_NULL}</code> as the new name will delete the node.
				</p>
			</Help>
		</StepEditor>
	);
});
