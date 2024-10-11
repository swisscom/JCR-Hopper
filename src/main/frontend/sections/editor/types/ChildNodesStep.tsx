import React, { FC } from 'react';

import { Hop } from '../../../model/hops';
import { StepEditor } from '../../../widgets/StepEditor';

import { shortDescription, title, Type } from '../../../model/hops/childNodes';
import { Help } from '../../../widgets/Help';
import { Input } from '../../../widgets/Input';
import { Pipeline } from '../Pipeline';

export const ChildNodesStep: FC<{ parentHops: Hop[]; hop: Type }> = ({ parentHops, hop }) => {
	return (
		<StepEditor parentHops={parentHops} hop={hop} title={shortDescription(hop)} pipeline={<Pipeline hops={(hop.hops ??= [])} />}>
			<Input
				label="Name Pattern"
				value={hop.namePattern ?? ''}
				onChange={namePattern => (hop.namePattern = namePattern)}
				placeholder="*"
			/>
			<Input
				label="Counter Name"
				value={hop.counterName ?? ''}
				onChange={counterName => (hop.counterName = counterName)}
				placeholder="index"
			/>
			<Help title={title}>
				<h5>Name Pattern</h5>
				<p>
					Gets all child nodes of this node accessible through the current Session that match namePattern. The pattern
					may be a full name, a partial name with one or more wildcard characters (*), or a disjunction of those (using
					the <code className="code font--serif">|</code> character).
				</p>
				<p>
					For example, <code className="code font--serif">jcr:* | myapp:report | my doc</code> will run the pipeline
					actions for each accessible child node that is either called{' '}
					<code className="code font--serif">myapp:report</code>, <code className="code font--serif">my doc</code>, or
					whose name begins with the prefix <code className="code font--serif">jcr:</code>.
				</p>
				<p>
					For more information, see <a href="https://adobe.ly/2YrfG1G">Node#getNodes(String)</a>
				</p>
				<p>If left empty, will return all all children of node.</p>
				<h5>Counter Name</h5>
				<p>
					Declare a counter variable with a name. When the pipeline iterates over the result set, each action in the
					sub-pipeline will have access to this variable and may use it in an expression like{' '}
					<code>{'${yourVariableName}'}</code>. The counter starts at 0 and is incremented by 1 every loop iteration.
				</p>
			</Help>
		</StepEditor>
	);
};
