package com.babting.igo.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.babting.igo.R;
import com.babting.igo.api.result.ApiResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
    	map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
    	map.setMyLocationEnabled(true);
    	
    	map.setOnMyLocationChangeListener(onMyLocationChangeListener);
    	map.setOnMapClickListener(onMapClickListener);
    	
    	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		provider = LocationManager.NETWORK_PROVIDER;
		
		
		addButton = (Button)findViewById(R.id.addPlaceBtn);
		addButton.setVisibility(View.INVISIBLE);
		
		// addButton 클릭시
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getBaseContext(), AddLocationActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == REQUEST_ADD_LOCATION_FRM) {
			if(data != null) {
				Bundle extras = data.getExtras();
				if(ApiResult.STATUS_SUCCESS.equals(extras.getString("rtn"))) {
					// currentLatitude, currentLongitude에 마지막 설정된 위치가 저장되어 있을 것이기 때문에 위치 등록 성공 시 marker를 새로 생성해 고정시킴.
					Marker registeredMarker = map.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title(extras.getString("title")));
					registeredMarker.showInfoWindow(); // 말풍선 show
					
					addButton.setVisibility(View.INVISIBLE);
				}
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
			map.setOnMyLocationChangeListener(null); // 현재 위치로 이동 후 listener 삭제
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
