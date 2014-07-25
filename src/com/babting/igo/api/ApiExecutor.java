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
	 * ApiTask를 실행하기 위한 스레드 풀이다.
	 */
	static ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_COUNT);
	

	/**
	 * 대기중이거나 실행중인 ApiTask의 목록을 관리하기 위한 Map이다.
	 * 
	 * ApiExecuteTask인스턴스가 생성될 때 put 되고 done()될 때 remove()된다.
	 * 특정 context가 종료 될 때는 ApiExecutor.cancelAllTaskOf(context)를 통해서 관련된 모든 task를 중지시킬 수 있다. 
	 */
	static ConcurrentHashMap<ApiFutureTask, Context> livingTasks = new ConcurrentHashMap<ApiFutureTask, Context>();
	
	/**
	 * 작업완료 후 ApiCallBack을 UI의 thread에서 실행할 경우 사용한다.<br/>
	 * 실제 HTTP호출 등의 작업을 보려면 MyCallable을 봐야한다.
	 * 
	 * @param context Callback을 실행할 쓰레드 레퍼런스를 위해 넘겨준다.
	 * @param apiRequestor API 요청 객체
	 */
	public static void execute(final ApiRun apiRun) {
		ApiFutureTask apiExecutorTask = new ApiFutureTask(apiRun);
		executor.execute(apiExecutorTask);
	}
}
