package com.swisscom.aem.tools.impl;

import com.swisscom.aem.tools.jcrhopper.context.JcrFunctions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Nullable;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
@SuppressFBWarnings(value = "OPM_OVERLY_PERMISSIVE_METHOD", justification = "Used by scripting")
@SuppressWarnings("PMD.GodClass")
public class JcrFunctionsImpl implements JcrFunctions {

	private final Session session;

	/**
	 * Tries to coerce a Value object to its type.
	 *
	 * @param val  the property value to check
	 * @param type the type of the property
	 * @return the value converted to its type
	 * @throws RepositoryException If an error occurs in JCR
	 */
	private static Object objectFromValue(Value val, int type) throws RepositoryException {
		final Object result;

		switch (type) {
			case PropertyType.BOOLEAN:
				result = val.getBoolean();
				break;
			case PropertyType.DATE:
				result = val.getDate();
				break;
			case PropertyType.DECIMAL:
				result = val.getDecimal();
				break;
			case PropertyType.DOUBLE:
				result = val.getDouble();
				break;
			case PropertyType.LONG:
				result = val.getLong();
				break;
			case PropertyType.BINARY:
				result = val.getBinary();
				break;
			default:
				result = val.getString();
				break;
		}

		return result;
	}

	@Override
	public boolean hasProp(Node node, String propName) throws RepositoryException {
		return node.hasProperty(propName);
	}

	@Override
	public Value prop(Node node, String propName) throws RepositoryException {
		if (!hasProp(node, propName)) {
			return null;
		}
		return node.getProperty(propName).getValue();
	}

	@Override
	public Value[] props(Node node, String propName) throws RepositoryException {
		if (!hasProp(node, propName)) {
			return new Value[] {};
		}
		final Property prop = node.getProperty(propName);
		if (prop.isMultiple()) {
			return prop.getValues();
		}

		return new Value[] { prop.getValue() };
	}

	@Override
	public Object val(Node node, String propName) throws RepositoryException {
		final Value val = prop(node, propName);
		if (val == null) {
			return null;
		}
		return objectFromValue(val, val.getType());
	}

	@Override
	public Object[] vals(Node node, String propName) throws RepositoryException {
		if (!hasProp(node, propName)) {
			return new Object[0];
		}

		final Property prop = node.getProperty(propName);

		if (!prop.isMultiple()) {
			return new Object[] { val(node, propName) };
		}

		final int type = prop.getType();

		final Value[] values = prop.getValues();
		final List<Object> list = new ArrayList<>(values.length);
		for (Value val : values) {
			list.add(objectFromValue(val, type));
		}
		return list.toArray();
	}

	@Override
	public boolean isType(Node node, String nodeType) throws RepositoryException {
		return node.isNodeType(nodeType);
	}

	@Override
	public boolean exists(String path) throws RepositoryException {
		return exists(null, path);
	}

	@Override
	public boolean exists(@Nullable Node context, String path) throws RepositoryException {
		if (StringUtils.startsWith(path, "/")) {
			return session.nodeExists(path);
		}
		if (context == null) {
			return false;
		}
		return context.hasNode(path);
	}

	@Override
	public Node resolve(@Nullable Node context, String path) throws RepositoryException {
		if (!exists(context, path)) {
			return null;
		}
		if (StringUtils.startsWith(path, "/")) {
			return session.getNode(path);
		}
		if (context == null) {
			return null;
		}
		return context.getNode(path);
	}

	@Override
	public Node resolve(String path) throws RepositoryException {
		return resolve(null, path);
	}

	@Override
	public Binary toBinary(String data, String encoding) throws RepositoryException {
		return session.getValueFactory().createBinary(new ByteArrayInputStream(data.getBytes(Charset.forName(encoding))));
	}

	@Override
	public List<Node> parents(Node context) throws RepositoryException {
		final List<Node> results = new ArrayList<>(context.getDepth());
		Node current = context;
		while (current.getDepth() > 0) {
			current = current.getParent();
			results.add(current);
		}
		return results;
	}

	@Override
	public Value valueFromObject(Object value) throws RepositoryException {
		if (value instanceof Value) {
			return (Value) value;
		}
		final ValueFactory fac = session.getValueFactory();
		return convertToValue(fac, value);
	}

	@SuppressWarnings({ "PMD", "checkstyle:ReturnCount", "checkstyle:CyclomaticComplexity" })
	@SuppressFBWarnings(value = "ITC_INHERITANCE_TYPE_CHECKING", justification = "ValueFactory requires different signatures for each type")
	private Value convertToValue(ValueFactory fac, Object value) throws RepositoryException {
		if (value instanceof String) {
			return fac.createValue((String) value);
		} else if (value instanceof Integer) {
			return fac.createValue((Integer) value);
		} else if (value instanceof Float) {
			return fac.createValue((Float) value);
		} else if (value instanceof Byte) {
			return fac.createValue((Byte) value);
		} else if (value instanceof BigDecimal) {
			return fac.createValue((BigDecimal) value);
		} else if (value instanceof Node) {
			return fac.createValue((Node) value);
		} else if (value instanceof Calendar) {
			return fac.createValue((Calendar) value);
		} else if (value instanceof Boolean) {
			return fac.createValue((Boolean) value);
		} else if (value instanceof Double) {
			return fac.createValue((Double) value);
		} else if (value instanceof Long) {
			return fac.createValue((Long) value);
		} else if (value instanceof Binary) {
			return fac.createValue((Binary) value);
		} else if (value instanceof InputStream) {
			return fac.createValue(fac.createBinary((InputStream) value));
		} else {
			return fac.createValue(value.toString());
		}
	}

	@Override
	public Value[] valuesFromArray(Object value) throws RepositoryException {
		if (!value.getClass().isArray()) {
			return new Value[] { valueFromObject(value) };
		}

		if (value instanceof Value[]) {
			return (Value[]) value;
		}

		final Object[] objects = new Object[Array.getLength(value)];
		final int length = Array.getLength(value);
		for (int i = 0; i < length; i++) {
			objects[i] = Array.get(value, i);
		}

		final Value[] result = new Value[objects.length];
		for (int i = 0; i < objects.length; i++) {
			result[i] = valueFromObject(objects[i]);
		}

		return result;
	}

	@Override
	public Value[] valuesFromList(List<?> list) throws RepositoryException {
		final Value[] result = new Value[list.size()];
		for (int i = 0; i < list.size(); i++) {
			result[i] = valueFromObject(list.get(i));
		}
		return result;
	}

	@Override
	public Value[] toStringArray(String[]... elements) throws RepositoryException {
		return valuesFromArray(elements);
	}

	@Override
	public Value[] toLongArray(Long[]... elements) throws RepositoryException {
		return valuesFromArray(elements);
	}

	@Override
	public Object asValueType(Object value) throws RepositoryException {
		final Object result;

		if (value instanceof Value) {
			result = value;
		} else if (value instanceof Value[]) {
			result = value;
		} else if (value instanceof Property && ((Property) value).isMultiple()) {
			result = ((Property) value).getValues();
		} else if (value instanceof Property) {
			result = ((Property) value).getValue();
		} else if (value instanceof List) {
			result = valuesFromList((List<?>) value);
		} else if (value.getClass().isArray()) {
			result = valuesFromArray(value);
		} else {
			result = valueFromObject(value);
		}

		return result;
	}

	@Override
	@SuppressFBWarnings(value = "ITC_INHERITANCE_TYPE_CHECKING", justification = "Arbitration is the point of this method")
	public int size(Object any) throws RepositoryException {
		if (any instanceof List) {
			return ((List<?>) any).size();
		}
		if (any instanceof Object[]) {
			return ((Object[]) any).length;
		}
		if (any instanceof Property) {
			if (!((Property) any).isMultiple()) {
				return 1;
			}
			return ((Property) any).getValues().length;
		}
		return -1;
	}
}
