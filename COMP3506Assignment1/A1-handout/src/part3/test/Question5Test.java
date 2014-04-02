package part3.test;

import org.junit.*;
import adt.*;
import part3.Question5;

/**
 * Basic tests for the revisedAnswers method from class Question5. A much more
 * extensive test suite will be performed for assessment of your code, but this
 * should get you started.
 */
public class Question5Test {

	// A factory that we will use to create instances of trees
	BinaryTreeFactory tFactory = new LinkedBinaryTreeFactory();

	@Test
	public void test3() {
		// create an example decision tree
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> n0 = tree.addRoot(0);
		Position<Integer> n1 = tree.insertLeft(n0, 1);//               [0]                           
		Position<Integer> n3 = tree.insertRight(n0, 3);//              /  \                            
		Position<Integer> n2 = tree.insertLeft(n1, 2);//              /    \                           
		Position<Integer> r2 = tree.insertRight(n1, 1);//           [1]    [3]                        
		Position<Integer> r0 = tree.insertLeft(n2, 2);//            / \    / \                         
		Position<Integer> r1 = tree.insertRight(n2, 9);//         [2]  1  2   8 <--- You want the 8    
		Position<Integer> r3 = tree.insertLeft(n3, 2);//          / \                                
		Position<Integer> r4 = tree.insertRight(n3, 8);//        2   9                               

		// test output for a tree where there is only one optimal solution
		Boolean[] answers = { true, false, true, true };
		Integer[] cost = { 1, 1, 6, 4 };
		Boolean[] revisedAnswers = Question5
				.revisedAnswers(tree, answers, cost);
		Boolean[] expectedAnswers = { false, false, true, false };
		Assert.assertArrayEquals(expectedAnswers, revisedAnswers);
	}

	@Test
	public void meanTree() {
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> n0 = tree.addRoot(0);
		Position<Integer> n1 = tree.insertLeft(n0, 2);//                  [0]                    
		Position<Integer> n2 = tree.insertLeft(n1, 4);//                 /   \    You want the 19
		Position<Integer> r3 = tree.insertLeft(n2, 21);//              [2]    \                 
		Position<Integer> r4 = tree.insertRight(n2, 13);//             / \    [3]               
		Position<Integer> r5 = tree.insertRight(n1, 17);//           [4]  17   /\                
		Position<Integer> n6 = tree.insertRight(n0, 3);//            / \     [5] 14                
		Position<Integer> n7 = tree.insertLeft(n6, 5);//           21  13    / \                   
		Position<Integer> r8 = tree.insertRight(n6, 14);//                  5  [1]                 
		Position<Integer> r9 = tree.insertLeft(n7, 5);//                       / \                 
		Position<Integer> n10 = tree.insertRight(n7, 1);//                    19  0                 
		Position<Integer> r11 = tree.insertLeft(n10, 19);//                                        
		Position<Integer> r12 = tree.insertRight(n10, 0);//                                   
		Boolean[] answers = { true, true, true, true, false, false };
		Integer[] cost = { 4, 2, 6, 1, 7, 4 };
		Boolean[] revisedAnswers = Question5
				.revisedAnswers(tree, answers, cost);
		Boolean[] expectedAnswers = { false, true, true, true, false, false };
		Assert.assertArrayEquals(expectedAnswers, revisedAnswers);
	}
	@Test
	public void indifferentTree() {
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> n0 = tree.addRoot(0);
		Position<Integer> n1 = tree.insertLeft(n0, 2);//                  [0]                    
		Position<Integer> n2 = tree.insertLeft(n1, 4);//                 /   \    You want the 21
		Position<Integer> r3 = tree.insertLeft(n2, 21);//              [2]    \                 
		Position<Integer> r4 = tree.insertRight(n2, 13);//             / \    [3]               
		Position<Integer> r5 = tree.insertRight(n1, 17);//           [4]  17   /\                
		Position<Integer> n6 = tree.insertRight(n0, 3);//            / \     [5] 14                
		Position<Integer> n7 = tree.insertLeft(n6, 5);//           21  13    / \                   
		Position<Integer> r8 = tree.insertRight(n6, 14);//                  5  [1]                 
		Position<Integer> r9 = tree.insertLeft(n7, 5);//                       / \                 
		Position<Integer> n10 = tree.insertRight(n7, 1);//                    19  0                 
		Position<Integer> r11 = tree.insertLeft(n10, 19);//                                        
		Position<Integer> r12 = tree.insertRight(n10, 0);//                                   
		Boolean[] answers = { true, true, true, true, false, false };
		Integer[] cost = { 6, 2, 6, 1, 7, 4 };
		Boolean[] revisedAnswers = Question5
				.revisedAnswers(tree, answers, cost);
		Boolean[] expectedAnswers = { true, true, true, true, true, false };
		Assert.assertArrayEquals(expectedAnswers, revisedAnswers);
	}
	@Test
	public void sameTree() {
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> n0 = tree.addRoot(0);
		Position<Integer> n1 = tree.insertLeft(n0, 2);//                  [0]                    
		Position<Integer> n2 = tree.insertLeft(n1, 4);//                 /   \    You want to give me money
		Position<Integer> r3 = tree.insertLeft(n2, 21);//              [2]    \                 
		Position<Integer> r4 = tree.insertRight(n2, 13);//             / \    [3]               
		Position<Integer> r5 = tree.insertRight(n1, 17);//           [4]  17   /\                
		Position<Integer> n6 = tree.insertRight(n0, 3);//            / \     [2] 14                
		Position<Integer> n7 = tree.insertLeft(n6, 2);//           21  13    / \                   
		Position<Integer> r8 = tree.insertRight(n6, 14);//                  5  [1]                 
		Position<Integer> r9 = tree.insertLeft(n7, 5);//                       / \                 
		Position<Integer> n10 = tree.insertRight(n7, 1);//                    19  0                 
		Position<Integer> r11 = tree.insertLeft(n10, 19);//                                        
		Position<Integer> r12 = tree.insertRight(n10, 0);//                                   
		Boolean[] answers = { true, true, true, true, false };
		Integer[] cost = { 1, 2, 6, 6, 9 };
		Boolean[] revisedAnswers = Question5
				.revisedAnswers(tree, answers, cost);
		Boolean[] expectedAnswers = { true, true, true, true, false };
		Assert.assertArrayEquals(expectedAnswers, revisedAnswers);
	}
	@Test
	public void dualTree() {
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> n0 = tree.addRoot(0);
		Position<Integer> n1 = tree.insertLeft(n0, 2);//                  [0]                    
		Position<Integer> n2 = tree.insertLeft(n1, 4);//                 /   \    You want the 99
		Position<Integer> r3 = tree.insertLeft(n2, 21);//              [2]    \                 
		Position<Integer> r4 = tree.insertRight(n2, 13);//             / \    [3]               
		Position<Integer> r5 = tree.insertRight(n1, 12);//           [4]  12   /\                
		Position<Integer> n6 = tree.insertRight(n0, 3);//            / \     [2] 14                
		Position<Integer> n7 = tree.insertLeft(n6, 2);//           21  13    / \                   
		Position<Integer> r8 = tree.insertRight(n6, 14);//                  5  [1]                 
		Position<Integer> r9 = tree.insertLeft(n7, 5);//                       / \                 
		Position<Integer> n10 = tree.insertRight(n7, 1);//                    19  99                 
		Position<Integer> r11 = tree.insertLeft(n10, 19);//                                        
		Position<Integer> r12 = tree.insertRight(n10, 99);//                                   
		Boolean[] answers = { true, true, true, false, false };
		Integer[] cost = { 3, 2, 6, 2, 9 };
		Boolean[] revisedAnswers = Question5
				.revisedAnswers(tree, answers, cost);
		Boolean[] expectedAnswers = { false, false, false, true, false };
		Assert.assertArrayEquals(expectedAnswers, revisedAnswers);
	}
	@Test
	public void tiredTree() {
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> n0 = tree.addRoot(0);
		Position<Integer> n1 = tree.insertLeft(n0, 1);//                  [0]                     
		Position<Integer> n2 = tree.insertLeft(n1, 4);//                 /   \    You want the 21
		Position<Integer> r3 = tree.insertLeft(n2, 21);//              [1]    \                  
		Position<Integer> r4 = tree.insertRight(n2, 13);//             / \    [5]                 
		Position<Integer> r5 = tree.insertRight(n1, 17);//           [4]  17   /\                
		Position<Integer> n6 = tree.insertRight(n0, 5);//            / \     [3] 14                
		Position<Integer> n7 = tree.insertLeft(n6, 3);//           21  13    / \                   
		Position<Integer> r8 = tree.insertRight(n6, 14);//                  5  [2]                 
		Position<Integer> r9 = tree.insertLeft(n7, 5);//                       / \                 
		Position<Integer> n10 = tree.insertRight(n7, 2);//                    15  0                 
		Position<Integer> r11 = tree.insertLeft(n10, 15);//                                        
		Position<Integer> r12 = tree.insertRight(n10, 0);//                                         
		Boolean[] answers = { false, true, false, false, true, true };
		Integer[] cost = { 20, 20, 20, 20, 20, 20 };
		Boolean[] revisedAnswers = Question5
				.revisedAnswers(tree, answers, cost);
		Boolean[] expectedAnswers = { true, true, false, false, true, true };
		Assert.assertArrayEquals(expectedAnswers, revisedAnswers);
	}
	@Test
	public void smallTree() {
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> n0 = tree.addRoot(0);//              [0]       
		Position<Integer> r1 = tree.insertLeft(n0, 8);//       / \       
		Position<Integer> r2 = tree.insertRight(n0, 2);//     8   2      
		Boolean[] answers = { false };
		Integer[] cost = { 5 };
		Boolean[] revisedAnswers = Question5
				.revisedAnswers(tree, answers, cost);
		Boolean[] expectedAnswers = { true };
		Assert.assertArrayEquals(expectedAnswers, revisedAnswers);
	}
	@Test
	public void tinyTree() {
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> n0 = tree.addRoot(0);//              [0]       
		Position<Integer> r1 = tree.insertLeft(n0, 9);//       / \       
		Position<Integer> r2 = tree.insertRight(n0, 2);//     9   2      
		Boolean[] answers = { false };
		Integer[] cost = { 8 };
		Boolean[] revisedAnswers = Question5
				.revisedAnswers(tree, answers, cost);
		Boolean[] expectedAnswers = { false };
		Assert.assertArrayEquals(expectedAnswers, revisedAnswers);
	}
}
