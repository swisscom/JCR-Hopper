package com.swisscom.aem.tools.jcrhopper;

public interface File {
	String getName();

	String getMimeType();

	String getExtension();

	byte[] getContents();
}
