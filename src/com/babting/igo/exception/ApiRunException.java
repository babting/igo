package com.babting.igo.exception;

public class ApiRunException extends Exception {
	int failureType;
	
	public ApiRunException(Exception e, int failureType) {
		super(e);
		this.failureType = failureType;
	}

	public ApiRunException(int failureType) {
		this.failureType = failureType;
	}

	public int getFailureType() {
		return failureType;
	}

	public void setFailureType(int failureType) {
		this.failureType = failureType;
	}
}
