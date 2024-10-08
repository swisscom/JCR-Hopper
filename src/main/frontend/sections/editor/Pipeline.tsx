import React, { FC } from 'react';
import { Hop } from '../../model/hops';

export const Pipeline: FC<{ hops: Hop[] }> = ({ hops }) => {
	return <pre>{JSON.stringify(hops, null, '\t')}</pre>;
};
