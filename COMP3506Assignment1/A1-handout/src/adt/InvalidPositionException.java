package adt;

@SuppressWarnings("serial")
public class InvalidPositionException extends RuntimeException {

	public InvalidPositionException() {
		super();
	}

	public InvalidPositionException(String s) {
		super(s);
	}

}
