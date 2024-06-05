package com.swisscom.aem.tools.jcrhopper;

import java.util.concurrent.ConcurrentSkipListSet;

import lombok.extern.slf4j.Slf4j;

/**
 * Cancel the (Scheduled) task(s) registered under it's classname.
 * <p>
 * If the implementation is prepared in the framework as in ScheduledTask, you can use the cancellation of tasks as follows.
 */
@Slf4j
public final class MyTask {
	/**
	 * The ThreadLocal variable is used to provide the classname for the isCancelled() method for developer's convenience.
	 */
	public static final ThreadLocal<String> CLASSNAME = ThreadLocal.withInitial(() -> null);

	private static final ConcurrentSkipListSet<String> TASKS = new ConcurrentSkipListSet<>();

	private MyTask() {
		// Private constructor to prevent instantiation.
	}


	/**
	 * @return true if the task was canceled, else false.
	 */
	public static boolean isCancelled() {
		if (getName() == null) {
			throw new IllegalArgumentException("ThreadLocal classname is not setup for thread cancellation.");
		}
		return TASKS.contains(getName());
	}

	/**
	 * Send cancellation request to the tasks registered under their classname.
	 * <p>
	 * NOTE: all instances of the same Class (tasks) are requested to cancel with this call.
	 *
	 * @param name The fully qualified classname (e.g. my.package.MyScheduledTask)
	 */
	public static void cancel(String name) {
		TASKS.add(name);
	}

	/**
	 * Reset cancellation state to enable next cancellation request.
	 *
	 * @param name The fully qualified classname of Task to reset.
	 */
	public static void resetCancellation(String name) {
		TASKS.remove(name);
	}

	/**
	 * Lookup the classname from ThreadLocal for the isCancelled() method if set-up in the framework.
	 *
	 * @return the classname
	 */
	public static String getName() {
		return CLASSNAME.get();
	}

	/**
	 * Remove the classname from ThreadLocal.
	 */
	public static void unregisterTaskName() {
		CLASSNAME.remove();
	}

	/**
	 * Set the classname on ThreadLocal to enable the calling of isCancelled() without passing the classname.
	 *
	 * @param name The fully qualified classname of the task to be canceled.
	 */
	public static void registerTaskName(String name) {
		CLASSNAME.set(name);
	}
}
