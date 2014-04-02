package part1.test;

//import org.junit.Assert;
import org.junit.Test;

import adt.*;
import part1.ArrayStack;

/**
 * Tests for the ArrayStack implementation of an IStack.
 */
public class ArrayStackTest extends IStackTest {

	public ArrayStackTest() {
		factory = new ArrayStackFactory();
	}

	/**
	 * Tests that the size of the array stack is as expected, and that a
	 * FullStackException is thrown when the size is exceeded.
	 */
	@Test(expected = FullStackException.class)
	public void testStackLimit1() {
		IStack<String> stack = new ArrayStack<String>(3);

		stack.push("1");
		stack.push("2");
		stack.push("3");
		stack.push("4");
	}

}
