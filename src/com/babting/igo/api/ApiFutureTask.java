package com.babting.igo.api;

import java.util.concurrent.FutureTask;

import com.babting.igo.api.result.ApiResult;

public class ApiFutureTask extends FutureTask<ApiResult> {
	ApiRun apiExecution;
	
	/**
	 * @param callable
	 */
	public ApiFutureTask(ApiRun apiExecution) {
		super(apiExecution);
		this.apiExecution = apiExecution;
	}
}
