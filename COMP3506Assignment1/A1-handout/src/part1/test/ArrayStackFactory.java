package part1.test;

import adt.IStack;
import adt.IStackFactory;
import part1.ArrayStack;

/**
 * An implementation of IStackFactory that creates and returns ArrayStacks.
 */
public class ArrayStackFactory implements IStackFactory {

	@Override
	public <E> IStack<E> createStack() {
		return new ArrayStack<E>();
	}

}
