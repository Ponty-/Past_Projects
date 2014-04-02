package a2;

import java.util.*;

public class HeightCalculatorNoDecimal {

	/**
	 * This method takes a (non-null) List of platform positions and returns a
	 * (non-null) map from each of those platform positions to a list of
	 * positions, as described in the assignment handout, representing
	 * subsegments of the platform and their propping heights. See the
	 * assignment handout for details.
	 */
	public static Map<Position, List<Position>> getProppingHeights(
			List<Position> platforms) {
		// Make a 2D Array to store platform heights. If a space is true,
		// then there is a platform there
		boolean[][] heightMap = fillHeightMap(platforms);
		// Create the map used to store platforms and their propping heights
		LinkedHashMap<Position, List<Position>> proppingHeights = new LinkedHashMap<Position, List<Position>>();
		// Position variables used to get propping heights
		Position current, currentHeight, previousHeight;
		// Iterate over the platforms and get heights
		Iterator<Position> itr = platforms.iterator();

		while (itr.hasNext()) {
			// Store the heights of the current platform in a linked list
			LinkedList<Position> currentHeights = new LinkedList<Position>();
			current = itr.next();
			// Add the first piece - this is here for simplicity with extending
			// the previous platform in the for loop
			currentHeights.add(getPieceHeight(
					heightMap,
					new Position(current.start(), current.start() + 1, current
							.height())));
			// Loop over the rest of the platform pieces
			for (double i = current.start() + 1; i < current.end(); i++) {
				previousHeight = currentHeights.peekLast();
				// Get the height of each piece of this platform, adding them to
				// the list
				currentHeight = getPieceHeight(heightMap, new Position(i,
						i + 1, current.height()));
				// If the height of the current piece is the same as the
				// previous piece, extend the length of the previous piece
				if (currentHeight.height() == previousHeight.height()) {
					currentHeights.set(currentHeights.size() - 1, new Position(
							previousHeight.start(), previousHeight.end() + 1,
							previousHeight.height()));
				} else {
					currentHeights.add(currentHeight);
				}
			}
			// Add the propping heights of this element to the map with this
			// element as the key
			proppingHeights.put(current, currentHeights);
		}

		return proppingHeights;
	}

	/**
	 * Sets up a 2D boolean array of the appropriate size for the given
	 * platforms, and sets all the locations that platforms are present to true.
	 * 
	 * @param platforms
	 *            The list of Positions off of which to base the 2D map array.
	 * @return A 2D boolean array of the appropriate size for the given
	 *         platforms filled with their locations.
	 */
	private static boolean[][] fillHeightMap(List<Position> platforms) {
		// Get the width and height of the facade
		double maxWidth = 0, maxHeight = 0;
		Iterator<Position> itr = platforms.iterator();
		Position current;

		// Iterate over all the platforms to find the largest width and height
		while (itr.hasNext()) {
			current = itr.next();
			if (current.end() > maxWidth) {
				maxWidth = current.end();
			}
			if (current.height() > maxHeight) {
				maxHeight = current.height();
			}
		}

		// Create a 2d array of the appropriate size
		boolean heightMap[][] = new boolean[(int) maxWidth + 1][(int) maxHeight + 1];

		// Reset the iterator
		itr = platforms.iterator();
		// Go through and set the platform locations in the 2d array
		while (itr.hasNext()) {
			current = itr.next();
			for (double i = current.start(); i < current.end(); i++) {
				heightMap[(int) i][(int) current.height()] = true;
			}
		}

		/* superAwesomeTestPrintFunction(heightMap); */

		return heightMap;
	}

	/**
	 * Gets the relative height of the given piece relative to the next lowest
	 * piece or the bottom of the given map.
	 * 
	 * @param heightMap
	 *            The map to be used.
	 * @param piece
	 *            The Position for the piece, storing its location and height.
	 * @return A Position storing the location and relative height of the given
	 *         piece.
	 */
	public static Position getPieceHeight(boolean[][] heightMap, Position piece) {
		// Get the next lowest height by checking the column it is in in the 2d
		// array
		for (double i = piece.height() - 1; i > 0; i--) {
			if (heightMap[(int) piece.start()][(int) i]) {
				return new Position(piece.start(), piece.end(), piece.height()
						- i);
			}
		}
		// If there is no platform, return the height relative to the bottom
		return piece;
	}

	/**
	 * HEY MICHAEL, DELETE THIS FUNCTION BEFORE UPLOADING
	 * 
	 * @param heightMap
	 */
	/*private static void superAwesomeTestPrintFunction(boolean[][] heightMap) {
		// Testing function to print out the 2d facade array
		System.out.println("Width height:" + heightMap.length + ' '
				+ heightMap[0].length);
		for (int i = 0; i < heightMap.length; i++) {
			for (int j = 0; j < heightMap[i].length; j++) {
				if (heightMap[i][j]) {
					System.out.print('|');
				} else {
					System.out.print(' ');
				}
			}
			System.out.print('\n');
		}
	}*/

}
