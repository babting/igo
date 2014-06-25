package com.babting.igo.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.babting.igo.api.DefaultConstants.HttpMethod;
import com.babting.igo.xml.model.NaverSearchApiModel;
import com.babting.igo.xml.model.SearchLocationResultAdapterModel;

public class OpenApiExecutor {
	/**
	 * Google 위치 검색 API 호출
	 * @param searchLocName
	 * @param resultHandler
	 */
	public static void searchGoogleLocation(Context context, String searchLocName, final Handler resultHandler) {
		List<Address> addressList = null;
		// 지오코더 객체 생성
		Geocoder gc = new Geocoder(context, Locale.KOREAN);
				
		try {
			addressList = gc.getFromLocationName(searchLocName, 20);
			
			if(addressList != null) {
				ArrayList<SearchLocationResultAdapterModel> locNameList = new ArrayList<SearchLocationResultAdapterModel>();
				for(int i = 0 ; i < addressList.size() ; i++) {
					SearchLocationResultAdapterModel model = new SearchLocationResultAdapterModel();
					Address outAddr = addressList.get(i);
					
					StringBuffer addrInfoStrBuf = new StringBuffer();
					
					int addrCount = outAddr.getMaxAddressLineIndex() + 1;
					for(int k = 0 ; k < addrCount ; k++) {
						addrInfoStrBuf.append(outAddr.getAddressLine(k));
					}
					model.setTitle(addrInfoStrBuf.toString());
					locNameList.add(model);
				}
				
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putParcelableArrayList("googleApiResult", locNameList);
				msg.setData(bundle);
				resultHandler.handleMessage(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Naver 검색 API 호출
	 * @param searchLocName
	 * @param resultHandler
	 * @throws UnsupportedEncodingException
	 */
	public static void searchNaverLocation(Context context, String searchLocName, final Handler resultHandler) throws UnsupportedEncodingException {
		ApiDefine apiDefine = new ApiDefine();
		apiDefine.setApiType("naver_search");
		apiDefine.setHost("openapi.naver.com");
		apiDefine.setProtocol("http");
		apiDefine.setUrl("search");
		apiDefine.setMethod(HttpMethod.HTTP_GET);
		
		ApiParams params = new ApiParams();
		params.addParameter("key", "cb6b44a501d624faac155be58077fb54");
		params.addParameter("query", searchLocName);
		params.addParameter("target", "local");
		params.addParameter("start", "1");
		params.addParameter("display", "30");
		ApiRequestor apiRequester = ApiRequestor.newInstance(apiDefine, params, context);
		
		apiRequester.setApiCallback(new ApiCallback() {
			@Override
			public void onSucceed(ApiResult apiResult, Context context) {
				if (apiResult.getResultCode() == ApiResult.STATUS_SUCCESS) {
					if(apiResult instanceof NaverSearchApiResult) {
						NaverSearchApiResult naverApiResult = (NaverSearchApiResult)apiResult;
						
						if(naverApiResult.getResultCode() != ApiResult.STATUS_SUCCESS) {
							Log.d("igo", "naver search result setting error : " + naverApiResult.getMessage() + " / " + naverApiResult.getErrorCode());
						} else {
							List<NaverSearchApiModel> resultList = naverApiResult.getNaverSearchApiModelList();
							ArrayList<SearchLocationResultAdapterModel> locNameList = new ArrayList<SearchLocationResultAdapterModel>();
							
							for(NaverSearchApiModel model : resultList) {
								SearchLocationResultAdapterModel resultModel = new SearchLocationResultAdapterModel();
								resultModel.setTitle(model.getTitle());
								resultModel.setDescription(model.getDescription());
								locNameList.add(resultModel);
							}
							
							Message msg = new Message();
							Bundle bundle = new Bundle();
							bundle.putParcelableArrayList("naverApiResult", locNameList);
							msg.setData(bundle);
							resultHandler.handleMessage(msg);
						}
						
						Log.d("igo", "naver search result setting complete");
					}
				}else{
				}
			}

			@Override
			public void onFailed(ApiResult apiResult, Context context) {
				
			}
		});
		
		ApiExecutor.execute(apiRequester);
	}
}
