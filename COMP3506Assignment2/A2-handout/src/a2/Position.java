package a2;

/**
 * A Position has a start, an end and a height. This is an immutable class.
 * 
 * It can be used to denote the position of a horizontal platform or a segment
 * of a platform on the facade of a building. The start and end positions of a
 * platform denote the horizontal start and end distance, respectively, from the
 * left hand corner of the building; and the height denotes the vertical height
 * of the horizontal platform and may either be relative to the base of the
 * building itself or relative to another lower platform depending on the usage
 * of this class.
 */
public class Position {

	final private double start;
	final private double end;
	final private double height;

	/**
	 * Creates a new position with specified parameters.
	 */
	public Position(double start, double end, double height) {
		this.start = start;
		this.end = end;
		this.height = height;
	}

	public double start() {
		return start;
	}

	public double end() {
		return end;
	}

	public double height() {
		return height;
	}

	@Override
	public String toString() {
		return "<" + start + ", " + end + ", " + height + ">";
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Position)) {
			return false;
		}
		Position s = (Position) o;
		return (s.start == this.start && s.end == this.end && s.height == this.height);
	}

	@Override
	public int hashCode() {
		double h = start;
		h = 31 * h + end;
		h = 31 * h + height;
		return (int) h;
	}

}
