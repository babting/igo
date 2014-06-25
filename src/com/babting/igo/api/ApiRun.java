package com.babting.igo.api;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;

import com.babting.igo.api.DefaultConstants.HttpMethod;
import com.babting.igo.exception.ApiRunException;
import com.babting.igo.exception.ParserException;
import com.babting.igo.util.JacksonParser;
import com.babting.igo.xml.model.NaverSearchApiXmlParserDOM;
import com.babting.igo.xml.model.NaverSearchApiXmlParserSAX;

public class ApiRun implements Callable<ApiResult> {
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
	
	private void executeRequest() throws URISyntaxException {
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
	private ApiResult processResponse() throws ApiRunException, IOException {
		InputStream is = httpResponse.getEntity().getContent();
		/*
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		StringBuffer lineBuf = new StringBuffer();
		while((line = br.readLine()) != null) {
			lineBuf.append(line);
		}
		*/
		// Check Http response code
		if (httpResponse.getStatusLine() == null) {
			throw new ApiRunException(603);
		}
		
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		
		if (statusCode == HttpStatus.SC_OK) {
			if("json".equals(apiDefine.getApiType())) { // json
				Class<? extends ApiResult> bindingClass = apiDefine.getBindingClass();
				try {
					if (bindingClass != null) {
						apiResult = JacksonParser.getInstance().readValue(is, bindingClass);
						
					} else {
						apiResult = JacksonParser.getInstance().readValue(is, ApiResult.class);
					}
				} catch (ParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if("naver_search".equals(apiDefine.getApiType())) {
				NaverSearchApiXmlParserDOM apiXmlParser = new NaverSearchApiXmlParserDOM(is);
				
				NaverSearchApiResult naverSearchApiResult = new NaverSearchApiResult();
				if(apiXmlParser.getErrorCode() != null && !"".equals(apiXmlParser.getErrorCode())) {
					naverSearchApiResult.setMessage(apiXmlParser.getMessage());
					naverSearchApiResult.setErrorCode(apiXmlParser.getErrorCode());
					naverSearchApiResult.setResultCode(ApiResult.API_ERROR);
				} else {
					naverSearchApiResult.setNaverSearchApiModelList(apiXmlParser.getNaverApiSearchModelList());
					naverSearchApiResult.setResultCode(ApiResult.STATUS_SUCCESS);
				}
				
				apiResult = naverSearchApiResult;
			}
			
			if(apiRequestor.isNeedApiCallback()) {
				apiRequestor.getApiCallback().onSucceed(apiResult, apiRequestor.getContext());
			}
			
		}
		
		return apiResult;
	}
	
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

}
