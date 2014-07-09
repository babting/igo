package com.babting.igo.api;

import com.babting.igo.api.DefaultConstants.HttpMethod;
import com.babting.igo.api.result.ApiResult;

public class ApiDefine {
	/**
	 * 사용할 HTTP method
	 */
	private String apiType;
	
	protected HttpMethod method;
	
	protected String protocol;
	
	protected String host;
	
	/**
	 * 호스트를 제외한 api 경로
	 * e.g) /userinfo/getProfileinfo.nhn
	 */
	protected String url;
	
	/**
	 * 이 api에 대한 connectionTimeout(밀리세컨드)
	 * 0이면 timeout없음
	 */
	protected int connectionTimeout;

	/**
	 * 이 api에 대한 socketTimeout(밀리세컨드)
	 * 0이면 timeout없음
	 */
	protected int socketTimeout;
	
	/**
	 * ApiExecutor.execute(callback) 에서 전달된 callback에 onComplete호출시 전달될 결과의 바인딩 클래스
	 */
	private Class<? extends ApiResult> bindingClass;
	
	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getConnectionTimeout() {
		if (connectionTimeout > 0) {
			return connectionTimeout;
		} else {
			return DefaultConstants.HTTP_TIME_OUT_CONNECTION;
		}
	}

	public int getSocketTimeout() {
		if (socketTimeout > 0) {
			return socketTimeout;
		} else {
			return DefaultConstants.HTTP_TIME_OUT_SOCKET;
		}
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	public Class<? extends ApiResult> getBindingClass() {
		return bindingClass;
	}
}
