package com.babting.igo.api.result;

import java.util.ArrayList;
import java.util.List;

import com.babting.igo.model.LocationInfo;

public class SelectLocationApiResult extends ApiResult {
	private List<LocationInfo> locationList;
	private String message;
	private String errorCode;
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public List<LocationInfo> getLocationList() {
		if(locationList == null) locationList = new ArrayList<LocationInfo>();
		return locationList;
	}
	public void setLocationList(List<LocationInfo> locationList) {
		this.locationList = locationList;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
