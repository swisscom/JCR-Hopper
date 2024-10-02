package com.swisscom.aem.tools.impl.file;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.swisscom.aem.tools.jcrhopper.config.File;
import com.swisscom.aem.tools.jcrhopper.config.LogLevel;
import com.swisscom.aem.tools.jcrhopper.config.RunHandler;

@RequiredArgsConstructor
@Slf4j
public class FileUtils {
	private final Consumer<File> fileCreated;
	private final Map<String, Function<String, File>> fileTypeSuppliers;
	private final RunHandler runHandler;

	/**
	 * The getter that JEXL uses when it doesn’t find a field or getter for property access.
	 *
	 * @param type the type of file to create
	 * @return the file creator callable
	 */
	public DuckCallableFileCreator get(String type) {
		final Function<String, File> supplier = fileTypeSuppliers.get(type);
		if (supplier == null) {
			log.warn("File type {} not known", type);
			runHandler.log(LogLevel.WARN, "File type " + type + " not known", null, null);
			return null;
		}
		return name -> {
			final File file = supplier.apply(name);
			fileCreated.accept(file);
			return file;
		};
	}
}
