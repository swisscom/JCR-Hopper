import { useState } from 'react';

export const useOnce = <T>(f: () => T) => {
	const [v] = useState(f);
	return v;
};
