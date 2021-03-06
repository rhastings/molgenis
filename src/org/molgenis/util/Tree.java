package org.molgenis.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * A tree with uniquely named nodes. Each node in the tree can have a value.
 * Name and parent should be set in constructor.
 */
public interface Tree<T> extends Serializable
{
	/**
	 * Retrieve the unique name of this tree element.
	 */
	public String getName();

	/**
	 * Retrieve a subtree by name.
	 * 
	 * @param name
	 *            of the subtree
	 * @return a subtree.
	 */
	public T get(String name);

	/**
	 * Retrieve the parent node of current element.
	 * 
	 * @return parent
	 */
	public T getParent();

	/**
	 * Set the parent of current element (and move the children accordingly).
	 * 
	 * @param parent
	 *            to replace current parent of this.
	 */
	public void setParent(T parent);

	/**
	 * Retrieve wether this Tree has a parent
	 * 
	 * @return true if hasParent, else false.
	 */
	public boolean hasParent();

	/**
	 * Get the root element of this tree.
	 * 
	 * @return root Tree containing this.
	 */
	public T getRoot();

	/**
	 * Get the children of this element.
	 * 
	 * @return child subtrees
	 */
	public Vector<T> getChildren();

	/**
	 * Retrieve wether this Tree has children
	 * 
	 * @return true if hasChildren, else false.
	 */
	public boolean hasChildren();

	/**
	 * Get the children, and their children, etc. of this element.
	 */
	public List<T> getAllChildren();

	/**
	 * Get all children, optional including self
	 */
	public List<T> getAllChildren(boolean includeSelf);

	/**
	 * Get a subtree by name.
	 * 
	 * @param name
	 *            of the subtree
	 * @return subtree
	 */
	public T getChild(String name);

	/**
	 * Print the tree.
	 * 
	 * @return a string describing the tree.
	 */
	public String toString();

	/**
	 * Pretty print the (sub)tree, but only a certain levels
	 * 
	 * @param includeSubTree
	 *            if true the subtree is also printed
	 * @param level
	 *            will print in so many levels
	 */
	public String toString(boolean includeSubTree, int level);

	/**
	 * Retrieve all tree elements as map.
	 * 
	 * @return a map of subtrees
	 */
	public Map<String, T> getTreeElements();

	/**
	 * Retrieve the value of this node/leaf
	 * 
	 * @return the value.
	 */
	public Object getValue();

	/**
	 * Set the value of this node/leaf
	 */
	public void setValue(Object o);

	/**
	 * Translate the path of this subtree as a separated string.
	 * 
	 * @param separator
	 * @return string with the pat. FIXME: make this return a collection.
	 */
	public String getPath(String separator);

}
