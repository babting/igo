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
	 * 통신객체
	 */
	private DefaultHttpClient httpClient;
	
	/**
	 * 응답객체
	 */
	public HttpResponse httpResponse;
	
	/**
	 * 파라미터로 전송할 값들
	 * 
	 * The request parameter.
	 */
	private List<NameValuePair> requestParameter;

	/**
	 * 헤더에 전송할 값들
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
			 * http.protocol.expect-continue : 클라이언트가 헤더를 먼저 보내서 서버의 allow 여부에 따라 request body를 넘길지 판단한다.
			     문제점 : HTTP/1.1을 지원하지 않는 웹 서버 혹은 애플리케이션에 이 헤더를 사용할 때 발생한다.
			     지원하지 않는 경우 적절한 응답을 주지 않기 때문에 문제가 된다.

			   따라서 true -> false로 교체함. 어짜피 Exception을 걸어두었기 때문에 exception 처리 가능.
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
