package com.babting.igo.api.apirun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpStatus;

import com.babting.igo.api.ApiRequestor;
import com.babting.igo.api.result.ApiResult;
import com.babting.igo.api.result.SelectLocationApiResult;
import com.babting.igo.exception.ApiRunException;
import com.babting.igo.exception.ParserException;
import com.babting.igo.util.JacksonParser;

public class LocSearchApiRun extends ApiRun {
	/**
	 * ApiRun 생성자
	 * 
	 * @param context
	 * 	api호출을 요청한 context.(activity일수도있고 applicationContext일수도 있다)
	 * 
	 * @param option
	 * 	api호출 옵션
	 */
	public LocSearchApiRun(ApiRequestor requestor) {
		super(requestor);
	}

	/**
	 * HTTP 수행 결과를 가지고 이후 처리를 진행한다.
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
			if("igoserver_loc_search".equals(this.getApiDefine().getApiType())) {
				JacksonParser jsonParser = JacksonParser.getInstance();
				
				try {
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line;
					StringBuffer lineBuf = new StringBuffer();
					while((line = br.readLine()) != null) {
						lineBuf.append(line);
					}
					
					SelectLocationApiResult selectLocationApiResult = jsonParser.readValue(lineBuf.toString(), SelectLocationApiResult.class);
					selectLocationApiResult.setResultCode(ApiResult.STATUS_SUCCESS);
					apiResult = selectLocationApiResult;
				} catch (ParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			if(apiRequestor.isNeedApiCallback()) {
				apiRequestor.getApiCallback().onSucceed(apiResult, apiRequestor.getContext());
			}
		}
		
		
		return apiResult;
	}
}
