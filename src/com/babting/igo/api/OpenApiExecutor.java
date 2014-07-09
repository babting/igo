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

import com.babting.igo.activity.AddLocationActivity;
import com.babting.igo.activity.SearchLocationResultActivity;
import com.babting.igo.api.DefaultConstants.HttpMethod;
import com.babting.igo.api.result.ApiResult;
import com.babting.igo.api.result.NaverSearchApiResult;
import com.babting.igo.api.result.RegistLocationApiResult;
import com.babting.igo.api.result.SearchLocationResultAdapterModel;
import com.babting.igo.api.result.TransCoordDaumApiResult;
import com.babting.igo.model.xml.NaverSearchApiModel;

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
					model.setMapX(Double.toString(outAddr.getLatitude()));
					model.setMapY(Double.toString(outAddr.getLongitude()));
					locNameList.add(model);
				}
				
				Message msg = Message.obtain();
				msg.what = SearchLocationResultActivity.API_LOC_SEARCH_GOOGLE;
				Bundle bundle = new Bundle();
				bundle.putParcelableArrayList("googleApiResult", locNameList);
				msg.setData(bundle);
				resultHandler.sendMessage(msg);
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
								resultModel.setMapX(model.getMapX());
								resultModel.setMapY(model.getMapY());
								locNameList.add(resultModel);
							}
							
							Message msg = Message.obtain();
							msg.what = SearchLocationResultActivity.API_LOC_SEARCH_NAVER;
							Bundle bundle = new Bundle();
							bundle.putParcelableArrayList("naverApiResult", locNameList);
							msg.setData(bundle);
							
							resultHandler.sendMessage(msg);
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
	
	public static void transCoordByDaum(Context context, String x, String y, final Handler resultHandler) {
		ApiDefine apiDefine = new ApiDefine();
		apiDefine.setApiType("daum_trans_coord");
		apiDefine.setHost("apis.daum.net");
		apiDefine.setProtocol("http");
		apiDefine.setUrl("local/geo/transcoord");
		apiDefine.setMethod(HttpMethod.HTTP_GET);
		
		ApiParams params = new ApiParams();
		params.addParameter("apikey", "e0e69f5609148c88f3c542b35e60921bd015c9c1");
		params.addParameter("x", x);
		params.addParameter("y", y);
		params.addParameter("fromCoord", "KTM");
		params.addParameter("toCoord", "WGS84");
		params.addParameter("output", "json");
		ApiRequestor apiRequester = ApiRequestor.newInstance(apiDefine, params, context);
		
		apiRequester.setApiCallback(new ApiCallback() {
			@Override
			public void onSucceed(ApiResult apiResult, Context context) {
				if (apiResult.getResultCode() == ApiResult.STATUS_SUCCESS) {
					if(apiResult instanceof TransCoordDaumApiResult) {
						TransCoordDaumApiResult transCoordDaumApiResult = (TransCoordDaumApiResult)apiResult;
						
						Message msg = resultHandler.obtainMessage(SearchLocationResultActivity.API_TRANS_COORD_DAUM);
						//msg.what = SearchLocationResultActivity.API_TRANS_COORD_DAUM;
						Bundle bundle = new Bundle();
						bundle.putParcelable("transCoordDaumApiResult", transCoordDaumApiResult.getDaumTransCoordResult());
						msg.setData(bundle);
						resultHandler.sendMessage(msg);
					}
				}
			}

			@Override
			public void onFailed(ApiResult result, Context context) {
				// TODO Auto-generated method stub
				
			}
		});
			
		ApiExecutor.execute(apiRequester);
	}
	
	public static void registLocInfo(Context context, Double latitude, Double longitude, String locName, String desc, final Handler resultHandler) {
		ApiDefine apiDefine = new ApiDefine();
		apiDefine.setApiType("igoserver_loc_add");
		apiDefine.setHost("excellent-bolt-614.appspot.com");
		apiDefine.setProtocol("http");
		apiDefine.setUrl("api/registLoc");
		apiDefine.setMethod(HttpMethod.HTTP_GET);
		
		ApiParams params = new ApiParams();
		params.addParameter("latitude", Double.toString(latitude));
		params.addParameter("longitude", Double.toString(longitude));
		params.addParameter("locName", locName);
		params.addParameter("desc", desc);
		ApiRequestor apiRequester = ApiRequestor.newInstance(apiDefine, params, context);
		
		apiRequester.setApiCallback(new ApiCallback() {
			@Override
			public void onSucceed(ApiResult apiResult, Context context) {
				if (apiResult.getResultCode() == ApiResult.STATUS_SUCCESS) {
					if(apiResult instanceof RegistLocationApiResult) {
						Message msg = Message.obtain();
						msg.what = AddLocationActivity.API_REGIST_LOC;
						Bundle bundle = new Bundle();
						bundle.putParcelable("apiResult", apiResult);
						msg.setData(bundle);
						resultHandler.sendMessage(msg);
					}
				}
			}

			@Override
			public void onFailed(ApiResult result, Context context) {
				// TODO Auto-generated method stub
				
			}
		});
			
		ApiExecutor.execute(apiRequester);
	}
}
