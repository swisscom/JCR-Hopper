package com.swisscom.aem.tools.jcrhopper.context;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

public interface JcrFunctions {
	/**
	 * Checks if a property exists.
	 *
	 * @param node     The node whose property you want to check
	 * @param propName The name of the property to read
	 * @return true if the referenced property exists
	 * @throws RepositoryException If an error occurs in JCR
	 */
	boolean hasProp(Node node, String propName) throws RepositoryException;

	/**
	 * Gets a property from a node or null if the property does not exist.
	 * Useful if you want to coerce the property to a different type than its native one.
	 *
	 * @param node     The node whose property you want to check
	 * @param propName The name of the property to read
	 * @return The value object of the property referenced by propName on node or null
	 * @throws RepositoryException If an error occurs in JCR
	 */
	Value prop(Node node, String propName) throws RepositoryException;

	/**
	 * Gets a multivalued property from a node.
	 * Useful if you want to coerce the property to a different type than its native one.
	 * Absent properties will return an empty array. Check with {@link JcrFunctions#hasProp(Node, String)}.
	 *
	 * @param node     The node whose property you want to check
	 * @param propName The name of the property to read
	 * @return The value object of the property referenced by propName on node or null
	 * @throws RepositoryException If an error occurs in JCR
	 */
	Value[] props(Node node, String propName) throws RepositoryException;

	/**
	 * Returns the value of a property or null if the property does not exist.
	 *
	 * @param node     The node whose property you want to check
	 * @param propName The name of the property to read
	 * @return The value of the property referenced by propName on node converted to its type or null
	 * @throws RepositoryException If an error occurs in JCR
	 */
	Object val(Node node, String propName) throws RepositoryException;

	/**
	 * Returns an array for the values of the given property (empty if the property does not exist).
	 * To check if the property exists, use hasProp.
	 *
	 * @param node     The node whose property you want to check
	 * @param propName The name of the property to read
	 * @return an array of the property values (all have the same type)
	 * @throws RepositoryException If an error occurs in JCR
	 */
	Object[] vals(Node node, String propName) throws RepositoryException;

	/**
	 * Checks if a node matches a given type either as primary or mixin.
	 *
	 * @param node     The node whose type you want to check
	 * @param nodeType The type to check
	 * @return true if the node matches the specified type
	 * @throws RepositoryException If an error occurs in JCR
	 */
	boolean isType(Node node, String nodeType) throws RepositoryException;

	/**
	 * Checks if a node exists at a given absolute path.
	 *
	 * @param path The absolute path to resolve
	 * @return true if a node exists at the path
	 * @throws RepositoryException If an error occurs in JCR
	 */
	boolean exists(String path) throws RepositoryException;

	/**
	 * Checks if a node exists at a given path.
	 *
	 * @param context The node from which the path should be resolved
	 * @param path    The absolute or relative path to resolve
	 * @return true if a node exists at the path
	 * @throws RepositoryException If an error occurs in JCR
	 */
	boolean exists(@Nullable Node context, String path) throws RepositoryException;

	/**
	 * Resolves the node at a given path.
	 *
	 * @param context The node from which the path should be resolved
	 * @param path    The absolute or relative path to resolve
	 * @return The node at the specified path or null if no node exists
	 * @throws RepositoryException If an error occurs in JCR
	 */
	Node resolve(@Nullable Node context, String path) throws RepositoryException;

	/**
	 * Resolves the node at a given absolute path.
	 *
	 * @param path The absolute path to resolve
	 * @return The node at the specified path or null if no node exists
	 * @throws RepositoryException If an error occurs in JCR
	 */
	Node resolve(String path) throws RepositoryException;

	/**
	 * Creates a binary representation of the given string.
	 *
	 * @param data     The string to convert
	 * @param encoding The target encoding
	 * @return An instance of Binary representing the given string
	 * @throws RepositoryException If an error occurs in JCR
	 * @throws IOException         If the encoding is invalid
	 */
	Binary toBinary(String data, String encoding) throws RepositoryException;

	/**
	 * Create list of parents nodes.
	 *
	 * @param context start node
	 * @return list of parent nodes
	 * @throws RepositoryException if there is no depth or parent
	 */
	List<Node> parents(Node context) throws RepositoryException;

	/**
	 * Converts the given object to a single-valued JCR value.
	 *
	 * @param value The value to convert
	 * @return The converted value in the correct type or its string representation
	 * @throws RepositoryException if an error occurred
	 */
	Value valueFromObject(Object value) throws RepositoryException;

	/**
	 * Convert the given array of objects or primitives to an array or JCR Values.
	 *
	 * @param value The array of values to convert
	 * @return The array of JCR values
	 * @throws RepositoryException if an error occurred
	 */
	Value[] valuesFromArray(Object value) throws RepositoryException;

	/**
	 * Creates a value array from a list of objects.
	 *
	 * @param list the list of values to convert. Values have to coerce to the same property type for them to be usable in a property
	 * @return the values array
	 * @throws RepositoryException if an error occurred
	 */
	Value[] valuesFromList(List<?> list) throws RepositoryException;

	/**
	 * Convert the given array of strings to an array or JCR Values.
	 *
	 * @param elements The array of values to convert
	 * @return The array of JCR values
	 * @throws RepositoryException if an error occurred
	 */
	Value[] toStringArray(String[]... elements) throws RepositoryException;

	/**
	 * Convert the given array of ints to an array or JCR Values.
	 *
	 * @param elements The array of values to convert
	 * @return The array of JCR values
	 * @throws RepositoryException if an error occurred
	 */
	Value[] toLongArray(Long[]... elements) throws RepositoryException;

	/**
	 * Turns the given object into either a single Value instance or an array of Value[] instances.
	 * <p>
	 * Is capable of handling all value types supported by valuesFromList, valuesFromArray and valueFromObject
	 *
	 * @param value the object to convert to either Value or Value[]
	 * @return an object, guaranteed to be either Value or Value[]
	 * @throws RepositoryException if an error occurred
	 */
	Object asValueType(Object value) throws RepositoryException;

	/**
	 * Count the number of items in the given thing.
	 *
	 * @param any the object to count
	 * @return the size of any if it’s either a list, an array or a multivalued property, -1 otherwise
	 * @throws RepositoryException if an error occurred
	 */
	int size(Object any) throws RepositoryException;
}
