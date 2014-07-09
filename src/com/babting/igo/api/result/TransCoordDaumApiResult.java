package com.babting.igo.api.result;


public class TransCoordDaumApiResult extends ApiResult {
	private DaumTransCoordResult daumTransCoordResult;
	private String message;
	private String errorCode;
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public DaumTransCoordResult getDaumTransCoordResult() {
		return daumTransCoordResult;
	}
	public void setDaumTransCoordResult(DaumTransCoordResult daumTransCoordResult) {
		this.daumTransCoordResult = daumTransCoordResult;
	}
}
