package com.babting.igo.activity;

import java.util.List;
import java.util.StringTokenizer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.babting.igo.R;
import com.babting.igo.api.OpenApiExecutor;
import com.babting.igo.api.result.ApiResult;
import com.babting.igo.api.result.SelectLocationApiResult;
import com.babting.igo.constants.MsgConstants;
import com.babting.igo.model.Categorys;
import com.babting.igo.model.LocationInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity {
	private GoogleMap map;
	private LocationManager locationManager;
	private String provider;
	private double currentLatitude;
	private double currentLongitude;
	private Button addButton;
	private Button searchButton;
	private EditText searchLocEditText;
	
	public static final int REQUEST_ADD_LOCATION_FRM = 0;
	public static final int REQUEST_SEARCH_LOCATION_LIST = 1;
	
	private String selectedTitleStr = "";
	private String selectedDescStr = "";
	
	private ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mHandler = new SearchLocationHandler();
		
    	map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
    	map.setMyLocationEnabled(true);
    	
    	map.setOnMyLocationChangeListener(onMyLocationChangeListener);
    	map.setOnMapClickListener(onMapClickListener);
    	map.setOnCameraChangeListener(onCameraChangeListener);
    	
    	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		provider = LocationManager.NETWORK_PROVIDER;
		
		addButton = (Button)findViewById(R.id.addPlaceBtn);
		addButton.setVisibility(View.INVISIBLE);
		
		// addButton 클릭시
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getBaseContext(), AddLocationActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("latitude", currentLatitude);
				intent.putExtra("longitude", currentLongitude);
				intent.putExtra("title", selectedTitleStr);
				intent.putExtra("desc", selectedDescStr);
				startActivityForResult(intent, REQUEST_ADD_LOCATION_FRM);
			}
			
		});
		
		searchButton = (Button)findViewById(R.id.searchLocBtn);
		searchLocEditText = (EditText)findViewById(R.id.searchLocEditText);
		
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getBaseContext(), SearchLocationResultActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("searchLocName", searchLocEditText.getText().toString());
				startActivityForResult(intent, REQUEST_SEARCH_LOCATION_LIST);
			}
			
		});
		
		mProgressDialog = ProgressDialog.show(this, "", "Searching current location..."); 
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setCancelable(true);
	}
	
	private SearchLocationHandler mHandler;
	class SearchLocationHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch(msg.what) {
			case MsgConstants.API_SEARCH_LOC : 
				SelectLocationApiResult rstObj = msg.getData().getParcelable("apiResult");
				
				List<LocationInfo> locationInfoList = rstObj.getLocationList();
				Toast.makeText(getBaseContext(), "registered location count : " + locationInfoList.size(), Toast.LENGTH_SHORT).show();
				
				if(locationInfoList.size() > 0) { // 현재 보이는 지도안에 등록된 지점이 있을 경우
					for(int i = 0 ; i < locationInfoList.size() ; i++) {
						LocationInfo locationInfo = locationInfoList.get(i);
						Marker registeredMarker = map.addMarker(new MarkerOptions().position(new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude())).title(locationInfo.getPlaceName()));
						
						int markerReserouce = R.drawable.marker_bread;
						if(locationInfo.getCategorys() != null && !locationInfo.getCategorys().equals("")) {
							StringTokenizer tokenizer = new StringTokenizer(locationInfo.getCategorys(), "|");
							if(tokenizer.hasMoreTokens()) {
								Categorys categorys = Categorys.getCategorysInfoFromCode(tokenizer.nextToken());
								markerReserouce = categorys.getMarkerImgId();
							}
						}
						registeredMarker.setIcon(BitmapDescriptorFactory.fromResource(markerReserouce));
					}
				}
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == REQUEST_ADD_LOCATION_FRM) {
			if(data != null) {
				Bundle extras = data.getExtras();
				
				// currentLatitude, currentLongitude에 마지막 설정된 위치가 저장되어 있을 것이기 때문에 위치 등록 성공 시 marker를 새로 생성해 고정시킴.
				Marker registeredMarker = map.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title(extras.getString("title")));
				
				String[] selectedCategorys = extras.getStringArray("categorys");
				int markerReserouce = R.drawable.marker_bread;
				if(selectedCategorys != null && selectedCategorys.length > 0) {
					Categorys categorys = Categorys.getCategorysInfoFromCode(selectedCategorys[0]);
					markerReserouce = categorys.getMarkerImgId();
				}
				
				registeredMarker.setIcon(BitmapDescriptorFactory.fromResource(markerReserouce));
				registeredMarker.showInfoWindow(); // 말풍선 show
				
				addButton.setVisibility(View.INVISIBLE);
			} else {
				Toast.makeText(getBaseContext(), "cancel~!", Toast.LENGTH_SHORT).show();
			}
		} else if(requestCode == REQUEST_SEARCH_LOCATION_LIST) {
			if(data != null) {
				Bundle extras = data.getExtras();
				Toast.makeText(getBaseContext(), extras.getString("rtn"), Toast.LENGTH_SHORT).show();
				
				if(ApiResult.STATUS_SUCCESS.equals(extras.getString("rtn"))) {
					map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(extras.getDouble("y"), extras.getDouble("x"))));
					Toast.makeText(getBaseContext(), extras.getDouble("y") + " / " + extras.getDouble("x"), Toast.LENGTH_SHORT).show();
					
					Log.d("igo", extras.getDouble("y") + " / " + extras.getDouble("x"));
					
					this.moveCurrentMarker(extras.getDouble("y"), extras.getDouble("x"));
					currentMarker.setTitle(extras.getString("title"));
					currentMarker.showInfoWindow();
					
					selectedTitleStr = extras.getString("title");
					selectedDescStr = extras.getString("desc");
					
					addButton.setVisibility(View.VISIBLE);
				}
			} else {
				Toast.makeText(getBaseContext(), "cancel~!", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**
	 * 현재 Marker의 위치를 변경
	 * @param x
	 * @param y
	 */
	private void moveCurrentMarker(double y, double x) {
		if(currentMarker != null) {
			currentMarker.setPosition(new LatLng(y, x));
		} else {
			currentMarker = map.addMarker(new MarkerOptions().position(new LatLng(y, x)));
		}
		
		currentMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_new_loc));
		currentMarker.setTitle("새로운 장소로 등록해 주세요~!");
		currentMarker.showInfoWindow();
		currentLatitude = y;
		currentLongitude = x;
	}
	
	private OnMyLocationChangeListener onMyLocationChangeListener = new OnMyLocationChangeListener() {

		@Override
		public void onMyLocationChange(Location location) {
			Toast.makeText(getBaseContext(), "1. " + location.getLatitude() + " / " + location.getLongitude(), Toast.LENGTH_SHORT).show();
			currentLatitude = location.getLatitude();
			currentLongitude = location.getLongitude();
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
			Log.d("igo", location.getLatitude() + " / " + location.getLongitude());
			
			if (mProgressDialog != null) {
	            mProgressDialog.dismiss();
	        }
			
			map.setOnMyLocationChangeListener(null); // 현재 위치로 이동 후 listener 삭제
			
			
			// 현재 보여지는 지도에 해당하는 등록되어 있는 정보들을 select
	    	double top = map.getProjection().getVisibleRegion().latLngBounds.northeast.latitude;
	    	double right = map.getProjection().getVisibleRegion().latLngBounds.northeast.longitude;
	    	double bottom = map.getProjection().getVisibleRegion().latLngBounds.southwest.latitude;
	    	double left = map.getProjection().getVisibleRegion().latLngBounds.southwest.longitude;
	    	mHandler.obtainMessage(MsgConstants.API_SEARCH_LOC);
	    	OpenApiExecutor.searchLocInfoList(MainActivity.this, bottom, top, left, right, mHandler);
		}

	}; 
	
	private OnCameraChangeListener onCameraChangeListener = new OnCameraChangeListener() {

		@Override
		public void onCameraChange(CameraPosition position) {
			// get current location of camera
			double top = map.getProjection().getVisibleRegion().latLngBounds.northeast.latitude;
	    	double right = map.getProjection().getVisibleRegion().latLngBounds.northeast.longitude;
	    	double bottom = map.getProjection().getVisibleRegion().latLngBounds.southwest.latitude;
	    	double left = map.getProjection().getVisibleRegion().latLngBounds.southwest.longitude;
	    	
	    	// DB에서 현재 위치에 해당하는 장소들 검색
	    	mHandler.obtainMessage(MsgConstants.API_SEARCH_LOC);
	    	OpenApiExecutor.searchLocInfoList(MainActivity.this, bottom, top, left, right, mHandler);
		}
		
	};
	
	private Marker currentMarker;
	private OnMapClickListener onMapClickListener = new OnMapClickListener() {

		@Override
		public void onMapClick(LatLng point) {
			moveCurrentMarker(point.latitude, point.longitude);
			
			currentLatitude = point.latitude;
			currentLongitude = point.longitude;
			
			addButton.setVisibility(View.VISIBLE);
		}
		
	};
	
	public void onResume() {
		super.onResume();
		Toast.makeText(getBaseContext(), "Resume", Toast.LENGTH_SHORT).show();
		
		if(map.getMyLocation() != null) {
			Toast.makeText(getBaseContext(), "not null~!!", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getBaseContext(), "null~!!", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void onPause() {
		super.onPause();
		Toast.makeText(getBaseContext(), "Pause", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
