package part3;

import adt.*;

public class Question5 {

	/**
	 * This method takes a decision tree with rewards for reaching each of the
	 * different possible outcomes; an array containing a person's real answers
	 * to all of the questions that appear (anywhere) in the decision tree; and
	 * another array containing the cost incurred by the person for lying about
	 * about their real answer to any given question.
	 * 
	 * It returns an array containing a revised array of answers that will
	 * maximize their profit as determined by both the decision tree, their real
	 * answers and the costs of lying.
	 * 
	 * The person's profit is defined to be the person's reward determined by
	 * the decision tree using the revised answers, minus the cost of each lie
	 * told in their revised answers.
	 * 
	 * There may be more than one revised set of answers that maximizes the
	 * person's profit. This method may return any one of these.
	 * 
	 * None of the parameters (tree, answers or cost) are to be modified by this
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
	 *            You may assume that the same question does not appear more
	 *            than once on any one path from the root of the tree to one of
	 *            its external nodes. (Note that despite this restriction the
	 *            same question may appear more than once in the tree as a
	 *            whole.)
	 * 
	 *            For convenience, you may assume that if there are m different
	 *            questions in the internal nodes of the decision tree, then
	 *            those questions are each represented by an integer between 0
	 *            and m-1 (inclusive).
	 * 
	 * @param answers
	 *            A non-null, boolean-valued array that contains the real
	 *            answers of a person to each of the questions in the decision
	 *            tree so that the real answer to question i appears at index i
	 *            in the array. The length of answers is equal to the number of
	 *            different questions in the decision tree. The boolean value
	 *            true corresponds to the answer "yes", and boolean value false
	 *            corresponds to the answer "no".
	 * 
	 * @param cost
	 *            A non-null array that contains the cost of lying about each of
	 *            the questions in the decision tree so that the cost of lying
	 *            about question i appears at the index i in the array. (The
	 *            length of cost is equal to the number of different questions
	 *            in the decision tree.)
	 * 
	 * @return A non-null array that contains revised answers that could be
	 *         given by the person to each of the questions in the decision tree
	 *         so as to maximize their profit as determined by both the decision
	 *         tree, their real answers and the costs of lying. The revised
	 *         answer that should be given to question i should appear at index
	 *         i, and the length of the array should be equal to the number of
	 *         different questions in the decision tree. The boolean value true
	 *         corresponds to the answer "yes", and boolean value false
	 *         corresponds to the answer "no".
	 */
	public static Boolean[] revisedAnswers(BinaryTree<Integer> tree,
			Boolean[] answers, Integer[] cost) {
		// Create a new RewardChecker to manage the reward and the path to it
		RewardChecker checker = new RewardChecker();
		// Call the recursive method to determine the best answers
		revisedAnswers(tree, answers, cost, tree.root(), 0, checker);
		// return the best revised answers
		return checker.getBestAnswers();
	}

	/**
	 * Recursive function that takes all the required variables to determine the
	 * answers to get to the highest reward.
	 * 
	 * @param tree
	 *            The binary tree to be worked on
	 * @param answers
	 *            The answers to be used for the questions
	 * @param cost
	 *            The costs of lying for each question
	 * @param current
	 *            The current node in the tree
	 * @param penalty
	 *            The total penalty from lying so far
	 * @param checker
	 *            The RewardChecker instance being used for comparing and
	 *            storing rewards
	 */
	private static void revisedAnswers(BinaryTree<Integer> tree,
			Boolean[] answers, Integer[] cost, Position<Integer> current,
			int penalty, RewardChecker checker) {
		if (tree.isExternal(current)) {
			// If the node is external send the answers and total reward
			// (including penalties) to the reward checker
			checker.checkReward(current.element() - penalty, answers);
		} else {
			// Otherwise get the question number
			int qNum = current.element();
			// Copy the answers array for modification
			Boolean[] newAnswers = answers.clone();

			if (newAnswers[qNum]) {
				// If the non-lying answer is true
				// Recurse on the left node
				revisedAnswers(tree, newAnswers, cost, tree.left(current),
						penalty, checker);
				// Change the answer for the current question in the copy
				newAnswers[qNum] = !newAnswers[qNum];
				// recurse on the right node
				revisedAnswers(tree, newAnswers, cost, tree.right(current),
						penalty + cost[qNum], checker);
			} else {
				// Otherwise, recurse on the right node
				revisedAnswers(tree, newAnswers, cost, tree.right(current),
						penalty, checker);
				// Change the answer for the current question in the copy
				newAnswers[qNum] = !newAnswers[qNum];
				// Recurse on the left node
				revisedAnswers(tree, newAnswers, cost, tree.left(current),
						penalty + cost[qNum], checker);
			}

		}
	}
}

/**
 * Internal class for storing and managing the best reward and the answers
 * required to get it.
 * 
 * @author Michael
 * 
 */
class RewardChecker {
	private int highest;
	private Boolean[] bestAnswers;

	public RewardChecker() {
		this.highest = 0;
	}

	/**
	 * Checks if the given reward is better than the current highest, and if so,
	 * stores it.
	 * 
	 * @param reward
	 *            The given reward.
	 * @param answers
	 *            The given answers to get to that reward.
	 */
	public void checkReward(int reward, Boolean[] answers) {
		if (reward > highest) {
			//Set set the new highest value, save a copy of the revised answers
			highest = reward;
			bestAnswers = answers.clone();
		}
	}

	/**
	 * Returns the array of best answers.
	 */
	public Boolean[] getBestAnswers() {
		return bestAnswers;
	}

	/**
	 * Returns the current highest reward.
	 */
	public int getHighest() {
		return highest;
	}
}
