package com.swisscom.aem.tools.impl.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.swisscom.aem.tools.impl.hops.Declare;
import com.swisscom.aem.tools.impl.hops.RunScript;
import com.swisscom.aem.tools.jcrhopper.HopperException;
import com.swisscom.aem.tools.jcrhopper.config.Script;
import com.swisscom.aem.tools.jcrhopper.osgi.RunnerService;
import com.swisscom.aem.tools.testsupport.FileTestRunHandler;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.testing.mock.aem.junit5.JcrMockAemContext;

@ExtendWith(AemContextExtension.class)
class FileProviderExtensionTest {
	public final AemContext context = new JcrMockAemContext();
	private RunnerService runnerService;

	private FileTestRunHandler runHandler;

	@BeforeEach
	void setUp() {
		context.registerInjectActivateService(Declare.class);
		context.registerInjectActivateService(RunScript.class);

		context.registerInjectActivateService(HopProviderExtension.class);
		context.registerInjectActivateService(FileProviderExtension.class);

		runnerService = context.registerInjectActivateService(RunnerService.class);
		runHandler = new FileTestRunHandler();
	}

	@Test
	public void file_csv() throws HopperException, RepositoryException {
		runnerService.builder()
			.runHandler(runHandler)
			.build(new Script(
				new Declare.Config().withDeclarations(Collections.singletonMap("csvFile", "file:csv('testfile')")),
				new RunScript.Config().withExtension("jexl").withCode("csvFile.line('my,csv', 'one')")
			))
			.run(context.resourceResolver().adaptTo(Session.class), true);

		assertEquals(
			"testfile.csv (text/csv;charset=utf-8):\n"
				+ "\"my,csv\",one\n",
			runHandler.getLastFile()
		);
	}

	@Test
	public void file_jsonObject() throws HopperException, RepositoryException {
		runnerService.builder()
			.runHandler(runHandler)
			.build(new Script(
				new Declare.Config().withDeclarations(Collections.singletonMap("jsonFile", "file:json('my-test-json')")),
				new RunScript.Config().withExtension("jexl").withCode("jsonFile.set('myProp', 'propVal')")
			))
			.run(context.resourceResolver().adaptTo(Session.class), true);

		assertEquals(
			"my-test-json.json (application/json):\n"
				+ "{\"myProp\":\"propVal\"}",
			runHandler.getLastFile()
		);
	}

	@Test
	public void file_jsonArray() throws HopperException, RepositoryException {
		runnerService.builder()
			.runHandler(runHandler)
			.build(new Script(
				new Declare.Config().withDeclarations(Collections.singletonMap("jsonFile", "file:jsonArray('arr')")),
				new RunScript.Config().withExtension("jexl").withCode("jsonFile.append('arr', ['speak like a', 'pirate'])")
			))
			.run(context.resourceResolver().adaptTo(Session.class), true);

		assertEquals(
			"arr.json (application/json):\n"
				+ "[\"arr\",[\"speak like a\",\"pirate\"]]",
			runHandler.getLastFile()
		);
	}

	@Test
	public void file_plainText() throws HopperException, RepositoryException {
		runnerService.builder()
			.runHandler(runHandler)
			.build(new Script(
				new Declare.Config().withDeclarations(Collections.singletonMap("txt", "file:txt('README')")),
				new RunScript.Config().withExtension("jexl").withCode("txt.append('## Usage');\ntxt.println();\ntxt.append('Use it however you want.')")
			))
			.run(context.resourceResolver().adaptTo(Session.class), true);

		assertEquals(
			"README.txt (text/plain;charset=utf-8):\n"
				+ "## Usage\n"
				+ "Use it however you want.",
			runHandler.getLastFile()
		);
	}

	@Test
	public void file_plainTextCustomType() throws HopperException, RepositoryException {
		runnerService.builder()
			.runHandler(runHandler)
			.build(new Script(
				new Declare.Config().withDeclarations(Collections.singletonMap("txt", "file:txt('main')")),
				new RunScript.Config().withExtension("jexl").withCode("txt.mimeType = 'text/javascript';\ntxt.extension = 'js';\ntxt.append('\"use strict\"')")
			))
			.run(context.resourceResolver().adaptTo(Session.class), true);

		assertEquals(
			"main.js (text/javascript):\n"
				+ "\"use strict\"",
			runHandler.getLastFile()
		);
	}
}
