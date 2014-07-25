package com.babting.igo.api.apirun;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;

import com.babting.igo.api.ApiDefine;
import com.babting.igo.api.ApiRequestor;
import com.babting.igo.api.DefaultConstants.HttpMethod;
import com.babting.igo.api.HttpServiceHelper;
import com.babting.igo.api.result.ApiResult;
import com.babting.igo.exception.ApiRunException;

public abstract class ApiRun implements Callable<ApiResult> {
	private ApiDefine apiDefine;
	
	List<NameValuePair> requestParameter = new ArrayList<NameValuePair>(); // 요청 파라미터
	Map<String, String> requestHeader = new HashMap<String, String>(); // 요청헤더
	final ApiRequestor apiRequestor; // 각 API 요청의 정보를 들고 있는 객체 
	
	HttpResponse httpResponse = null; // http 응답 저장 객체
	
	ApiResult apiResult = null; // 콜백에 넘길 처리 결과
	
	/**
	 * ApiRun 생성자
	 * 
	 * @param context
	 * 	api호출을 요청한 context.(activity일수도있고 applicationContext일수도 있다)
	 * 
	 * @param option
	 * 	api호출 옵션
	 */
	public ApiRun(ApiRequestor requestor) {
		this.apiDefine = requestor.getApiDefine();
		this.apiRequestor = requestor;
	}

	@Override
	public ApiResult call() throws Exception {
		prepareRequest();
		executeRequest();
		
		return processResponse();
	}
	
	/**
	 * HTTP 통신을 준비한다.
	 */
	private void prepareRequest() throws ApiRunException {
		// 호출시 설정한 부가 헤더들을 설정한다.
		requestHeader.putAll(apiRequestor.getAddonHeaders());
		requestParameter.addAll(apiRequestor.getParams().getRequestParameter());
	}
	
	protected void executeRequest() throws URISyntaxException {
		// TODO Auto-generated method stub
		HttpServiceHelper httpServiceHelper = newHttpServiceHelper();
		
		if (!apiDefine.getMethod().equals(HttpMethod.HTTP_GET)) {
			httpServiceHelper.setRequestParameter(requestParameter);
		}
		httpServiceHelper.setRequestHeader(requestHeader);
		
		try {
			httpResponse = httpServiceHelper.execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * HTTP 수행 결과를 가지고 이후 처리를 진행한다.
	 * 
	 * @throws Exception
	 * @throws IOException
	 */
	protected abstract ApiResult processResponse() throws ApiRunException, IOException;
	
	private HttpServiceHelper newHttpServiceHelper() throws URISyntaxException {
		URI uri = null;
		
		if (apiDefine.getMethod().equals(HttpMethod.HTTP_GET)) {
			uri = URIUtils.createURI(apiDefine.getProtocol(), apiDefine.getHost(), -1, apiDefine.getUrl(), "", URLEncodedUtils.format(requestParameter, "UTF-8"));
		} else {
			uri = URIUtils.createURI(apiDefine.getProtocol(), apiDefine.getHost(), -1, apiDefine.getUrl(), "", "");
		}
		
		HttpServiceHelper httpServiceHelper = new HttpServiceHelper(new URI(uri.toString().replaceAll("#", "")), apiDefine);

		return httpServiceHelper;
	}

	public ApiDefine getApiDefine() {
		return apiDefine;
	}
}
