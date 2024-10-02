package com.swisscom.aem.tools.impl.file;

import com.swisscom.aem.tools.jcrhopper.config.File;

/**
 * Ensure the file creation calls have a <code>call</code> method because that’s how JEXL determines they’re callable.
 */
@FunctionalInterface
public interface DuckCallableFileCreator {
	/**
	 * The call method for JEXL to use.
	 *
	 * @param name the name of the file to create
	 * @return the created file
	 */
	File call(String name);
}
