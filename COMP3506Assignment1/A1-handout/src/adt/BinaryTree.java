package adt;

/**
 * An interface for a binary tree, where each node can have zero, one, or two
 * children. A binary tree tree may contain zero or more nodes.
 * 
 * * - taken and adapted from Goodrich and Tamassia (5th Ed)
 * 
 * @param <E>, the type of the objects to be stored in the tree.
 * 
 */
public interface BinaryTree<E> {

	// access methods

	/**
	 * Returns the number of nodes in the tree.
	 * 
	 * @return the number of nodes in the tree
	 */
	public int size();

	/**
	 * Returns whether or not the tree is empty.
	 * 
	 * @return true if the tree is empty and false otherwise
	 */
	public boolean isEmpty();

	/**
	 * Returns whether or not node v is the root of the tree.
	 * 
	 * @return true iff v is the root
	 * @throws InvalidPositionException
	 *             if v is not a valid position of the tree
	 */
	public boolean isRoot(Position<E> v) throws InvalidPositionException;

	/**
	 * Returns whether the node represented by position v is internal.
	 * 
	 * @return true iff v is internal
	 * @throws InvalidPositionException
	 *             if v is not a valid position of the tree
	 */
	public boolean isInternal(Position<E> v) throws InvalidPositionException;

	/**
	 * Returns whether or not node v is an external node
	 * 
	 * @return true iff v is external
	 * @throws InvalidPositionException
	 *             if v is not a valid position of the tree
	 */
	public boolean isExternal(Position<E> v) throws InvalidPositionException;

	/**
	 * Returns the root of the tree.
	 * 
	 * @return the root of the tree
	 * @throws EmptyTreeException
	 *             if the tree is empty
	 */
	public Position<E> root() throws EmptyTreeException;

	/**
	 * Returns the parent of node v.
	 * 
	 * @returns the parent of node v
	 * @throws InvalidPositionException
	 *             if v is not a valid position of the tree
	 * @throws BoundaryViolationException
	 *             if v has no parent.
	 */
	public Position<E> parent(Position<E> v) throws InvalidPositionException,
			BoundaryViolationException;

	/**
	 * Returns the left child of node v.
	 * 
	 * @return the left child of v
	 * @throws InvalidPositionException
	 *             if v is not a valid position of the tree
	 * @throws BoundaryViolationException
	 *             if v has no left child
	 * 
	 * */
	public Position<E> left(Position<E> v) throws InvalidPositionException,
			BoundaryViolationException;

	/**
	 * Returns the right child of node v.
	 * 
	 * @return the right child of v
	 * 
	 * @throws InvalidPositionException
	 *             if v is not a valid position of the tree
	 * @throws BoundaryViolationException
	 *             if v has no right child.
	 */
	public Position<E> right(Position<E> v) throws InvalidPositionException,
			BoundaryViolationException;

	/**
	 * Returns whether or not node v has a left child.
	 * 
	 * @return true iff v has a left child
	 * 
	 * @throws InvalidPositionException
	 *             if v is not a valid position of the tree
	 * */
	public boolean hasLeft(Position<E> v) throws InvalidPositionException;

	/**
	 * Returns whether or not node v has a right child.
	 * 
	 * @return true iff v has a right child
	 * @throws InvalidPositionException
	 *             if v is not a valid position of the tree
	 * */
	public boolean hasRight(Position<E> v) throws InvalidPositionException;

	// mutator methods

	/**
	 * Adds a root node with element e to an empty tree.
	 * 
	 * @return the position representing the new root node
	 * @throws NonEmptyTreeException
	 *             if the tree is not empty
	 */
	public Position<E> addRoot(E e) throws NonEmptyTreeException;

	/**
	 * Inserts a left child with element e at node v.
	 * 
	 * @throws InvalidPositionException
	 *             if v is not a valid position of the tree or it already has a
	 *             left child
	 * 
	 * @return the position of the newly constructed left child.
	 * 
	 * */
	public Position<E> insertLeft(Position<E> v, E e)
			throws InvalidPositionException;

	/**
	 * Inserts a right child with element e at a node v.
	 * 
	 * @throws InvalidPositionException
	 *             if v is not a valid position of the tree or it already has a
	 *             right child
	 * 
	 * @return the position of the newly constructed right child.
	 * 
	 * */
	public Position<E> insertRight(Position<E> v, E e)
			throws InvalidPositionException;

	/**
	 * Sets the left and right subtree of external node v to be T1 and T2,
	 * respectively. Trees T1 and T2 are no longer valid trees after this
	 * operation, as they are subsumed by the tree to which they are added (this
	 * one). An exception is thrown if v is either invalid or internal.
	 * 
	 * @throws InvalidPositionException
	 *             if position v is either invalid or internal
	 * */
	public void attach(Position<E> v, BinaryTree<E> T1, BinaryTree<E> T2)
			throws InvalidPositionException;

	/**
	 * An exception is thrown if node v has both a left and a right child;
	 * Otherwise, if node v has no child, then it is removed from the tree. If
	 * it has a child, it is removed and replaced by its only child.
	 * 
	 * @throws InvalidPositionException
	 *             if position v is either invalid or has two children
	 * */
	public E remove(Position<E> v) throws InvalidPositionException;

	/**
	 * Replaces the element at node v with o; returning the pre-existing
	 * element.
	 * 
	 * @throws InvalidPositionException
	 *             if v is not a valid position of the tree
	 * @return the old element at position v
	 */
	public E replace(Position<E> v, E o) throws InvalidPositionException;

}