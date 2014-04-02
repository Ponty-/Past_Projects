package a2.test;

import org.junit.*;

import a2.*;

import java.io.*;
import java.util.*;

/**
 * A basic test for the getProppingHeights method from the class
 * HeightCalculator. A much much much more extensive test suite will be
 * performed for assessment of your code, but this should get you started.
 */
public class HeightCalculatorTest {
	
	String nl = System.getProperty("line.separator");
	
	@Test
	public void basicTest() {
		// create some platforms positions
		List<Position> platforms = new ArrayList<Position>();
		Position p1 = new Position(1, 3, 2);
		Position p2 = new Position(2, 5, 4);
		Position p3 = new Position(2, 4, 1);
		Position p4 = new Position(4, 7, 3);
		platforms.add(p1);
		platforms.add(p2);
		platforms.add(p3);
		platforms.add(p4);

		// get and check the propping heights for each platform
		Map<Position, List<Position>> actual = HeightCalculator
				.getProppingHeights(platforms); 
		List<Position> actualList1 = actual.get(p1);
		Assert.assertEquals(new Position(1, 2, 2), actualList1.get(0));
		Assert.assertEquals(new Position(2, 3, 1), actualList1.get(1));
		Assert.assertEquals(2, actualList1.size());

		List<Position> actualList2 = actual.get(p2);
		Assert.assertEquals(new Position(2, 3, 2), actualList2.get(0));
		Assert.assertEquals(new Position(3, 4, 3), actualList2.get(1));
		Assert.assertEquals(new Position(4, 5, 1), actualList2.get(2));
		Assert.assertEquals(3, actualList2.size());

		List<Position> actualList3 = actual.get(p3);
		Assert.assertEquals(new Position(2, 4, 1), actualList3.get(0));
		Assert.assertEquals(1, actualList3.size());

		List<Position> actualList4 = actual.get(p4);
		Assert.assertEquals(new Position(4, 7, 3), actualList4.get(0));
		Assert.assertEquals(1, actualList4.size());

		Assert.assertEquals(4, actual.size());
	}
	
	@Test
	public void singlePlatform() {
		List<Position> platforms = new ArrayList<Position>();
		Position p1 = new Position(4, 30, 5);
		
		platforms.add(p1);
		
		Map<Position, List<Position>> actual = HeightCalculator
				.getProppingHeights(platforms);
		
		List<Position> actualList = actual.get(p1);
		Assert.assertEquals(p1, actualList.get(0));
	}
	
	@Test
	public void stacky() {
		List<Position> platforms = new ArrayList<Position>();
		Position p1 = new Position(1, 5, 3);  //       ____      Like so.
		Position p2 = new Position(1, 5, 4);  //       ____
		Position p3 = new Position(1, 5, 6);  //       ____
		Position p4 = new Position(1, 5, 12); //       ____
		
		platforms.add(p1);
		platforms.add(p2);
		platforms.add(p3);
		platforms.add(p4);
		
		Map<Position, List<Position>> actual = HeightCalculator
				.getProppingHeights(platforms);
		
		List<Position> actualList1 = actual.get(p1);
		Assert.assertEquals(new Position(1, 5, 3), actualList1.get(0));
		Assert.assertEquals(1, actualList1.size());
		
		List<Position> actualList2 = actual.get(p2);
		Assert.assertEquals(new Position(1, 5, 1), actualList2.get(0));
		Assert.assertEquals(1, actualList2.size());
		
		List<Position> actualList3 = actual.get(p3);
		Assert.assertEquals(new Position(1, 5, 2), actualList3.get(0));
		Assert.assertEquals(1, actualList3.size());
		
		List<Position> actualList4 = actual.get(p4);
		Assert.assertEquals(new Position(1, 5, 6), actualList4.get(0));
		Assert.assertEquals(1, actualList4.size());
	}
	
	@Test
	public void joined() {
		List<Position> platforms = new ArrayList<Position>();
		Position p1 = new Position(0, 10, 10); //   _______._______   They are touching at their ends
		Position p2 = new Position(10, 15, 10);
		
		platforms.add(p1);
		platforms.add(p2);
		
		Map<Position, List<Position>> actual = HeightCalculator
				.getProppingHeights(platforms);
		List<Position> actualList1 = actual.get(p1);
		Assert.assertEquals(new Position(0, 10, 10), actualList1.get(0));
		Assert.assertEquals(1, actualList1.size());

		List<Position> actualList2 = actual.get(p2);
		Assert.assertEquals(new Position(10, 15, 10), actualList2.get(0));
		Assert.assertEquals(1, actualList2.size());
	}
	
	@Test
	public void tipTouchTest() {
		List<Position> platforms = new ArrayList<Position>();
		Position p1 = new Position(7, 12, 5);  //            _______
		Position p2 = new Position(12, 15, 6); //     _______       ______
		Position p3 = new Position(12, 15, 4);  //           _______         All tips are vertical each other
		Position p4 = new Position(15, 20, 5);
		
		platforms.add(p1);
		platforms.add(p2);
		platforms.add(p3);
		platforms.add(p4);
		
		Map<Position, List<Position>> actual = HeightCalculator
				.getProppingHeights(platforms);
		List<Position> actualList1 = actual.get(p1);
		Assert.assertEquals(new Position(7, 12, 5), actualList1.get(0));
		Assert.assertEquals(1, actualList1.size());

		List<Position> actualList2 = actual.get(p2);
		Assert.assertEquals(new Position(12, 15, 2), actualList2.get(0));
		Assert.assertEquals(1, actualList2.size());
		
		List<Position> actualList3 = actual.get(p3);
		Assert.assertEquals(new Position(12, 15, 4), actualList3.get(0));
		Assert.assertEquals(1, actualList3.size());
		
		List<Position> actualList4 = actual.get(p4);
		Assert.assertEquals(new Position(15, 20, 5), actualList4.get(0));
		Assert.assertEquals(1, actualList4.size());
		
	}
	
	@Test
	public void aboveAndLeft() {
		List<Position> platforms = new ArrayList<Position>();
		Position p1 = new Position(0, 10, 10); //     _______
		Position p2 = new Position(5, 15, 5);  //         _______                 Like this
		
		platforms.add(p1);
		platforms.add(p2);
		
		Map<Position, List<Position>> actual = HeightCalculator
				.getProppingHeights(platforms);
		List<Position> actualList1 = actual.get(p1);
		Assert.assertEquals(new Position(0, 5, 10), actualList1.get(0));
		Assert.assertEquals(new Position(5, 10, 5), actualList1.get(1));
		Assert.assertEquals(2, actualList1.size());

		List<Position> actualList2 = actual.get(p2);
		Assert.assertEquals(new Position(5, 15, 5), actualList2.get(0));
		Assert.assertEquals(1, actualList2.size());
	}
	
	@Test
	public void aboveAndRight() {
		List<Position> platforms = new ArrayList<Position>();
		Position p1 = new Position(5, 15, 10); //         _______
		Position p2 = new Position(0, 10, 5);  //     _______                 Like this
		
		platforms.add(p1);
		platforms.add(p2);
		
		Map<Position, List<Position>> actual = HeightCalculator
				.getProppingHeights(platforms);
		List<Position> actualList1 = actual.get(p1);
		Assert.assertEquals(new Position(5, 10, 5), actualList1.get(0));
		Assert.assertEquals(new Position(10, 15, 10), actualList1.get(1));
		Assert.assertEquals(2, actualList1.size());

		List<Position> actualList2 = actual.get(p2);
		Assert.assertEquals(new Position(0, 10, 5), actualList2.get(0));
		Assert.assertEquals(1, actualList2.size());
	}
	
	@Test
	public void bigAboveSmall() {
		List<Position> platforms = new ArrayList<Position>();
		Position p1 = new Position(5, 20, 10); //           _____________          Like this
		Position p2 = new Position(10, 15, 5); //               _____
		
		platforms.add(p1);
		platforms.add(p2);
		
		Map<Position, List<Position>> actual = HeightCalculator
				.getProppingHeights(platforms);
		List<Position> actualList1 = actual.get(p1);
		Assert.assertEquals(new Position(5, 10, 10), actualList1.get(0));
		Assert.assertEquals(new Position(10, 15, 5), actualList1.get(1));
		Assert.assertEquals(new Position(15, 20, 10), actualList1.get(2));
		Assert.assertEquals(3, actualList1.size());

		List<Position> actualList2 = actual.get(p2);
		Assert.assertEquals(new Position(10, 15, 5), actualList2.get(0));
		Assert.assertEquals(1, actualList2.size());
	}
	
	@Test
	public void smallAboveBig() {
		List<Position> platforms = new ArrayList<Position>();
		Position p1 = new Position(5, 20, 5); //                   _____           Like this
		Position p2 = new Position(10, 15, 10); //             _____________
		
		platforms.add(p1);
		platforms.add(p2);
		
		Map<Position, List<Position>> actual = HeightCalculator
				.getProppingHeights(platforms);
		List<Position> actualList1 = actual.get(p1);
		Assert.assertEquals(new Position(5, 20, 5), actualList1.get(0));
		Assert.assertEquals(1, actualList1.size());

		List<Position> actualList2 = actual.get(p2);
		Assert.assertEquals(new Position(10, 15, 5), actualList2.get(0));
		Assert.assertEquals(1, actualList2.size());
	}
	
	@Test
	public void decimalPlatforms() throws IOException {
		// create some platforms positions
		List<Position> platforms = new ArrayList<Position>();
		Position p1 = new Position(1.5, 3.5, 2);
		Position p2 = new Position(2, 5, 4.5);
		Position p3 = new Position(2.5, 4, 1);
		Position p4 = new Position(3, 7.5, 4);
		platforms.add(p1);
		platforms.add(p2);
		platforms.add(p3);
		platforms.add(p4);

		// get and check the propping heights for each platform
		Map<Position, List<Position>> actual = HeightCalculator
				.getProppingHeights(platforms); 
		List<Position> actualList1 = actual.get(p1);
		Assert.assertEquals(new Position(1.5, 2.5, 2), actualList1.get(0));
		Assert.assertEquals(new Position(2.5, 3.5, 1), actualList1.get(1));
		Assert.assertEquals(2, actualList1.size());

		List<Position> actualList2 = actual.get(p2);
		Assert.assertEquals(new Position(2, 3, 2.5), actualList2.get(0));
		Assert.assertEquals(new Position(3, 5, 0.5), actualList2.get(1));
		Assert.assertEquals(2, actualList2.size());

		List<Position> actualList3 = actual.get(p3);
		Assert.assertEquals(new Position(2.5, 4, 1), actualList3.get(0));
		Assert.assertEquals(1, actualList3.size());

		List<Position> actualList4 = actual.get(p4);
		Assert.assertEquals(new Position(3, 3.5, 2), actualList4.get(0));
		Assert.assertEquals(new Position(3.5, 4, 3), actualList4.get(1));
		Assert.assertEquals(new Position(4, 7.5, 4), actualList4.get(2));
		Assert.assertEquals(3, actualList4.size());

		Assert.assertEquals(4, actual.size());
	}
	
	@Test
	public void happyTest() throws IOException {
		List<Position> platforms = getPlatformsFromFile("platforms.txt");
		
		Map<Position, List<Position>> actual = HeightCalculator
				.getProppingHeights(platforms);
		
		String actualString = mapToString(actual);
		String expectedString = getFile("happyheights.txt");
		// This test uses Addison's Results, which are better.
		// Proof: http://i.imgur.com/38kfxza.png
		Assert.assertEquals(expectedString, actualString);
	}
	
	@Test
	public void underCutTest() {
		// create some platforms positions
		List<Position> platforms = new ArrayList<Position>();
		Position p1 = new Position(2, 7, 4);
		Position p2 = new Position(3, 6, 3);
		Position p3 = new Position(1, 4, 2);
		platforms.add(p1);
		platforms.add(p2);
		platforms.add(p3);

		// get and check the propping heights for each platform
		Map<Position, List<Position>> actual = HeightCalculator
				.getProppingHeights(platforms); 
		List<Position> actualList1 = actual.get(p1);
		Assert.assertEquals(new Position(2, 3, 2), actualList1.get(0));
		Assert.assertEquals(new Position(3, 6, 1), actualList1.get(1));
		Assert.assertEquals(new Position(6, 7, 4), actualList1.get(2));
		Assert.assertEquals(3, actualList1.size());

		List<Position> actualList2 = actual.get(p2);
		Assert.assertEquals(new Position(3, 4, 1), actualList2.get(0));
		Assert.assertEquals(new Position(4, 6, 3), actualList2.get(1));
		Assert.assertEquals(2, actualList2.size());

		List<Position> actualList3 = actual.get(p3);
		Assert.assertEquals(new Position(1, 4, 2), actualList3.get(0));
		Assert.assertEquals(1, actualList3.size());

		Assert.assertEquals(3, actual.size());
	}
	
	private List<Position> getPlatformsFromFile(String filename) 
		throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		List<Position> out = new ArrayList<Position>();
		
		while ((line = br.readLine()) != null) {
			String[] split = line.split("\\s+");
			int start = Integer.parseInt(split[0]);
			int end = Integer.parseInt(split[1]);
			int height = Integer.parseInt(split[2]);
			out.add(new Position(start, end, height));
		}
		
		br.close();
		return out;
	}
	
	private String mapToString(Map<Position, List<Position>> actual) {
		String toFile = "";
		
		for (Position p: actual.keySet()) {
			toFile += p + nl;
			for (Position q: actual.get(p)) {
				toFile += "\t" + q + nl;
			}
		}
		return toFile;
	}
	
	private String getFile(String filename) 
			throws IOException {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String out = "", line;
			
			while ((line = br.readLine()) != null) {
				out += line + nl;
			}
			br.close();
			return out;
		}
}
