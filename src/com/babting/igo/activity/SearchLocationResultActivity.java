package com.babting.igo.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.babting.igo.R;
import com.babting.igo.adapter.SearchLocationResultAdapter;
import com.babting.igo.api.OpenApiExecutor;
import com.babting.igo.api.result.ApiResult;
import com.babting.igo.api.result.DaumTransCoordResult;
import com.babting.igo.api.result.SearchLocationResultAdapterModel;

public class SearchLocationResultActivity extends Activity {
	private Button cancelBtn;
	private Button searchLocNaverResultBtn;
	private Button searchLocGoogleResultBtn;
	
	private ListView searchLocListView;
	
	private String searchLocName;
	
	private SearchLocationResultAdapter adapter;
	List<SearchLocationResultAdapterModel> resultLocNameList = new ArrayList<SearchLocationResultAdapterModel>();
	
	private String listViewServiceDiv = "google";
	
	private String selectedTitleStr = "";
	private String selectedDescStr = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.search_location_list);
		
		cancelBtn = (Button) findViewById(R.id.searchLocResultBackBtn);
		searchLocNaverResultBtn = (Button) findViewById(R.id.searchLocNaverResultBtn);
		searchLocGoogleResultBtn = (Button) findViewById(R.id.searchLocGoogleResultBtn);
		
		searchLocListView = (ListView) findViewById(R.id.searchLocListView);
		
		// list view
		adapter = new SearchLocationResultAdapter(this, resultLocNameList, R.layout.search_location_rst_custom);
		searchLocListView.setAdapter(adapter);
		
		mHandler = new SearchLocationHandler();
		
		// list view의 각 아이템 클릭시 이벤트 지정
		searchLocListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if("naver".equals(listViewServiceDiv)) {
					// 카텍좌표계 -> 위도,경도 좌표
					String x = resultLocNameList.get(position).getMapX();
					String y = resultLocNameList.get(position).getMapY();
					
					// naver 검색결과 조회 시 좌표를 경도, 위도로 변경
					Message message = mHandler.obtainMessage(API_TRANS_COORD_DAUM);
					
					selectedTitleStr = resultLocNameList.get(position).getTitle();
					selectedDescStr = resultLocNameList.get(position).getDescription();
					
					OpenApiExecutor.transCoordByDaum(SearchLocationResultActivity.this, x, y, mHandler);
					
				} else {
					Toast.makeText(SearchLocationResultActivity.this, resultLocNameList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		
		
		// 처음 로딩시 구글 검색 시작~!
		Bundle bundle = getIntent().getExtras();
		searchLocName = bundle.getString("searchLocName");
		
		Toast.makeText(getBaseContext(), searchLocName, Toast.LENGTH_SHORT).show();
		
		mHandler.obtainMessage(API_LOC_SEARCH_GOOGLE);
		OpenApiExecutor.searchGoogleLocation(SearchLocationResultActivity.this, searchLocName, mHandler);
		
		// 취소 버튼
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent rtnIntent = new Intent();
				rtnIntent.putExtra("rtn", "cancel");
				
				setResult(RESULT_OK, rtnIntent);
				
				finish();
			}
			
		});
		
		searchLocNaverResultBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					listViewServiceDiv = "naver"; 
							
					mHandler.obtainMessage(API_LOC_SEARCH_NAVER);
					OpenApiExecutor.searchNaverLocation(SearchLocationResultActivity.this, searchLocName, mHandler);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		
		searchLocGoogleResultBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				listViewServiceDiv = "google";
				
				mHandler.obtainMessage(API_LOC_SEARCH_GOOGLE);
				OpenApiExecutor.searchGoogleLocation(SearchLocationResultActivity.this, searchLocName, mHandler);
			}
			
		});
	}
	
	public final static int API_TRANS_COORD_DAUM = 0;
	public final static int API_LOC_SEARCH_NAVER = 1;
	public final static int API_LOC_SEARCH_GOOGLE = 2;
	private SearchLocationHandler mHandler;
	class SearchLocationHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch(msg.what) {
			case API_TRANS_COORD_DAUM : 
				DaumTransCoordResult rstObj = msg.getData().getParcelable("transCoordDaumApiResult");
				
				Intent rtnIntent = new Intent();
				rtnIntent.putExtra("rtn", ApiResult.STATUS_SUCCESS);
				rtnIntent.putExtra("x", rstObj.getX());
				rtnIntent.putExtra("y", rstObj.getY());
				rtnIntent.putExtra("title", selectedTitleStr);
				rtnIntent.putExtra("desc", selectedDescStr);
				
				setResult(RESULT_OK, rtnIntent);
				
				finish();
				break;
			case API_LOC_SEARCH_NAVER : 
				ArrayList<SearchLocationResultAdapterModel> resultList = msg.getData().getParcelableArrayList("naverApiResult");
				resultLocNameList.clear();
				resultLocNameList.addAll(resultList);
				adapter.notifyDataSetChanged();
				
				searchLocListView.refreshDrawableState();
				break;
			case API_LOC_SEARCH_GOOGLE :
				ArrayList<SearchLocationResultAdapterModel> googleResultList = msg.getData().getParcelableArrayList("googleApiResult");
				resultLocNameList.clear();
				resultLocNameList.addAll(googleResultList);
				adapter.notifyDataSetChanged();
				
				searchLocListView.refreshDrawableState();
				break;
			}
		}
	}
}
