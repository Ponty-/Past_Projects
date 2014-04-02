package part1;

import adt.IStack;
import adt.FullStackException;
import adt.EmptyStackException;


/*
 * PART 1 (QUESTION 1): Using the JUnit tests in part1.test.ArrayStackTest
 * discover and correct the 2 ERRORS in the following ArrayStack implementation
 * of the IStack interface. When you find an error, insert a brief one-line "//"
 * comment at that location, indicating where the error had been found, and why
 * it occurred.
 * 
 * You may not modify the code other than to fix the 2 ERRORS and insert the
 * required comments.
 */

/**
 * Implementation of the stack ADT using a fixed-length array. An exception is
 * thrown if a push operation is attempted when the size of the stack is equal
 * to the length of the array.
 * 
 * - taken and adapted from Goodrich and Tamassia (4th Ed)
 * 
 * @param <E>, the type of the objects to be stored on the stack.
 */
public class ArrayStack<E> implements IStack<E> {

	// Length of the array used to implement the stack.
	protected int capacity;

	// Default array capacity.
	public static final int CAPACITY = 1000;

	// Array used to implement the stack.
	protected E stackArray[];

	// Index of the top element of the stack in the array.
	protected int top = -1;

	/**
	 * Initializes the stack to use an array of default length.
	 */
	public ArrayStack() {
		this(CAPACITY);
	}

	/**
	 * Initializes the stack to hold a certain number of elements.
	 * 
	 * @param cap
	 *            capacity of the stack.
	 */
	@SuppressWarnings("unchecked")
	public ArrayStack(int cap) {
		capacity = cap;
		stackArray = (E[]) new Object[capacity];
	}

	@Override
	public int size() {
		return (top + 1);
	}

	@Override
	public boolean isEmpty() {
		return (top < 0);
	}

	/**
	 * Places the passed element on the top of the stack.
	 * 
	 * @param element
	 *            element to be inserted.
	 * @exception FullStackException
	 *                if the array storing the elements is full.
	 */
	@Override
	public void push(E element) throws FullStackException {
		if (size() == capacity)
			throw new FullStackException();
		stackArray[top+=1] = element;//Top wasn't being incremented when pushing elements
	}

	@Override
	public E top() throws EmptyStackException {
		if (isEmpty())
			throw new EmptyStackException();
		return stackArray[top];
	}

	@Override
	public E pop() throws EmptyStackException {
		if (isEmpty())
			throw new EmptyStackException();
		E element = stackArray[top]; //Setting element and decrementing top in the wrong order
		stackArray[top--] = null;
		return element;
	}
}
