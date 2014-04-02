package part3.test;

import org.junit.*;
import java.util.*;
import java.io.*;
import adt.*;
import part3.Question4;

/**
 * Basic tests for the printDecisionTree method from class Question4. A much
 * more extensive test suite will be performed for assessment of your code, but
 * this should get you started.
 */
public class Question4Test {

	// Retrieve the line separator in an OS independent way
	private final String LS = System.getProperty("line.separator");
	// A factory that we will use to create instances of trees
	private BinaryTreeFactory tFactory = new LinkedBinaryTreeFactory();

	// A stream that we will redirect standard output to.
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	// Remember what the old System.out is so that we can reset it
	private final PrintStream oldSystemOutputStream = System.out;

	/**
	 * Before each test, redirect standard output so that we can capture it.
	 */
	@Before
	public void redirectStdOutput() {
		System.setOut(new PrintStream(outContent));
	}

	/**
	 * After each test, fix up redirection of standard output.
	 */
	@After
	public void reinstallStdOutput() {
		System.setOut(oldSystemOutputStream);
	}

	@Test
	public void basicTest() throws IOException {
		// create an example decision tree
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> n0 = tree.addRoot(0);
		Position<Integer> n1 = tree.insertLeft(n0, 1);
		Position<Integer> n3 = tree.insertRight(n0, 3);
		Position<Integer> n2 = tree.insertLeft(n1, 2);
		Position<Integer> r2 = tree.insertRight(n1, 1);
		Position<Integer> r0 = tree.insertLeft(n2, 2);
		Position<Integer> r1 = tree.insertRight(n2, 9);
		Position<Integer> r3 = tree.insertLeft(n3, 2);
		Position<Integer> r4 = tree.insertRight(n3, 8);

		// get expected output: then get and check against solution
		String expected = readFromFile("indentedRep1.txt");
		Question4.printDecisionTree(tree);
		Assert.assertEquals(expected, outContent.toString());
	}
	
	@Test // This test will try testing a larger tree, with 2 digit numbers
	public void happyTree() throws IOException {
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> n0 = tree.addRoot(0);
		Position<Integer> n1 = tree.insertLeft(n0, 6);
		Position<Integer> n2 = tree.insertLeft(n1, 3);
		Position<Integer> r3 = tree.insertLeft(n2, 8);
		Position<Integer> r4 = tree.insertRight(n2, 11);
		Position<Integer> r5 = tree.insertRight(n1, 9);
		Position<Integer> n6 = tree.insertRight(n0, 12);
		Position<Integer> n7 = tree.insertLeft(n6, 5);
		Position<Integer> r8 = tree.insertRight(n6, 4);
		Position<Integer> r9 = tree.insertLeft(n7, 1);
		Position<Integer> n10 = tree.insertRight(n7, 10);
		Position<Integer> r11 = tree.insertLeft(n10, 2);
		Position<Integer> r12 = tree.insertRight(n10, 7);
		
		String expected = readFromFile("happyTree.txt");
		Question4.printDecisionTree(tree);
		Assert.assertEquals(expected, outContent.toString());
	}
	
	@Test // This test will try testing a lone root
	public void lonelyTree() throws IOException {
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> r999 = tree.addRoot(999);
		
		String expected = readFromFile("lonelyTree.txt");
		Question4.printDecisionTree(tree);
		Assert.assertEquals(expected, outContent.toString());
	}
	
	@Test // This test will try testing a long left branch
	public void tallTree() throws IOException {
		BinaryTree<Integer> tree = tFactory.<Integer> createBinaryTree();
		Position<Integer> n0 = tree.addRoot(0);
		Position<Integer> n1 = tree.insertLeft(n0, 1);
		Position<Integer> n2 = tree.insertLeft(n1, 2);
		Position<Integer> n3 = tree.insertLeft(n2, 3);
		Position<Integer> n4 = tree.insertLeft(n3, 4);
		Position<Integer> n5 = tree.insertLeft(n4, 5);
		Position<Integer> r6 = tree.insertLeft(n5, 6);
		Position<Integer> r7 = tree.insertRight(n5, 6);
		Position<Integer> r8 = tree.insertRight(n4, 5);
		Position<Integer> r9 = tree.insertRight(n3, 4);
		Position<Integer> r10 = tree.insertRight(n2, 3);
		Position<Integer> r11 = tree.insertRight(n1, 2);
		Position<Integer> r12 = tree.insertRight(n0, 1);
		
		String expected = readFromFile("tallTree.txt");
		Question4.printDecisionTree(tree);
		Assert.assertEquals(expected, outContent.toString());
	}
	
	
	// Helper methods

	/**
	 * Helper method to read a file and return its contents as a string.
	 * 
	 * @param f
	 *            the name of the file from which to read
	 * @return the contents of the file.
	 * @throws IOException
	 */
	private String readFromFile(String fileName) throws IOException {
		File f = new File(fileName);
		StringBuilder sb = new StringBuilder();
		Scanner scanner = new Scanner(f);
		try {
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine() + LS);
			}
		} finally {
			scanner.close();
		}
		return sb.toString();
	}

}
