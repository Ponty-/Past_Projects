package adt;

/**
 * Interface for a position.
 * 
 * - taken and adapted from Goodrich and Tamassia (5th Ed)
 * 
 * @param <E>, the type of the element stored in a Position.
 */

public interface Position<E> {
	/** Return the element stored at this position. */
	public E element();
}
