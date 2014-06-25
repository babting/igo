package com.babting.igo.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

import com.babting.igo.api.DefaultConstants.HttpMethod;

public class HttpServiceHelper {
	private static final String TAG = "HttpServiceHelper";
	
	/**
	 * The request url.
	 */
	private URI requestUrl;

	private ApiDefine apiDefine;
	
	/**
	 * ��Ű�ü
	 */
	private DefaultHttpClient httpClient;
	
	/**
	 * ���䰴ü
	 */
	public HttpResponse httpResponse;
	
	/**
	 * �Ķ���ͷ� ������ ����
	 * 
	 * The request parameter.
	 */
	private List<NameValuePair> requestParameter;

	/**
	 * ����� ������ ����
	 */
	private Map<String, String> requestHeader;

	
	public HttpServiceHelper(URI requestUrl, ApiDefine apiDefine) {
		this.requestUrl = requestUrl;
		this.apiDefine = apiDefine;
	}
	
	public HttpResponse execute() throws IOException, URISyntaxException, Exception {
		switch (apiDefine.getMethod()) {
			case HTTP_GET:
				return handleHttpGet();
			case HTTP_POST:
				return handleHttpPost();
			case HTTP_DELETE:
				return handleHttpDelete();
		}
		throw new UnsupportedOperationException();
	}

	private HttpResponse handleHttpDelete() {
		return null;
	}

	private HttpResponse handleHttpPost() {
		// TODO Auto-generated method stub
		return null;
	}

	private HttpResponse handleHttpGet() {
		//Logger.d(TAG, "Get Method == >> Final URL == >" + requestUrl);
		HttpGet httpGet = new HttpGet(requestUrl);
		try {
			httpResponse = getHttpClient().execute(httpGet);
		} catch (Exception e) {
			Log.e(TAG, "handleHttpGet", e);
		}
		
		return httpResponse;
	}
	
	/**
	 * Access point to set the http request parameter.
	 *
	 * @param requestParameter http request parameter
	 */
	public void setRequestParameter(List<NameValuePair> requestParameter) {
		this.requestParameter = requestParameter;
	}
	
	/**
	 * Access point to set the http request headers.
	 *
	 * @param requestHeader http request headers
	 */
	public void setRequestHeader(Map<String, String> requestHeader) {
		this.requestHeader = requestHeader;
	}
	
	private DefaultHttpClient getHttpClient() {
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter(DefaultConstants.HTTP_SOCKET_TIME_OUT_PARAM, apiDefine.getSocketTimeout());
			httpClient.getParams().setParameter(DefaultConstants.HTTP_CONNECTION_TIME_OUT_PARAM, apiDefine.getConnectionTimeout());
			httpClient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
			httpClient.getParams().setParameter("http.protocol.single-cookie-header", false);
			/*
			 * http.protocol.expect-continue : Ŭ���̾�Ʈ�� ����� ���� ������ ������ allow ���ο� ���� request body�� �ѱ��� �Ǵ��Ѵ�.
			     ������ : HTTP/1.1�� �������� �ʴ� �� ���� Ȥ�� ���ø����̼ǿ� �� ����� ����� �� �߻��Ѵ�.
			     �������� �ʴ� ��� ������ ������ ���� �ʱ� ������ ������ �ȴ�.

			   ���� true -> false�� ��ü��. ��¥�� Exception�� �ɾ�ξ��� ������ exception ó�� ����.
			 */
			httpClient.getParams().setParameter("http.protocol.expect-continue", false); //as-is : true
			httpClient.getParams().setParameter("http.method.response.buffer.warnlimit", 1048576);
			httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
				@Override
				public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
					
				}
			});
		}
		return httpClient;
	}
}
