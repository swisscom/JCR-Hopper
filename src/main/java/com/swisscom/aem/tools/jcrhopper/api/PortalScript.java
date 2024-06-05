package com.swisscom.aem.tools.jcrhopper.api;

/**
 * The PortalScript interface defines scripts which can be run from the PortalScriptServlet. A constructor with the
 * following interface is needed to make the dynamic class instantiation possible:
 * <p>
 * public ConcretePortalScript(PortalScriptContext context){}
 * </p>
 *
 * 
 */
public interface PortalScript {

	/**
	 * Subclasses must implement this method to do the actual work.
	 */
	void process() throws Exception;

	/**
	 * Returns the maximum expected duration for this script. If the duration exceeds the expected time it will be
	 * logged in the CQ task log. Subclasses must override this method and provide a sensible value.
	 *
	 * @return Expected time in milliseconds
	 */
	long getMaxDurationInMs();

	/**
	 * @return the MIME Content-Type for this script’s response
	 */
	String getResponseContentType();

}
