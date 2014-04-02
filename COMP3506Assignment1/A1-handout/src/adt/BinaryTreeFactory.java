package adt;

/**
 * An interface with a method that creates and returns a BinaryTree.
 */
public interface BinaryTreeFactory {

	/** Create and return a BinaryTree*/
	public <E> BinaryTree<E> createBinaryTree();
	
	
}
