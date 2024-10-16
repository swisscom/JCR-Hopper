import { styled } from 'goober';
import React, { FC, useId, useRef } from 'react';

const Elm = styled('div')`
	justify-self: end;
	grid-column: span 2;
`;

export const Help: FC<React.PropsWithChildren<{ title: string }>> = ({ title, children }) => {
	const popoverRef = useRef<HTMLDialogElement>(null);
	const buttonId = useId();

	return (
		<Elm>
			<button is="coral-button" id={buttonId} icon="helpCircle"></button>
			<coral-popover ref={popoverRef} target={`#${CSS.escape(buttonId)}`} placement="top" style={{ marginRight: '2em' }}>
				<coral-popover-header>{title}</coral-popover-header>
				<coral-popover-content style={{ maxWidth: '400px', maxHeight: '50vh', overflow: 'auto' }} class="u-coral-padding">
					{children}
				</coral-popover-content>
			</coral-popover>
		</Elm>
	);
};
