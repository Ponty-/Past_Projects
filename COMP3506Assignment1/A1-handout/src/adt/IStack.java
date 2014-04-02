package adt;

/**
 * Interface for a stack: a collection of objects that are inserted and removed
 * according to the last-in first-out principle.
 * 
 * - taken and adapted from Goodrich and Tamassia (5th Ed)
 * 
 * @param <E>, the type of the objects to be stored on the stack.
 */

public interface IStack<E> {
	/**
	 * Places the passed element on the top of the stack.
	 * 
	 * @param element
	 *            element to be inserted.
	 */
	public void push(E element);

	/**
	 * Removes and returns the top element of the stack.
	 * 
	 * @return the removed element.
	 * @throws EmptyStackException
	 *             if the stack is empty.
	 */
	public E pop() throws EmptyStackException;

	/**
	 * Inspects the element at the top of the stack.
	 * 
	 * @return the top element of the stack.
	 * @throws EmptyStackException
	 *             if the stack is empty.
	 */
	public E top() throws EmptyStackException;

	/**
	 * Returns the number of elements in the stack
	 * 
	 * @return number of elements in the stack.
	 */
	public int size();

	/**
	 * Returns whether the stack is empty
	 * 
	 * @return true if the stack is empty; false otherwise.
	 */
	public boolean isEmpty();
}
