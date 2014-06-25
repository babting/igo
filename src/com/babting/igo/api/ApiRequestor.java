package com.babting.igo.api;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

public class ApiRequestor {
	private final ApiDefine apiDefine;
	private ApiCallback apiCallback;
	private ApiParams params;
	private boolean needApiCallback;
	private Context context;

	// 요청시 필요한 부가 헤더 정보들을 담는다. 동일 헤더가 기 존재시 overwrite 된다.
	private Map<String, String> addonHeaders = new HashMap<String, String>();
	
	ApiRequestor(ApiDefine apiDefine, ApiParams params, Context context) {
		this.apiDefine = apiDefine;
		this.params = params;
		this.context = context;
	}
	
	public static ApiRequestor newInstance(ApiDefine apiDefine, ApiParams params, Context context) {
		return new ApiRequestor(apiDefine, params, context);
	}
	
	public Context getContext() {
		return context;
	}

	public ApiDefine getApiDefine() {
		return apiDefine;
	}

	public ApiParams getParams() {
		return params;
	}

	public Map<String, String> getAddonHeaders() {
		return addonHeaders;
	}
	
	public ApiRequestor setApiCallback(ApiCallback apiCallback) {
		this.apiCallback = apiCallback;
		this.needApiCallback = true;
		return this;
	}
	
	public ApiCallback getApiCallback() {
		if (apiCallback == null && needApiCallback == true) {
			// new default callback (do nothing!)
			return new ApiCallback() {
				@Override
				public void onFailed(ApiResult failure, Context context) {
					// none
				}
				@Override
				public void onSucceed(ApiResult success, Context context) {
					// none
				}
			};
		}
		return apiCallback;
	}
	
	public boolean isNeedApiCallback() {
		return needApiCallback;
	}
}
