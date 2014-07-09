package com.babting.igo.api;

import com.babting.igo.api.result.ApiResult;

import android.content.Context;

public interface ApiCallback {
	/**
	 * API ������ �����Ͽ��� ����� ó������
	 * 
	 * @param result
	 * 	API ���� ���ε��� ����
	 */
	public void onFailed(ApiResult result, Context context);

	/**
	 * API ������ �Ϸ� �Ǿ��� ����� ó�� ����
	 * 
	 * @param result
	 *  API ������ VO�� �����Ͽ� ���޵�
	 */
	public void onSucceed(ApiResult result, Context context);
}
