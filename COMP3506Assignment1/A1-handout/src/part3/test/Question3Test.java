package part3.test;

import org.junit.*;
import adt.*;
import part3.Question3;

/**
 * Basic tests for the reward method from class Question3. A much more extensive
 * test suite will be performed for assessment of your code, but this should get
 * you started.
 */
public class Question3Test {

	// A factory that we will use to create instances of trees
	BinaryTreeFactory tFactory = new LinkedBinaryTreeFactory();

	@Test
	public void basicTest() {
		// create an example decision tree
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> n0 = tree.addRoot(0);
		Position<Integer> n1 = tree.insertLeft(n0, 1);//               [0]                           
		Position<Integer> n3 = tree.insertRight(n0, 3);//              /  \                            
		Position<Integer> n2 = tree.insertLeft(n1, 2);//              /    \                           
		Position<Integer> r2 = tree.insertRight(n1, 1);//           [1]    [3]                        
		Position<Integer> r0 = tree.insertLeft(n2, 2);//            / \    / \                         
		Position<Integer> r1 = tree.insertRight(n2, 9);//         [2]  1  2   8                       
		Position<Integer> r3 = tree.insertLeft(n3, 2);//          / \  ^.                           
		Position<Integer> r4 = tree.insertRight(n3, 8);//        2   9   `- You want the 1             

		boolean[] answers = { true, false, true, true };
		Assert.assertEquals("Reward is incorrect.", 1,
				Question3.reward(tree, answers));
	}
	
	@Test
	public void sadTree() {
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> n0 = tree.addRoot(0);
		Position<Integer> n1 = tree.insertLeft(n0, 2);//                  [0]                    
		Position<Integer> n2 = tree.insertLeft(n1, 4);//                 /   \    You want the 0
		Position<Integer> r3 = tree.insertLeft(n2, 21);//              [2]    \                 
		Position<Integer> r4 = tree.insertRight(n2, 13);//             / \    [3]               
		Position<Integer> r5 = tree.insertRight(n1, 17);//           [4]  17   /\                
		Position<Integer> n6 = tree.insertRight(n0, 3);//            / \     [5] 14                
		Position<Integer> n7 = tree.insertLeft(n6, 5);//           21  13    / \                   
		Position<Integer> r8 = tree.insertRight(n6, 14);//                  5  [1]                 
		Position<Integer> r9 = tree.insertLeft(n7, 5);//                       / \                 
		Position<Integer> n10 = tree.insertRight(n7, 1);//                    15  0                 
		Position<Integer> r11 = tree.insertLeft(n10, 15);//                                        
		Position<Integer> r12 = tree.insertRight(n10, 0);//                                   
		boolean[] answers = { false, false, true, true, false, false };
		Assert.assertEquals(0, Question3.reward(tree, answers));
	}
	@Test
	public void lethargicTree() {
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> n0 = tree.addRoot(0);
		Position<Integer> n1 = tree.insertLeft(n0, 2);//                  [0]                    
		Position<Integer> n2 = tree.insertLeft(n1, 4);//                 /   \    You want the 13
		Position<Integer> r3 = tree.insertLeft(n2, 21);//              [2]    \                 
		Position<Integer> r4 = tree.insertRight(n2, 13);//             / \    [3]               
		Position<Integer> r5 = tree.insertRight(n1, 17);//           [4]  17   /\                
		Position<Integer> n6 = tree.insertRight(n0, 3);//            / \     [5] 14                
		Position<Integer> n7 = tree.insertLeft(n6, 5);//           21  13    / \                   
		Position<Integer> r8 = tree.insertRight(n6, 14);//                  5  [1]                 
		Position<Integer> r9 = tree.insertLeft(n7, 5);//                       / \                 
		Position<Integer> n10 = tree.insertRight(n7, 1);//                    15  0                 
		Position<Integer> r11 = tree.insertLeft(n10, 15);//                                        
		Position<Integer> r12 = tree.insertRight(n10, 0);//                                   
		boolean[] answers = { true, false, true, false, false, false };
		Assert.assertEquals(13, Question3.reward(tree, answers));
	}
	@Test
	public void boredTree() {
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> n0 = tree.addRoot(0);
		Position<Integer> n1 = tree.insertLeft(n0, 2);//                  [0]                    
		Position<Integer> n2 = tree.insertLeft(n1, 4);//                 /   \    You want the 14
		Position<Integer> r3 = tree.insertLeft(n2, 21);//              [2]    \                 
		Position<Integer> r4 = tree.insertRight(n2, 13);//             / \    [3]               
		Position<Integer> r5 = tree.insertRight(n1, 17);//           [4]  17   /\                
		Position<Integer> n6 = tree.insertRight(n0, 3);//            / \     [5] 14                
		Position<Integer> n7 = tree.insertLeft(n6, 5);//           21  13    / \                   
		Position<Integer> r8 = tree.insertRight(n6, 14);//                  5  [1]                 
		Position<Integer> r9 = tree.insertLeft(n7, 5);//                       / \                 
		Position<Integer> n10 = tree.insertRight(n7, 1);//                    15  0                 
		Position<Integer> r11 = tree.insertLeft(n10, 15);//                                        
		Position<Integer> r12 = tree.insertRight(n10, 0);//                                   
		boolean[] answers = { false, true, true, false, false, true };
		Assert.assertEquals(14, Question3.reward(tree, answers));
	}
	@Test
	public void crazyTree() {
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> n0 = tree.addRoot(0);
		Position<Integer> n1 = tree.insertLeft(n0, 1);//                  [0]                     
		Position<Integer> n2 = tree.insertLeft(n1, 4);//                 /   \    You want the 15
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
		boolean[] answers = { false, true, true, false, true, true };
		Assert.assertEquals(15, Question3.reward(tree, answers));
	}
	@Test
	public void magicTree() {
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> n0 = tree.addRoot(0);
		Position<Integer> n1 = tree.insertLeft(n0, 1);//                  [0]                     
		Position<Integer> n2 = tree.insertLeft(n1, 4);//                 /   \    You want the 5
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
		boolean[] answers = { false, true, false, true, false, true };
		Assert.assertEquals(5, Question3.reward(tree, answers));
	}
	@Test
	public void doubleTree() {
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> n0 = tree.addRoot(0);
		Position<Integer> n1 = tree.insertLeft(n0, 1);//                  [0]                     
		Position<Integer> n2 = tree.insertLeft(n1, 3);//                 /   \    You want the 13
		Position<Integer> r3 = tree.insertLeft(n2, 21);//              [1]    \                  
		Position<Integer> r4 = tree.insertRight(n2, 13);//             / \    [5]                 
		Position<Integer> r5 = tree.insertRight(n1, 17);//           [3]  17   /\                
		Position<Integer> n6 = tree.insertRight(n0, 5);//            / \     [3] 14                
		Position<Integer> n7 = tree.insertLeft(n6, 3);//           21  13    / \                   
		Position<Integer> r8 = tree.insertRight(n6, 14);//                  5  [2]                 
		Position<Integer> r9 = tree.insertLeft(n7, 5);//                       / \                 
		Position<Integer> n10 = tree.insertRight(n7, 2);//                    15  0                 
		Position<Integer> r11 = tree.insertLeft(n10, 15);//                                        
		Position<Integer> r12 = tree.insertRight(n10, 0);//       THIS TREE HAS QUESTION 0 ON IT TWICE.                       
		boolean[] answers = { true, true, false, false, false, true };
		Assert.assertEquals(13, Question3.reward(tree, answers));
	}
	@Test
	public void microTree() {
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> n0 = tree.addRoot(0);//              [0]       
		Position<Integer> r1 = tree.insertLeft(n0, 9);//       / \       
		Position<Integer> r2 = tree.insertRight(n0, 2);//     9   2      
		boolean[] answers = { false };
		Assert.assertEquals(2, Question3.reward(tree, answers));
	}
}
