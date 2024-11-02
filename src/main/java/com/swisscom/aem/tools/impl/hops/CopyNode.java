package com.swisscom.aem.tools.impl.hops;

import com.swisscom.aem.tools.jcrhopper.HopperException;
import com.swisscom.aem.tools.jcrhopper.config.ConflictResolution;
import com.swisscom.aem.tools.jcrhopper.config.Hop;
import com.swisscom.aem.tools.jcrhopper.config.HopConfig;
import com.swisscom.aem.tools.jcrhopper.context.HopContext;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.commons.xml.ToXmlContentHandler;
import org.osgi.service.component.annotations.Component;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

@Slf4j
@Component(service = Hop.class)
@SuppressFBWarnings
public class CopyNode implements Hop<CopyNode.Config> {

	@Override
	public void run(Config config, Node node, HopContext context) throws RepositoryException, HopperException {
		if (StringUtils.equals(node.getPath(), "/")) {
			throw new HopperException("Copying the root node isn’t allowed");
		}

		final String newName = context.evaluateTemplate(config.newName);
		final MoveNode.NewNodeDescriptor descriptor = MoveNode.resolvePathToNewNode(node.getParent(), newName, config.conflict, context);
		if (descriptor.isTargetExists()) {
			return;
		}

		final Session session = node.getSession();

		try {
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			final ToXmlContentHandler serializingContentHandler = new ToXmlContentHandler(os);
			session.exportSystemView(
				node.getPath(),
				new RootRenamingContentHandler(serializingContentHandler, node.getName(), descriptor.getNewChildName()),
				false,
				false
			);
			// Remove the replaced node only after the snapshot has been taken because it’s possible it was part of the copied tree
			descriptor.removeReplacedNode();

			context.info("Copying node from {} to {}", node.getPath(), descriptor.getAbsolutePath());

			session.importXML(
				descriptor.getParent().getPath(),
				new ByteArrayInputStream(os.toByteArray()),
				ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW
			);
		} catch (IOException | SAXException e) {
			throw new HopperException("Importing the node at " + descriptor.getAbsolutePath() + " failed", e);
		}

		context.runHops(session.getNode(descriptor.getAbsolutePath()), config.hops);
	}

	@Nonnull
	@Override
	public Class<Config> getConfigType() {
		return Config.class;
	}

	@Nonnull
	@Override
	public String getConfigTypeName() {
		return "copyNode";
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@With
	@ToString
	@EqualsAndHashCode
	@SuppressWarnings("PMD.ImmutableField")
	public static final class Config implements HopConfig {

		private String newName;

		@Nonnull
		private ConflictResolution conflict = ConflictResolution.IGNORE;

		@Nonnull
		private List<HopConfig> hops = Collections.emptyList();
	}

	@RequiredArgsConstructor
	private static final class RootRenamingContentHandler implements ContentHandler {

		@Delegate(types = ContentHandler.class)
		private final ContentHandler inner;

		private final String oldName;
		private final String newName;

		private boolean isRootNode = true;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
			Attributes usedAttributes = atts;
			if (isRootNode) {
				final AttributesImpl attributesImpl = new AttributesImpl(usedAttributes);
				final int index = attributesImpl.getIndex("sv:name");
				if (index == -1) {
					log.warn("sv:name attribute not found on element {}", qName);
				} else if (StringUtils.equals(attributesImpl.getValue(index), oldName)) {
					attributesImpl.setValue(index, newName);
					usedAttributes = attributesImpl;
				} else {
					log.warn("sv:name expected to be {} but is {}", oldName, attributesImpl.getValue(index));
				}
			}

			isRootNode = false;
			inner.startElement(uri, localName, qName, usedAttributes);
		}
	}
}
