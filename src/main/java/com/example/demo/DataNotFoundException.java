package com.example.demo;

public class DataNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -8864742884664458267L;

	public DataNotFoundException() {
	}

	public DataNotFoundException(String message) {
		super(message);
	}

	public DataNotFoundException(Throwable cause) {
		super(cause);
	}

	public DataNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
