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
	
	List<NameValuePair> requestParameter = new ArrayList<NameValuePair>(); // ��û �Ķ����
	Map<String, String> requestHeader = new HashMap<String, String>(); // ��û���
	final ApiRequestor apiRequestor; // �� API ��û�� ������ ��� �ִ� ��ü 
	
	HttpResponse httpResponse = null; // http ���� ���� ��ü
	
	ApiResult apiResult = null; // �ݹ鿡 �ѱ� ó�� ���
	
	/**
	 * ApiRun ������
	 * 
	 * @param context
	 * 	apiȣ���� ��û�� context.(activity�ϼ����ְ� applicationContext�ϼ��� �ִ�)
	 * 
	 * @param option
	 * 	apiȣ�� �ɼ�
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
	 * HTTP ����� �غ��Ѵ�.
	 */
	private void prepareRequest() throws ApiRunException {
		// ȣ��� ������ �ΰ� ������� �����Ѵ�.
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
	 * HTTP ���� ����� ������ ���� ó���� �����Ѵ�.
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
