package part3.test;

import adt.BinaryTree;
import adt.BinaryTreeFactory;

/**
 * An implementation of BinaryTreeFactory that creates and returns
 * LinkedBinaryTrees.
 */
public class LinkedBinaryTreeFactory implements BinaryTreeFactory {

	@Override
	public <E> BinaryTree<E> createBinaryTree() {
		return new LinkedBinaryTree<E>();
	}

}
