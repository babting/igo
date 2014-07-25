package com.babting.igo.api.apirun;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpStatus;

import com.babting.igo.api.ApiRequestor;
import com.babting.igo.api.result.ApiResult;
import com.babting.igo.api.result.NaverSearchApiResult;
import com.babting.igo.exception.ApiRunException;
import com.babting.igo.xml_parser.NaverSearchApiXmlParserDOM;

public class NaverLocSearchApiRun extends ApiRun {
	/**
	 * ApiRun ������
	 * 
	 * @param context
	 * 	apiȣ���� ��û�� context.(activity�ϼ����ְ� applicationContext�ϼ��� �ִ�)
	 * 
	 * @param option
	 * 	apiȣ�� �ɼ�
	 */
	public NaverLocSearchApiRun(ApiRequestor requestor) {
		super(requestor);
	}

	/**
	 * HTTP ���� ����� ������ ���� ó���� �����Ѵ�.
	 * 
	 * @throws Exception
	 * @throws IOException
	 */
	@Override
	protected ApiResult processResponse() throws ApiRunException, IOException {
		InputStream is = httpResponse.getEntity().getContent();
		
		// Check Http response code
		if (httpResponse.getStatusLine() == null) {
			throw new ApiRunException(603);
		}
		
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		
		if (statusCode == HttpStatus.SC_OK) {
			if("naver_search".equals(this.getApiDefine().getApiType())) {
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
}
