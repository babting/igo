package com.babting.igo.api.result;

import android.os.Parcel;
import android.os.Parcelable;

public class DaumTransCoordResult implements Parcelable {
	private String result;
	private Double x;
	private Double y;
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Double getX() {
		return x;
	}
	public void setX(Double x) {
		this.x = x;
	}
	public Double getY() {
		return y;
	}
	public void setY(Double y) {
		this.y = y;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(x);
		dest.writeDouble(y);
		dest.writeString(result);
	}
}
