package com.example.demo;

public class UnsupportedSortOrderException extends RuntimeException {

	private static final long serialVersionUID = -9033309277716199105L;

	
	public UnsupportedSortOrderException() {
	}

	public UnsupportedSortOrderException(String message) {
		super("This sort order paramenter is wrong : " + message + ", please use asc or desc");
	}

	public UnsupportedSortOrderException(Throwable cause) {
		super(cause);
	}

	public UnsupportedSortOrderException(String message, Throwable cause) {
		super(message, cause);
	}

}