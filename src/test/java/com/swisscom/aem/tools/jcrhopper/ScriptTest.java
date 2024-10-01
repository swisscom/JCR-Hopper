package com.swisscom.aem.tools.jcrhopper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import com.swisscom.aem.tools.impl.hops.ChildNodes;
import com.swisscom.aem.tools.impl.hops.CreateChildNode;
import com.swisscom.aem.tools.impl.hops.ResolveNode;
import com.swisscom.aem.tools.impl.hops.SetProperty;

import org.skyscreamer.jsonassert.JSONAssert;

class ScriptTest {
	@Test
	public void fromJson() throws IOException {
		final String json = IOUtils.resourceToString("/json/pipeline-config.json", StandardCharsets.UTF_8);
		final Script script = RunnerTest.RUNNER_BUILDER.scriptFromJson(json);

		assertEquals(LogLevel.TRACE, script.getLogLevel());
		final List<HopConfig> hops = script.getHops();
		assertEquals(5, hops.size());

		assertTrue(hops.get(0) instanceof SetProperty.Config);
		assertTrue(hops.get(1) instanceof CreateChildNode.Config);
		assertTrue(hops.get(2) instanceof ResolveNode.Config);
		assertTrue(hops.get(3) instanceof ChildNodes.Config);
		assertTrue(hops.get(4) instanceof ResolveNode.Config);

		assertEquals("Script(hops=["
			+ "SetProperty.Config(propertyName=sling:resourceType, value='swisscom/sdx/components/containers/tabs', conflict=FORCE), "
			+ "CreateChildNode.Config(name=contents, primaryType=nt:unstructured, conflict=IGNORE, hops=["
				+ "CreateChildNode.Config(name=shared, primaryType=nt:unstructured, conflict=IGNORE, hops=["
					+ "SetProperty.Config(propertyName=sling:resourceType, value='swisscom/sdx/components/responsivegrid', conflict=FORCE)"
				+ "])"
			+ "]), "
			+ "ResolveNode.Config(name=angularApp, conflict=IGNORE, hops=[MoveNode.Config(newName=./contents/shared/angularapppicker, conflict=IGNORE)]), "
			+ "ChildNodes.Config(namePattern=tabpar*, counterName=item, hops=[MoveNode.Config(newName=./contents/${item}, conflict=IGNORE)]), "
			+ "ResolveNode.Config(name=tabNames, conflict=IGNORE, hops=["
				+ "MoveNode.Config(newName=tabs, conflict=IGNORE), "
				+ "ChildNodes.Config(namePattern=null, counterName=tab, hops=["
					+ "SetProperty.Config(propertyName=tabContentId, value=tab, conflict=IGNORE), "
					+ "RenameProperty.Config(propertyName=tabname, newName=tabTitle, doesNotExist=IGNORE, conflict=IGNORE), "
					+ "SetProperty.Config(propertyName=hideSharedContent, value=!jcr:val(node, 'isEnabledAngularApp'), conflict=IGNORE), "
					+ "RenameProperty.Config(propertyName=isEnabledAngularApp, newName=/dev/null, doesNotExist=IGNORE, conflict=IGNORE), "
					+ "RenameProperty.Config(propertyName=trackApp, newName=appRouteValue, doesNotExist=IGNORE, conflict=IGNORE), "
					+ "RenameProperty.Config(propertyName=trackApp, newName=/dev/null, doesNotExist=FORCE, conflict=IGNORE)"
				+ "])"
			+ "])"
		+ "], logLevel=TRACE)", script.toString());


		JSONAssert.assertEquals(json, RunnerTest.RUNNER_BUILDER.scriptToJson(script), true);
	}
}