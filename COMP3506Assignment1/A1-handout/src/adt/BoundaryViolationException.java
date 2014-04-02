package adt;

@SuppressWarnings("serial")
public class BoundaryViolationException extends RuntimeException {
	
	public BoundaryViolationException() {
		super();
	}

	public BoundaryViolationException(String s) {
		super(s);
	}
}
