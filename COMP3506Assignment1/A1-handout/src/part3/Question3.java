package part3;

import adt.*;

public class Question3 {

	/**
	 * This method takes a decision tree annotated with rewards for reaching the
	 * different outcomes; and an array containing a person's answers to all of
	 * the questions that appear anywhere in the decision tree.
	 * 
	 * It returns the person's reward determined by the decision tree using the
	 * given answers.
	 * 
	 * Neither of the parameters (tree or answers) are to be modified by this
	 * method.
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
	 *            and m-1 (inclusive).
	 * 
	 * @param answers
	 *            A non-null boolean-valued array that contains the answers of a
	 *            person to each of the questions in the decision tree so that
	 *            the answer to question i appears at index i in the array. The
	 *            length of answers is equal to the number of different
	 *            questions in the decision tree. The boolean value true
	 *            corresponds to the answer "yes", and boolean value false
	 *            corresponds to the answer "no".
	 * 
	 * @return An integer representing the person's reward determined by the
	 *         decision tree using the given answers to the questions.
	 */
	public static int reward(BinaryTree<Integer> tree, boolean[] answers) {
		Position<Integer> current = tree.root();
		//continue looping until a reward is reached
		while(true) {
			//if it is a reward, return it
			if (tree.isExternal(current)){
				return current.element();
			}
			//otherwise go left or right based on answer given
			if (answers[current.element()]){
				current = tree.left(current);
			} else {
				current = tree.right(current);
			}
		}
	}

}
