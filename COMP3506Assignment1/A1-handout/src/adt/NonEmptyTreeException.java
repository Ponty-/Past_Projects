package adt;

@SuppressWarnings("serial")
public class NonEmptyTreeException extends RuntimeException {

	public NonEmptyTreeException() {
		super();
	}

	public NonEmptyTreeException(String s) {
		super(s);
	}

}
