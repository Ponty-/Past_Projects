package adt;

@SuppressWarnings("serial")
public class EmptyStackException extends RuntimeException {

	public EmptyStackException() {
		super();
	}

	public EmptyStackException(String s) {
		super(s);
	}

}
