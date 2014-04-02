package adt;

/**
 * An interface with a method that creates and returns an IStack.
 */
public interface IStackFactory {

	/** Create and return an IStack*/
	public <E> IStack<E> createStack();
	
	
}
