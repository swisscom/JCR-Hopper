import React, { FC, useEffect, useId, useRef } from 'react';
import { Options } from './Select';
import type { CoralIcon } from '../coral/custom-elements';

export const Picker: FC<{
	items: Options;
	buttonLabel: string;
	icon: CoralIcon | undefined;
	title: string;
	picked(value: string): void;
}> = ({ items, buttonLabel, icon, title, picked }) => {
	const popoverRef = useRef<HTMLDialogElement>(null);
	const selectlistRef = useRef<HTMLSelectElement>(null);
	const buttonId = useId();

	useEffect(() => {
		const selectlist = selectlistRef.current;
		const popover = popoverRef.current;
		if (!selectlist || !popover) {
			return;
		}

		function selectlistChanged(e: Event) {
			const item = e.target as HTMLOptionElement;
			popover!.open = false;
			picked(item.value);
			item.selected = false;
		}

		selectlist.addEventListener('click', selectlistChanged);

		return () => selectlist.removeEventListener('click', selectlistChanged);
	}, [selectlistRef.current, popoverRef.current, picked]);

	return (
		<>
			<button is="coral-button" id={buttonId} {...(icon ? { icon } : {})}>
				{buttonLabel}
			</button>
			<coral-popover ref={popoverRef} target={`#${CSS.escape(buttonId)}`} placement="bottom">
				<coral-popover-header>{title}</coral-popover-header>
				<coral-popover-content onChange={console.log}>
					<coral-selectlist ref={selectlistRef}>
						{items.map(([value, label, icon]) => (
							<coral-selectlist-item key={value} value={value}>
								{icon ? <coral-icon icon={icon} /> : undefined}
								{label}
							</coral-selectlist-item>
						))}
					</coral-selectlist>
				</coral-popover-content>
			</coral-popover>
		</>
	);
};
