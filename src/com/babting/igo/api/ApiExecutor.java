package com.babting.igo.api;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.babting.igo.api.apirun.ApiRun;

import android.content.Context;

public class ApiExecutor {
	protected static final String TAG = "ApiExecutor";
	protected static final String TAG_TRACE = "ApiExecutor.TRACE";
	protected static final int THREAD_POOL_COUNT = 20;
	
	/**
	 * ApiTask�� �����ϱ� ���� ������ Ǯ�̴�.
	 */
	static ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_COUNT);
	

	/**
	 * ������̰ų� �������� ApiTask�� ����� �����ϱ� ���� Map�̴�.
	 * 
	 * ApiExecuteTask�ν��Ͻ��� ������ �� put �ǰ� done()�� �� remove()�ȴ�.
	 * Ư�� context�� ���� �� ���� ApiExecutor.cancelAllTaskOf(context)�� ���ؼ� ���õ� ��� task�� ������ų �� �ִ�. 
	 */
	static ConcurrentHashMap<ApiFutureTask, Context> livingTasks = new ConcurrentHashMap<ApiFutureTask, Context>();
	
	/**
	 * �۾��Ϸ� �� ApiCallBack�� UI�� thread���� ������ ��� ����Ѵ�.<br/>
	 * ���� HTTPȣ�� ���� �۾��� ������ MyCallable�� �����Ѵ�.
	 * 
	 * @param context Callback�� ������ ������ ���۷����� ���� �Ѱ��ش�.
	 * @param apiRequestor API ��û ��ü
	 */
	public static void execute(final ApiRun apiRun) {
		ApiFutureTask apiExecutorTask = new ApiFutureTask(apiRun);
		executor.execute(apiExecutorTask);
	}
}
