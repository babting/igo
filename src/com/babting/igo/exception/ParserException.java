package com.babting.igo.exception;

public class ParserException extends Exception {
	public ParserException(String message, Throwable cause) {
		super(message, cause);
		printStackTrace();
	}
	
	public ParserException(String message) {
		super(message);
		printStackTrace();
	}
}
