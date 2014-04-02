package part3.test;

/**
 * An linked implementation of a BTPosition.
 * 
 * - taken and adapted from Goodrich and Tamassia (5th Ed)
 * 
 * @param <E>, the type of the element stored in a BTNode.
 */

public class BTNode<E> implements BTPosition<E> {

	private E element; // element stored at this node
	private BTPosition<E> left, right, parent; // adjacent nodes

	/**
	 * Constructs a node with the given element, parent, and left and right
	 * children nodes
	 */
	public BTNode(E element, BTPosition<E> parent, BTPosition<E> left,
			BTPosition<E> right) {
		setElement(element);
		setParent(parent);
		setLeft(left);
		setRight(right);
	}

	@Override
	public E element() {
		return element;
	}

	@Override
	public BTPosition<E> getParent() {
		return parent;
	}

	@Override
	public BTPosition<E> getLeft() {
		return left;
	}

	@Override
	public BTPosition<E> getRight() {
		return right;
	}

	@Override
	public void setElement(E e) {
		element = e;
	}

	@Override
	public void setParent(BTPosition<E> v) {
		parent = v;
	}

	@Override
	public void setLeft(BTPosition<E> v) {
		left = v;
	}

	@Override
	public void setRight(BTPosition<E> v) {
		right = v;
	}

}
