package com.babting.igo.api;

import com.babting.igo.api.DefaultConstants.HttpMethod;
import com.babting.igo.api.result.ApiResult;

public class ApiDefine {
	/**
	 * ����� HTTP method
	 */
	private String apiType;
	
	protected HttpMethod method;
	
	protected String protocol;
	
	protected String host;
	
	/**
	 * ȣ��Ʈ�� ������ api ���
	 * e.g) /userinfo/getProfileinfo.nhn
	 */
	protected String url;
	
	/**
	 * �� api�� ���� connectionTimeout(�и�������)
	 * 0�̸� timeout����
	 */
	protected int connectionTimeout;

	/**
	 * �� api�� ���� socketTimeout(�и�������)
	 * 0�̸� timeout����
	 */
	protected int socketTimeout;
	
	/**
	 * ApiExecutor.execute(callback) ���� ���޵� callback�� onCompleteȣ��� ���޵� ����� ���ε� Ŭ����
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
