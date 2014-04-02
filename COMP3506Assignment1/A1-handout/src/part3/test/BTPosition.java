package part3.test;

import adt.Position;

/**
 * Interface for a position in a binary tree.
 * 
 * - taken and adapted from Goodrich and Tamassia (5th Ed)
 * 
 * @param <E>, the type of the element stored in a BTPosition.
 */

public interface BTPosition<E> extends Position<E> {

	/** Returns the element stored at this position */
	public E element();

	/** Sets the element stored at this position to be e */
	public void setElement(E e);

	/** Returns the parent of this position if there is one, or null otherwise. */
	public BTPosition<E> getParent();

	/**
	 * Returns the left child of this position if there is one, or null
	 * otherwise.
	 */
	public BTPosition<E> getLeft();

	/**
	 * Returns the right child of this position if there is one, or null
	 * otherwise.
	 */
	public BTPosition<E> getRight();

	/** Sets the parent of this position to be v. */
	public void setParent(BTPosition<E> v);

	/** Sets the left child of this position to be v. */
	public void setLeft(BTPosition<E> v);

	/** Sets the right child of this position to be v. */
	public void setRight(BTPosition<E> v);

}
