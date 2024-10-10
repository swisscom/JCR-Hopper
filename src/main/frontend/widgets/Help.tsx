import { styled } from 'goober';
import React, { FC, useId, useRef } from 'react';

const Elm = styled('div')`
	justify-self: end;
`;

export const Help: FC<React.PropsWithChildren<{ title: string }>> = ({ title, children }) => {
	const popoverRef = useRef<HTMLDialogElement>(null);
	const buttonId = useId();

	return (
		<Elm>
			<button is="coral-button" id={buttonId} icon="helpCircle"></button>
			<coral-popover ref={popoverRef} target={`#${CSS.escape(buttonId)}`} placement="bottom">
				<coral-popover-header>{title}</coral-popover-header>
				<coral-popover-content style={{ maxWidth: '400px' }}>{children}</coral-popover-content>
			</coral-popover>
		</Elm>
	);
};
