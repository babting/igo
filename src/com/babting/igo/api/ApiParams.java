package com.babting.igo.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.babting.igo.util.HmacUtil;

public class ApiParams {
	/**
	 * 요청 파라미터 목록
	 */
	private List<NameValuePair> requestParameter = new ArrayList<NameValuePair>();
	
	public ApiParams addParameter(String name, String value) {
		requestParameter.add(new BasicNameValuePair(name, value));
		return this;
	}
	
	public List<NameValuePair> getRequestParameter() {
		return requestParameter;
	}
	
	public String makeParameterString(String prefix) {
		StringBuilder sb = new StringBuilder(prefix).append("!");

		for (NameValuePair nameValuePair : requestParameter) {
			sb.append(nameValuePair.getName()).append("=").append(HmacUtil.encodeBase64(nameValuePair.getValue().substring(0, Math.min(10, nameValuePair.getValue().length())).getBytes())).append("&");
		}

		return sb.toString();
	}
}
