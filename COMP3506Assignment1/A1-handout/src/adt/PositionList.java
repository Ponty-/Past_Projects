package adt;

/**
 * Interface for a position list.
 * 
 * - taken and adapted from Goodrich and Tamassia (5th Ed)
 * 
 * @param <E>, the type of the objects to be stored in the position list.
 */

public interface PositionList<E> {

	/** Returns the number of elements in this list. */
	public int size();

	/** Returns whether or not the list is empty. */
	public boolean isEmpty();

	/**
	 * Returns the first position in the list.
	 * 
	 * @throws BoundaryViolationException
	 *             if the list is empty
	 */
	public Position<E> first() throws BoundaryViolationException;

	/**
	 * Returns the last position in the list.
	 * 
	 * @throws BoundaryViolationException
	 *             if the list is empty
	 */
	public Position<E> last() throws BoundaryViolationException;

	/**
	 * Returns true if p has a next position in the list, and false otherwise
	 * 
	 * @throws InvalidPositionException
	 *             if p is not a valid position of the list
	 */
	public boolean hasNext(Position<E> p) throws InvalidPositionException;

	/**
	 * Returns true if p has a previous position in the list, and false
	 * otherwise
	 * 
	 * @throws InvalidPositionException
	 *             if p is not a valid position of the list
	 */
	public boolean hasPrev(Position<E> p) throws InvalidPositionException;

	/**
	 * Returns the node after position p in the list.
	 * 
	 * @throws InvalidPositionException
	 *             if p is not a valid position of the list
	 * @throws BoundaryViolationException
	 *             if p is the last position in the list
	 */
	public Position<E> next(Position<E> p) throws InvalidPositionException,
			BoundaryViolationException;

	/**
	 * Returns the position before p in the list.
	 * 
	 * @throws InvalidPositionException
	 *             if p is not a valid position of the list
	 * @throws BoundaryViolationException
	 *             if p is the first position in the list
	 */
	public Position<E> prev(Position<E> p) throws InvalidPositionException,
			BoundaryViolationException;

	/** Inserts element e at the front of the list */
	public void addFirst(E e);

	/** Inserts element e at the back of the list */
	public void addLast(E e);

	/**
	 * Inserts element e after position p in the list.
	 * 
	 * @throws InvalidPositionException
	 *             if p is not a valid position of the list
	 */
	public void addAfter(Position<E> p, E e) throws InvalidPositionException;

	/**
	 * Inserts element e before position p in the list.
	 * 
	 * @throws InvalidPositionException
	 *             if p is not a valid position of the list
	 */
	public void addBefore(Position<E> p, E e) throws InvalidPositionException;

	/**
	 * Removes position p from the list, returning the element stored there.
	 * 
	 * @throws InvalidPositionException
	 *             if p is not a valid position of the list
	 */
	public E remove(Position<E> p) throws InvalidPositionException;

	/**
	 * Replaces the element stored at the position p with e, returning old
	 * element.
	 * 
	 * @throws InvalidPositionException
	 *             if p is not a valid position of the list
	 * 
	 * */
	public E set(Position<E> p, E e) throws InvalidPositionException;

}
