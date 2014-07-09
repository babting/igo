package com.babting.igo.api.result;

import android.os.Parcel;
import android.os.Parcelable;

public class ApiResult implements Parcelable {
	public static final String STATUS_SUCCESS = "0";
	public static final String STATUS_NETWORK_UNAVAILABLE = "1";
	public static final String API_ERROR = "2";
	private String resultCode;

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeString(resultCode);
	}
}
