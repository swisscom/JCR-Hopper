package com.swisscom.aem.tools.jcrhopper.config;

/**
 * Interface representing a file generated during the run of a script.
 */
public interface File {
	/**
	 * @return the name of the file, excluding its extension
	 */
	String getName();

	/**
	 * @return the mime type of the file
	 */
	String getMimeType();

	/**
	 * @return the file extension
	 */
	String getExtension();

	/**
	 * @return the file’s contents in binary
	 */
	byte[] getContents();
}
