/**
 * Coral UI overrides document.createElement with a version that doesn’t understand when an object is passed as second argument.
 */
export function patchCoralUiCreateElement() {
	// eslint-disable-next-line @typescript-eslint/unbound-method
	const createElement = document.createElement;

	if (createElement === Document.prototype.createElement) {
		// Not patched, nothing to do
		return;
	}

	document.createElement = function (tagName: string, options?: ElementCreationOptions) {
		const adobeConformantOptions = typeof options === 'string' ? options : options?.is;

		return createElement.call(document, tagName, adobeConformantOptions as unknown as ElementCreationOptions);
	};
}
