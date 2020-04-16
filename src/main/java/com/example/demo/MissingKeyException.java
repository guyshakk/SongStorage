package com.example.demo;

public class MissingKeyException extends RuntimeException {

	private static final long serialVersionUID = -8864742884664458267L;

	public MissingKeyException() {
	}

	public MissingKeyException(String message) {
		super("In storage - discovered an object with no such field called " + message);
	}

	public MissingKeyException(Throwable cause) {
		super(cause);
	}

	public MissingKeyException(String message, Throwable cause) {
		super(message, cause);
	}

}
