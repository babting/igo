package com.babting.igo.api.result;

import java.util.List;

import com.babting.igo.model.xml.NaverSearchApiModel;

public class NaverSearchApiResult extends ApiResult {
	private List<NaverSearchApiModel> naverSearchApiModelList;
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

	public List<NaverSearchApiModel> getNaverSearchApiModelList() {
		return naverSearchApiModelList;
	}

	public void setNaverSearchApiModelList(
			List<NaverSearchApiModel> naverSearchApiModelList) {
		this.naverSearchApiModelList = naverSearchApiModelList;
	}
}
