import React, { FC } from 'react';

import { Hop } from '../../../model/hops';
import { StepEditor } from '../../../widgets/StepEditor';

import { shortDescription, title, Type } from '../../../model/hops/try';
import { Help } from '../../../widgets/Help';
import { Pipeline } from '../Pipeline';
import { Switch } from '../../../widgets/Switch';

export const TryStep: FC<{ parentHops: Hop[]; hop: Type }> = ({ parentHops, hop }) => {
	return (
		<StepEditor parentHops={parentHops} hop={hop} title={shortDescription(hop)} pipeline={<Pipeline hops={(hop.hops ??= [])} />}>
			<Switch
				value={hop.catchGeneric ?? false}
				label="Catch Generic"
				onChange={catchGeneric => (hop.catchGeneric = catchGeneric)}
			/>
			<Help title={title}>
				<p>
					Normaly when an exception occurs in any of your piplines your complete script fails and nothing will be
					peristed to jcr. With try you have the option to wrap a pipeline in a try that will allow all actions in other
					piplines to continue and succeed even if there is an exception within the pipeline that is wrapped in the try
					action.
				</p>
				<h5>Catch Generic</h5>
				<p>
					By default only <code>HopException</code>s occuring, for example, from JEXL errors or exceptions from
					explicitly configured hops will be caught. If you want to catch all errors, enable <i>Catch Generic</i>.
				</p>
			</Help>
		</StepEditor>
	);
};
