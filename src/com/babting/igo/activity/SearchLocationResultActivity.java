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
import com.babting.igo.xml.model.SearchLocationResultAdapterModel;

public class SearchLocationResultActivity extends Activity {
	private Button cancelBtn;
	private Button searchLocNaverResultBtn;
	private Button searchLocGoogleResultBtn;
	
	private ListView searchLocListView;
	
	private String searchLocName;
	
	private SearchLocationResultAdapter adapter;
	List<SearchLocationResultAdapterModel> resultLocNameList = new ArrayList<SearchLocationResultAdapterModel>(); 
	
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
		
		searchLocListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(SearchLocationResultActivity.this, resultLocNameList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
			}
			
		});
		
		
		Bundle bundle = getIntent().getExtras();
		searchLocName = bundle.getString("searchLocName");
		
		Toast.makeText(getBaseContext(), searchLocName, Toast.LENGTH_SHORT).show();
		
		OpenApiExecutor.searchGoogleLocation(SearchLocationResultActivity.this, searchLocName, new Handler() {
			
			public void handleMessage(Message msg){
				ArrayList<SearchLocationResultAdapterModel> resultList = msg.getData().getParcelableArrayList("googleApiResult");
				resultLocNameList.clear();
				resultLocNameList.addAll(resultList);
				adapter.notifyDataSetChanged();
				
				searchLocListView.refreshDrawableState();
		    }
			
		});
		
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
					OpenApiExecutor.searchNaverLocation(SearchLocationResultActivity.this, searchLocName, new Handler() {
						
						public void handleMessage(Message msg){
							ArrayList<SearchLocationResultAdapterModel> resultList = msg.getData().getParcelableArrayList("naverApiResult");
							resultLocNameList.clear();
							resultLocNameList.addAll(resultList);
							adapter.notifyDataSetChanged();
							
							searchLocListView.refreshDrawableState();
					    }
						
					});
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		
		searchLocGoogleResultBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				OpenApiExecutor.searchGoogleLocation(SearchLocationResultActivity.this, searchLocName, new Handler() {
					
					public void handleMessage(Message msg){
						ArrayList<SearchLocationResultAdapterModel> resultList = msg.getData().getParcelableArrayList("googleApiResult");
						resultLocNameList.clear();
						resultLocNameList.addAll(resultList);
						adapter.notifyDataSetChanged();
						
						searchLocListView.refreshDrawableState();
				    }
					
				});
			}
			
		});
	}
	
	
}
