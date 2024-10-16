package com.swisscom.aem.tools.impl.extension;

import com.swisscom.aem.tools.impl.file.CsvFile;
import com.swisscom.aem.tools.impl.file.JsonArrayFile;
import com.swisscom.aem.tools.impl.file.JsonObjectFile;
import com.swisscom.aem.tools.impl.file.PlainTextFile;
import com.swisscom.aem.tools.jcrhopper.RunnerBuilder;
import com.swisscom.aem.tools.jcrhopper.osgi.RunnerBuilderExtension;
import org.osgi.service.component.annotations.Component;

@Component(service = RunnerBuilderExtension.class)
public class FileProviderExtension implements RunnerBuilderExtension {

	@Override
	public void configure(RunnerBuilder builder) {
		builder.registerFile("json", JsonObjectFile::new);
		builder.registerFile("jsonArray", JsonArrayFile::new);
		builder.registerFile("csv", CsvFile::new);
		builder.registerFile("txt", PlainTextFile::new);
	}
}
