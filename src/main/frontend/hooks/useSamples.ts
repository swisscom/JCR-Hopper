import { Sample, SAMPLES } from '../model/samples';
import { useEffect, useState } from 'react';
import { Script } from '../model/Script';

export const useSamples = (): [Sample[], boolean] => {
	const [samples, setSamples] = useState<Sample[]>([]);
	const [loading, setLoading] = useState(false);

	useEffect(() => {
		const loadSamples = async () => {
			setLoading(true);
			try {
				const response = await fetch('/apps/jcr-hopper/script-builder/samples/additional-samples.json');
				if (!response.ok) throw new Error(`Failed to fetch samples: ${response.statusText}`);

				const fetchedData = await response.json();
				if (Array.isArray(fetchedData)) {
					const additionalSamples = fetchedData.map(item => ({
						label: item.label,
						config: item.config as Script,
					}));
					setSamples([...SAMPLES, ...additionalSamples]);
				} else {
					console.error('Fetched data is not an array:', fetchedData);
				}
			} catch (error) {
				console.error('Error fetching additional samples:', error);
			} finally {
				setLoading(false);
			}
		};
		loadSamples();
	}, []);

	return [samples, loading];
};
