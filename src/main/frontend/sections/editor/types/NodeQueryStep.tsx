import React, { forwardRef, useContext } from 'react';

import { Hop } from '../../../model/hops';
import { StepEditor } from '../../../widgets/StepEditor';

import { QUERY_TYPE_LABELS, shortDescription, title, Type, iconFor } from '../../../model/hops/nodeQuery';
import { Help } from '../../../widgets/Help';
import { Pipeline } from '../Pipeline';
import { Select } from '../../../widgets/Select';
import { Input } from '../../../widgets/Input';
import { CodeEditor } from '../../../widgets/CodeEditor';
import { ScriptContext } from '../../../App';

export const NodeQueryStep = forwardRef<HTMLDivElement, { parentHops: Hop[]; hop: Type }>(function NodeQueryStep({ parentHops, hop }, ref) {
	const scriptContext = useContext(ScriptContext);

	return (
		<StepEditor
			icon={iconFor(hop)}
			parentHops={parentHops}
			hop={hop}
			title={shortDescription(hop)}
			pipeline={<Pipeline hops={(hop.hops ??= [])} />}
			ref={ref}
		>
			<CodeEditor
				language="sql"
				lines={5}
				value={hop.query}
				onChange={query => {
					hop.query = query;
					scriptContext.commit();
				}}
			/>
			<Select
				value={hop.queryType}
				label="Query Language"
				list={Object.entries(QUERY_TYPE_LABELS) as [Type['queryType'], string][]}
				onChange={queryType => (hop.queryType = queryType)}
			/>
			<Input
				label="Counter Name"
				value={hop.counterName ?? ''}
				placeholder="counter"
				onChange={counterName => (hop.counterName = counterName)}
			/>
			<Input label="Selector Name" value={hop.selectorName ?? ''} onChange={selectorName => (hop.selectorName = selectorName)} />
			<Input
				label="Limit"
				value={String(hop.limit ?? '')}
				placeholder="0"
				onChange={limit => (hop.limit = limit ? Number(limit) : undefined)}
				type="number"
			/>
			<Input
				label="Offset"
				value={String(hop.offset ?? '')}
				placeholder="0"
				onChange={offset => (hop.offset = offset ? Number(offset) : undefined)}
				type="number"
			/>
			<Help title={title}>
				<h5>Query</h5>
				<p>
					The XPath or SQL2 Query you want to run.
					<br />
					<small>
						For SQL2 refer to <a href="http://drfits.com/jcr-sql2-query-with-examples/">this guide</a>.
					</small>
				</p>
				<h5>Query Language</h5>
				<p>What the query is written in.</p>
				<h5>Counter Name</h5>
				<p>
					Declare a counter variable with a name that can be used inside EL expressions. The counter starts at 0 and is
					incremented by 1 every loop iteration.
				</p>
				<h5>Selector Name</h5>
				<p>
					When a query selects multiple nodes per row (e.g. in a JOIN), this property defines which of them is to be
					used for the child pipeline. Other selected nodes will still be available inside EL expressions under the name
					used in the query.
				</p>
				<h5>Limit/Offset</h5>
				<p>If set and {'>'} 0, these define how many results are returned and how many are skipped.</p>
			</Help>
		</StepEditor>
	);
});
