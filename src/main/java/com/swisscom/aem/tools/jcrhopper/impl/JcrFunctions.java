package com.swisscom.aem.tools.jcrhopper.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
@SuppressWarnings("PMD.GodClass")
public class JcrFunctions {
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

	/**
	 * Checks if a property exists.
	 *
	 * @param node     The node whose property you want to check
	 * @param propName The name of the property to read
	 * @return true if the referenced property exists
	 * @throws RepositoryException If an error occurs in JCR
	 */
	public boolean hasProp(Node node, String propName) throws RepositoryException {
		return node.hasProperty(propName);
	}

	/**
	 * Gets a property from a node or null if the property does not exist.
	 * Useful if you want to coerce the property to a different type than its native one.
	 *
	 * @param node     The node whose property you want to check
	 * @param propName The name of the property to read
	 * @return The value object of the property referenced by propName on node or null
	 * @throws RepositoryException If an error occurs in JCR
	 */
	public Value prop(Node node, String propName) throws RepositoryException {
		if (!hasProp(node, propName)) {
			return null;
		}
		return node.getProperty(propName).getValue();
	}

	/**
	 * Gets a multi-valued property from a node or null if the property does not exist.
	 * Useful if you want to coerce the property to a different type than its native one.
	 *
	 * @param node     The node whose property you want to check
	 * @param propName The name of the property to read
	 * @return The value object of the property referenced by propName on node or null
	 * @throws RepositoryException If an error occurs in JCR
	 */
	public Value[] props(Node node, String propName) throws RepositoryException {
		if (!hasProp(node, propName)) {
			return null;
		}
		final Property prop = node.getProperty(propName);
		if (prop.isMultiple()) {
			return prop.getValues();
		}

		return new Value[]{prop.getValue()};
	}

	/**
	 * Returns the value of a property or null if the property does not exist.
	 *
	 * @param node     The node whose property you want to check
	 * @param propName The name of the property to read
	 * @return The value of the property referenced by propName on node converted to its type or null
	 * @throws RepositoryException If an error occurs in JCR
	 */
	public Object val(Node node, String propName) throws RepositoryException {
		final Value val = prop(node, propName);
		if (val == null) {
			return null;
		}
		return objectFromValue(val, val.getType());
	}

	/**
	 * Returns an array for the values of the given property (empty if the property does not exist).
	 * To check if the property exists, use hasProp.
	 *
	 * @param node     The node whose property you want to check
	 * @param propName The name of the property to read
	 * @return an array of the property values (all have the same type)
	 * @throws RepositoryException If an error occurs in JCR
	 */
	public Object[] vals(Node node, String propName) throws RepositoryException {
		if (!hasProp(node, propName)) {
			return new Object[0];
		}

		final Property prop = node.getProperty(propName);

		if (!prop.isMultiple()) {
			return new Object[]{val(node, propName)};
		}

		final int type = prop.getType();

		final List<Object> list = new ArrayList<>();
		for (Value val : prop.getValues()) {
			list.add(objectFromValue(val, type));
		}
		return list.toArray();
	}

	/**
	 * Checks if a node matches a given type either as primary or mixin.
	 *
	 * @param node     The node whose type you want to check
	 * @param nodeType The type to check
	 * @return true if the node matches the specified type
	 * @throws RepositoryException If an error occurs in JCR
	 */
	public boolean isType(Node node, String nodeType) throws RepositoryException {
		return node.isNodeType(nodeType);
	}

	/**
	 * Checks if a node exists at a given absolute path.
	 *
	 * @param path The absolute path to resolve
	 * @return true if a node exists at the path
	 * @throws RepositoryException If an error occurs in JCR
	 */
	public boolean exists(String path) throws RepositoryException {
		return exists(null, path);
	}

	/**
	 * Checks if a node exists at a given path.
	 *
	 * @param context The node from which the path should be resolved
	 * @param path    The absolute or relative path to resolve
	 * @return true if a node exists at the path
	 * @throws RepositoryException If an error occurs in JCR
	 */
	public boolean exists(@Nullable Node context, String path) throws RepositoryException {
		if (StringUtils.startsWith(path, "/")) {
			return session.nodeExists(path);
		}
		if (context == null) {
			return false;
		}
		return context.hasNode(path);
	}

	/**
	 * Resolves the node at a given path.
	 *
	 * @param context The node from which the path should be resolved
	 * @param path    The absolute or relative path to resolve
	 * @return The node at the specified path or null if no node exists
	 * @throws RepositoryException If an error occurs in JCR
	 */
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

	/**
	 * Resolves the node at a given absolute path.
	 *
	 * @param path The absolute path to resolve
	 * @return The node at the specified path or null if no node exists
	 * @throws RepositoryException If an error occurs in JCR
	 */
	public Node resolve(String path) throws RepositoryException {
		return resolve(null, path);
	}

	/**
	 * Creates a binary representation of the given string.
	 *
	 * @param data     The string to convert
	 * @param encoding The target encoding
	 * @return An instance of Binary representing the given string
	 * @throws RepositoryException If an error occurs in JCR
	 * @throws IOException         If the encoding is invalid
	 */
	public Binary toBinary(String data, String encoding) throws RepositoryException {
		return session.getValueFactory().createBinary(
			new ByteArrayInputStream(data.getBytes(Charset.forName(encoding)))
		);
	}

	/**
	 * Create list of parents nodes.
	 *
	 * @param context start node
	 * @return list of parent nodes
	 * @throws RepositoryException if there is no depth or parent
	 */
	public List<Node> parents(Node context) throws RepositoryException {
		final List<Node> results = new ArrayList<>(context.getDepth());
		Node current = context;
		while (current.getDepth() > 0) {
			current = current.getParent();
			results.add(current);
		}
		return results;
	}

	/**
	 * Converts the given object to a single-valued JCR value.
	 *
	 * @param value The value to convert
	 * @return The converted value in the correct type or its string representation
	 * @throws RepositoryException if an error occurred
	 */
	public Value valueFromObject(Object value) throws RepositoryException {

		if (value instanceof Value) {
			return (Value) value;
		}
		final ValueFactory fac = session.getValueFactory();
		return convertToValue(fac, value);
	}

	@SuppressWarnings({"PMD", "checkstyle:ReturnCount", "checkstyle:CyclomaticComplexity"})
	private Value convertToValue(ValueFactory fac, Object value) throws RepositoryException {
		if (value instanceof String) {
			return fac.createValue((String) value);
		}
		else if (value instanceof Integer) {
			return fac.createValue((Integer) value);
		}
		else if (value instanceof Float) {
			return fac.createValue((Float) value);
		}
		else if (value instanceof Byte) {
			return fac.createValue((Byte) value);
		}
		else if (value instanceof BigDecimal) {
			return fac.createValue((BigDecimal) value);
		}
		else if (value instanceof Node) {
			return fac.createValue((Node) value);
		}
		else if (value instanceof Calendar) {
			return fac.createValue((Calendar) value);
		}
		else if (value instanceof Boolean) {
			return fac.createValue((Boolean) value);
		}
		else if (value instanceof Double) {
			return fac.createValue((Double) value);
		}
		else if (value instanceof Long) {
			return fac.createValue((Long) value);
		}
		else if (value instanceof Binary) {
			return fac.createValue((Binary) value);
		}
		else if (value instanceof InputStream) {
			return fac.createValue(fac.createBinary((InputStream) value));
		}
		else {
			return fac.createValue(value.toString());
		}
	}


	/**
	 * Convert the given array of objects or primitives to an array or JCR Values.
	 *
	 * @param value The array of values to convert
	 * @return The array of JCR values
	 * @throws RepositoryException if an error occurred
	 */
	public Value[] valuesFromArray(Object value) throws RepositoryException {
		if (!value.getClass().isArray()) {
			return new Value[]{valueFromObject(value)};
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

	/**
	 * Creates a value array from a list of objects.
	 *
	 * @param list the list of values to convert. Values have to coerce to the same property type for them to be usable in a property
	 * @return the values array
	 * @throws RepositoryException if an error occurred
	 */
	public Value[] valuesFromList(List<?> list) throws RepositoryException {
		final Value[] result = new Value[list.size()];
		for (int i = 0; i < list.size(); i++) {
			result[i] = valueFromObject(list.get(i));
		}
		return result;
	}

	/**
	 * Convert the given array of strings to an array or JCR Values.
	 *
	 * @param elements The array of values to convert
	 * @return The array of JCR values
	 * @throws RepositoryException if an error occurred
	 */
	public Value[] toStringArray(String[]... elements) throws RepositoryException {
		return valuesFromArray(elements);
	}

	/**
	 * Convert the given array of ints to an array or JCR Values.
	 *
	 * @param elements The array of values to convert
	 * @return The array of JCR values
	 * @throws RepositoryException if an error occurred
	 */
	public Value[] toLongArray(Long[]... elements) throws RepositoryException {
		return valuesFromArray(elements);
	}

	/**
	 * Turns the given object into either a single Value instance or an array of Value[] instances.
	 * <p>
	 * Is capable of handling all value types supported by valuesFromList, valuesFromArray and valueFromObject
	 *
	 * @param value the object to convert to either Value or Value[]
	 * @return an object, guaranteed to be either Value or Value[]
	 * @throws RepositoryException if an error occurred
	 */
	public Object asValueType(Object value) throws RepositoryException {
		final Object result;

		if (value instanceof Value) {
			result = value;
		}
		else if (value instanceof Value[]) {
			result = value;
		}
		else if (value instanceof Property && ((Property) value).isMultiple()) {
			result = ((Property) value).getValues();
		}
		else if (value instanceof Property) {
			result = ((Property) value).getValue();
		}
		else if (value instanceof List) {
			result = valuesFromList((List<?>) value);
		}
		else if (value.getClass().isArray()) {
			result = valuesFromArray(value);
		}
		else {
			result = valueFromObject(value);
		}

		return result;
	}


	/**
	 * Count the number of items in the given thing.
	 *
	 * @param any the object to count
	 * @return the size of any if it’s either a list, an array or a multivalued property, -1 otherwise
	 * @throws RepositoryException if an error occurred
	 */
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
