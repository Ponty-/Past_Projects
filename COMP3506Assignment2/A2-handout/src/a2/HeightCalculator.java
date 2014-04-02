package a2;

import java.util.*;

public class HeightCalculator {

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
		ArrayList<LinkedList<ComPosition>> heightMap = fillHeightMap(platforms);
		// Create the map used to store platforms and their propping heights
		LinkedHashMap<Position, List<Position>> proppingHeights = new LinkedHashMap<Position, List<Position>>(platforms.size());
		// Position variables used to get propping heights
		Position current;
		// Iterate over the platforms and get heights
		Iterator<Position> itr = platforms.iterator();

		while (itr.hasNext()) {
			current = itr.next();

			// Add the propping heights of this element to the map with this
			// element as the key
			proppingHeights
					.put(current, getPlatformHeights(heightMap, current));
		}

		return proppingHeights;
	}

	/**
	 * Sets up a 2D boolean array of the appropriate size for the given
	 * platforms, and sets all the locations that platforms are present to true.
	 * 
	 * @param platforms
	 *            The list of Positions off of which to base the 2D map list.
	 * @return A 2D List containing sorted lists of platforms at each height.
	 */
	private static ArrayList<LinkedList<ComPosition>> fillHeightMap(
			List<Position> platforms) {
		// Get the width and height of the facade
		double maxHeight = 0;
		Iterator<Position> itr = platforms.iterator();
		Position current;
		ComPosition compCurrent;
		// Iterate over all the platforms to find the largest width and height
		while (itr.hasNext()) {
			current = itr.next();

			if (current.height() > maxHeight) {
				maxHeight = current.height();
			}
		}

		// Create a 2d array of the appropriate size
		ArrayList<LinkedList<ComPosition>> heightMap = new ArrayList<LinkedList<ComPosition>>(
				(int) maxHeight);
		// initialize all the linked lists
		for (int i = 0; i <= maxHeight; i++) {
			heightMap.add(new LinkedList<ComPosition>());
		}

		// Reset the iterator
		itr = platforms.iterator();
		// Go through and set the platform locations in the 2d array
		while (itr.hasNext()) {
			current = itr.next();
			// Convert it to the comparable version
			compCurrent = new ComPosition(current);
			heightMap.get((int) current.height()).add(compCurrent);
		}
		// Sort the lists using the sort function (merge sort)
		for (int i = 0; i < heightMap.size(); i++) {
			Collections.sort(heightMap.get(i));
		}
		return heightMap;
	}

	/**
	 * Gets the relative height of the given piece relative to the next lowest
	 * piece or the bottom of the given map.
	 * 
	 * @param heightMap
	 *            The map to be used.
	 * @param platform
	 *            The Position for the piece, storing its location and height.
	 * @return A List of positions, storing the heights of the given platform.
	 */
	private static LinkedList<Position> getPlatformHeights(
			ArrayList<LinkedList<ComPosition>> heightMap, Position platform) {
		double scanStart = platform.start();
		double scanStop = platform.end();
		LinkedList<Position> returnList = new LinkedList<Position>();
		// While there is still room on the platform
		while (scanStart < scanStop) {
			Position height = scan(
					new Position(scanStart, scanStop, platform.height()),
					heightMap);
			scanStart = height.end();
			returnList.add(height);
		}
		return returnList;
	}

	private static Position scan(Position scanPos,
			ArrayList<LinkedList<ComPosition>> heightMap) {
		double scanStart = scanPos.start(), scanStop = scanPos.end(), scanHeight = scanPos
				.height();
		// Get list of heights for given platform
		// For each height
		for (int i = (int) scanHeight; i > 0; i--) {
			Iterator<ComPosition> currentRow = heightMap.get(i).iterator();
			// Loop over each platform at that height
			while (currentRow.hasNext()) {
				ComPosition current = currentRow.next();
				if (current.start() > scanStop) {
					// if the start of the current platform is after where we
					// stop
					// scanning, go to the next row
					break;
				} else if (current.height() >= scanHeight
						|| current.end() <= scanStart) {
					// If the height is above or the same as the scanning
					// height,
					// check the next platform on the row
					continue;
				} else if (current.start() > scanStart
						&& current.start() < scanStop) {
					// If the current platform starts after where we start
					// scanning but ends before we stop, moving scanStop to the
					// start of that platform
					scanStop = current.start();

				} else if (current.start() <= scanStart) {
					// If the current platform starts at or
					if (current.end() < scanStop) {
						scanStop = current.end();
					}
					return (new Position(scanStart, scanStop, scanHeight
							- current.height()));
				}
			}
		}
		return new Position(scanStart, scanStop, scanHeight);
	}
}

class ComPosition extends Position implements Comparable<Position> {

	public ComPosition(Position pos) {
		super(pos.start(), pos.end(), pos.height());
	}

	public int compareTo(Position o) {
		if (this.equals(o)) {
			return 0;
		} else if (this.start() > o.start()) {
			return 1;
		} else {
			return -1;
		}
	}
}