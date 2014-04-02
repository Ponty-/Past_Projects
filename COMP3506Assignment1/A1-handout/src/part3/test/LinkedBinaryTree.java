package part3.test;

import adt.BinaryTree;
import adt.BoundaryViolationException;
import adt.EmptyTreeException;
import adt.InvalidPositionException;
import adt.NonEmptyTreeException;
import adt.Position;

/**
 * An implementation of the BinaryTree interface by means of a linked structure.
 * 
 * - taken and adapted from Goodrich and Tamassia (5th Ed)
 * 
 * @param <E>, the type of the objects to be stored in the tree.
 */
public class LinkedBinaryTree<E> implements BinaryTree<E> {

	protected BTPosition<E> root; // reference to the root
	protected int size; // number of nodes in the tree

	/** Creates an empty binary tree. */
	public LinkedBinaryTree() {
		root = null; // start with an empty tree
		size = 0;
	}

	// access methods

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return (size == 0);
	}

	@Override
	public boolean isRoot(Position<E> v) throws InvalidPositionException {
		checkPosition(v); // auxiliary method
		return (v == root());
	}

	@Override
	public boolean isInternal(Position<E> v) throws InvalidPositionException {
		checkPosition(v); // auxiliary method
		return (hasLeft(v) || hasRight(v));
	}

	@Override
	public boolean isExternal(Position<E> v) throws InvalidPositionException {
		return !isInternal(v);
	}

	@Override
	public Position<E> root() throws EmptyTreeException {
		if (root == null)
			throw new EmptyTreeException("The tree is empty");
		return root;
	}

	@Override
	public Position<E> parent(Position<E> v) throws InvalidPositionException,
			BoundaryViolationException {
		BTPosition<E> vv = checkPosition(v);
		BTPosition<E> parentPos = vv.getParent();
		if (parentPos == null)
			throw new BoundaryViolationException("No parent");
		return parentPos;
	}

	@Override
	public Position<E> left(Position<E> v) throws InvalidPositionException,
			BoundaryViolationException {
		BTPosition<E> vv = checkPosition(v);
		BTPosition<E> leftPos = vv.getLeft();
		if (leftPos == null)
			throw new BoundaryViolationException("No left child");
		return leftPos;
	}

	@Override
	public Position<E> right(Position<E> v) throws InvalidPositionException,
			BoundaryViolationException {
		BTPosition<E> vv = checkPosition(v);
		BTPosition<E> rightPos = vv.getRight();
		if (rightPos == null)
			throw new BoundaryViolationException("No right child");
		return rightPos;
	}

	@Override
	public boolean hasLeft(Position<E> v) throws InvalidPositionException {
		BTPosition<E> vv = checkPosition(v);
		return (vv.getLeft() != null);
	}

	@Override
	public boolean hasRight(Position<E> v) throws InvalidPositionException {
		BTPosition<E> vv = checkPosition(v);
		return (vv.getRight() != null);
	}

	// mutator methods

	@Override
	public Position<E> addRoot(E e) throws NonEmptyTreeException {
		if (!isEmpty())
			throw new NonEmptyTreeException("Tree already has a root");
		size = 1;
		root = createNode(e, null, null, null);
		return root;
	}

	@Override
	public Position<E> insertLeft(Position<E> v, E e)
			throws InvalidPositionException {
		BTPosition<E> vv = checkPosition(v);
		Position<E> leftPos = vv.getLeft();
		if (leftPos != null)
			throw new InvalidPositionException("Node already has a left child");
		BTPosition<E> ww = createNode(e, vv, null, null);
		vv.setLeft(ww);
		size++;
		return ww;
	}

	@Override
	public Position<E> insertRight(Position<E> v, E e)
			throws InvalidPositionException {
		BTPosition<E> vv = checkPosition(v);
		Position<E> rightPos = vv.getRight();
		if (rightPos != null)
			throw new InvalidPositionException("Node already has a right child");
		BTPosition<E> ww = createNode(e, vv, null, null);
		vv.setRight(ww);
		size++;
		return ww;
	}

	@Override
	public void attach(Position<E> v, BinaryTree<E> T1, BinaryTree<E> T2)
			throws InvalidPositionException {
		BTPosition<E> vv = checkPosition(v);
		if (isInternal(v))
			throw new InvalidPositionException(
					"Cannot attach from internal node");
		int newSize = size + T1.size() + T2.size();
		if (!T1.isEmpty()) {
			BTPosition<E> r1 = checkPosition(T1.root());
			vv.setLeft(r1);
			r1.setParent(vv); // T1 should be invalidated
		}
		if (!T2.isEmpty()) {
			BTPosition<E> r2 = checkPosition(T2.root());
			vv.setRight(r2);
			r2.setParent(vv); // T2 should be invalidated
		}
		size = newSize;
	}

	@Override
	public E remove(Position<E> v) throws InvalidPositionException {
		BTPosition<E> vv = checkPosition(v);
		BTPosition<E> leftPos = vv.getLeft();
		BTPosition<E> rightPos = vv.getRight();
		if (leftPos != null && rightPos != null)
			throw new InvalidPositionException(
					"Cannot remove node with two children");
		BTPosition<E> ww; // the only child of v, if any
		if (leftPos != null)
			ww = leftPos;
		else if (rightPos != null)
			ww = rightPos;
		else
			// v is a leaf
			ww = null;
		if (vv == root) { // v is the root
			if (ww != null)
				ww.setParent(null);
			root = ww;
		} else { // v is not the root
			BTPosition<E> uu = vv.getParent();
			if (vv == uu.getLeft())
				uu.setLeft(ww);
			else
				uu.setRight(ww);
			if (ww != null)
				ww.setParent(uu);
		}
		size--;
		return v.element();
	}

	@Override
	public E replace(Position<E> v, E o) throws InvalidPositionException {
		BTPosition<E> vv = checkPosition(v);
		E temp = v.element();
		vv.setElement(o);
		return temp;
	}

	// supporting methods

	/**
	 * If Position v is not null, cast it to a BTPosition if possible, otherwise
	 * throw an exception.
	 * 
	 * @return v cast to a binary tree position
	 * @throws InvalidPositionException
	 *             if either v is null or it is not an instance of BTPosition
	 */
	protected BTPosition<E> checkPosition(Position<E> v)
			throws InvalidPositionException {
		if (v == null || !(v instanceof BTPosition))
			throw new InvalidPositionException("The position is invalid");
		return (BTPosition<E>) v;
	}

	/**
	 * Creates a new binary tree node with the given element, parent, left and
	 * right child positions.
	 * 
	 * @return the newly created node
	 * */
	protected BTPosition<E> createNode(E element, BTPosition<E> parent,
			BTPosition<E> left, BTPosition<E> right) {
		return new BTNode<E>(element, parent, left, right);
	}

}
