package com.example.demo;

public class UnsupportedSortByException extends RuntimeException {
	
	private static final long serialVersionUID = -9033309277716199105L;

	
	public UnsupportedSortByException() {
	}

	public UnsupportedSortByException(String message) {
		super("this sort paramenter does not exist : " + message);
	}

	public UnsupportedSortByException(Throwable cause) {
		super(cause);
	}

	public UnsupportedSortByException(String message, Throwable cause) {
		super(message, cause);
	}
}
