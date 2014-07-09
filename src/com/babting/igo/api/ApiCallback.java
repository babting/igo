package com.babting.igo.api;

import com.babting.igo.api.result.ApiResult;

import android.content.Context;

public interface ApiCallback {
	/**
	 * API 실행히 실패하였을 경우의 처리구현
	 * 
	 * @param result
	 * 	API 실패 원인등의 정보
	 */
	public void onFailed(ApiResult result, Context context);

	/**
	 * API 실행이 완료 되었을 경우의 처리 구현
	 * 
	 * @param result
	 *  API 응답을 VO에 맵핑하여 전달됨
	 */
	public void onSucceed(ApiResult result, Context context);
}
