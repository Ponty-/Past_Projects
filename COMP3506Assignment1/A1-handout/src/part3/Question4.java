package part3;

import adt.*;

public class Question4 {

	/**
	 * This method takes a decision tree annotated with rewards for reaching the
	 * different outcomes and prints (to System.out) an indented representation
	 * of the tree, as described in the assignment handout.
	 * 
	 * The parameter tree is not modified by this method.
	 * 
	 * NOTE: Newline characters should not be used in an OS-specific way: use
	 * method println() to terminate lines by writing the line separator string;
	 * do not assume that a newline will have a particular OS-specific
	 * representation. Your code will be batch tested on a UNIX machine.
	 * 
	 * @param tree
	 *            A (non-null and non-empty) decision tree. The elements of
	 *            internal nodes in the tree are integers that represent
	 *            question numbers. Each internal node has exactly two children
	 *            (so that the tree is proper): the left child represents the
	 *            remainder of the decision tree that must be completed if the
	 *            answer to the question is yes (true); and the right node
	 *            represents the remainder of the decision tree that must be
	 *            completed if the answer to the question is no (false). All
	 *            questions have a "yes" or "no" answer.
	 * 
	 *            The external nodes of the tree contain integers that represent
	 *            the reward associated with reaching the conclusion that
	 *            follows from having answered the questions (starting from the
	 *            root node) in such a way as to reach that external node. Note
	 *            that in the special case that the decision tree has only one
	 *            node, the root, no questions have to be asked or answered to
	 *            receive the reward given in that external node.
	 * 
	 *            For convenience you may assume that if there are m different
	 *            questions in the internal nodes of the decision tree, then
	 *            those questions are each represented by an integer between 0
	 *            and m-1 (inclusive). (Questions may appear in any order in the
	 *            tree.)
	 */
	public static void printDecisionTree(BinaryTree<Integer> tree) {
		printDecisionTree(tree, tree.root(), 0);

	}

	private static void printDecisionTree(BinaryTree<Integer> tree,
			Position<Integer> current, int depth) {
		//If the current node is external, print it as a reward
		if (tree.isExternal(current)) {
			System.out.println("R" + current.element());
			return;
		} else {
			//Otherwise print it as a question
			System.out.println("Q" + current.element() + ":");
			String tabs = new String();

			for (int i = 0; i <= depth; i++) {
				tabs += "   ";
			}
			//Put appropriate tab on the next line based on depth
			//Then recursively call on the children
			System.out.print(tabs + "Y->");
			printDecisionTree(tree, tree.left(current), depth + 1);
			System.out.print(tabs + "N->");
			printDecisionTree(tree, tree.right(current), depth + 1);
		}

	}
}
