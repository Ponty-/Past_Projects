package part1.test;

import org.junit.Assert;
import org.junit.Test;

import adt.*;

/**
 * Tests for any implementation of an IStack. 
 */

public abstract class IStackTest {

	protected IStackFactory factory;

	/**
	 * Test the constructor
	 */
	@Test
	public void testStack() {
		IStack<String> s = factory.<String>createStack();
	}	
	
	/**
	 * Test that the size of a newly created IStack is 0.
	 */
	@Test
	public void testGetSize1() {
		IStack<String> stack = factory.<String>createStack();
		Assert.assertEquals("Incorrect stack size", 0, stack.size());
	}

	/**
	 * Test that a newly created IStack is empty.
	 */
	public void testIsEmpty1() {
		IStack<String> stack = factory.<String>createStack();
		Assert.assertEquals("List incorrectly thinks it is not empty", true, stack
				.isEmpty());
	}

	/**
	 * Tests that attempting to access the top element of an empty stack will
	 * throw an EmptyStackException.
	 */
	@Test(expected=EmptyStackException.class)
	public void testTop1() {
		IStack<Object> stack = factory.<Object>createStack();
		stack.top();
	}

	/**
	 * Tests that attempting to pop the top element of an empty stack will throw
	 * an EmptyStackException.
	 */
	@Test(expected=EmptyStackException.class)
	public void testPop1() {
		IStack<Object> stack = factory.<Object>createStack();
		stack.pop();
	}

	/**
	 * Tests the push method to make sure it increments the size of the stack
	 * appropriately.
	 */
	@Test
	public void testPush1() {
		IStack<String> stack = factory.<String>createStack();

		Assert.assertEquals("Incorrect stack size", 0, stack.size());
		stack.push("1");
		Assert.assertEquals("Incorrect stack size", 1, stack.size());
		stack.push("2");
		Assert.assertEquals("Incorrect stack size", 2, stack.size());
	}

	/**
	 * Tests that we can access the last object pushed onto the stack.
	 */
	@Test
	public void testPush2() {
		IStack<String> stack = factory.<String>createStack();
		stack.push("other");
		stack.push("other");
		stack.push("expected");

		String str = stack.top();
		Assert.assertEquals("Incorrect object retrieved", "expected", str);
	}

	/**
	 * Tests that serial additions to the stack are correctly retrieved.
	 */
	@Test
	public void testTop2() {
		String str;
		IStack<String> stack = factory.<String>createStack();
		stack.push("other");
		str = stack.top();
		Assert.assertEquals("Incorrect object retrieved", "other", str);
		stack.push("expected");
		str = stack.top();
		Assert.assertEquals("Incorrect object retrieved", "expected", str);
	}

	/**
	 * Tests that the size of the stack reduces appropriately as elements are
	 * removed.
	 */
	@Test
	public void testPop2() {
		IStack<String> stack = factory.<String>createStack();

		Assert.assertEquals("Incorrect stack size", 0, stack.size());
		stack.push("1");
		Assert.assertEquals("Incorrect stack size", 1, stack.size());
		stack.push("2");
		Assert.assertEquals("Incorrect stack size", 2, stack.size());

		stack.pop();
		Assert.assertEquals("Incorrect stack size", 1, stack.size());

		stack.pop();
		Assert.assertEquals("Incorrect stack size", 0, stack.size());
	}

	/**
	 * Tests that elements added to the stack are removed properly.
	 */
	@Test
	public void testPop3() {
		IStack<String> stack = factory.<String>createStack();

		stack.push("1");
		stack.push("2");

		String two = stack.pop();
		Assert.assertEquals("Incorrect number retrieved", "2", two);

		String one = stack.pop();
		Assert.assertEquals("Incorrect number retrieved", "1", one);
	}

	/**
	 * Tests that a series of push/pop operations work.
	 */
	@Test
	public void testPushPop1() {
		IStack<String> stack = factory.<String>createStack();
		
		stack.push("1");
		stack.push("2");

		String two = stack.pop();
		Assert.assertEquals("Incorrect number retrieved", "2", two);
		Assert.assertEquals("Incorrect stack size", 1, stack.size());

		stack.push("2");
		Assert.assertEquals("Incorrect stack size", 2, stack.size());
		stack.push("3");
		Assert.assertEquals("Incorrect stack size", 3, stack.size());

		String three = stack.pop();
		Assert.assertEquals("Incorrect number retrieved", "3", three);
		Assert.assertEquals("Incorrect stack size", 2, stack.size());
	}


}
