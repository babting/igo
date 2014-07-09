package com.babting.igo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.babting.igo.R;
import com.babting.igo.api.OpenApiExecutor;
import com.babting.igo.api.result.ApiResult;
import com.babting.igo.api.result.RegistLocationApiResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddLocationActivity extends FragmentActivity {
	private Button cancelBtn;
	private Button registBtn;
	private GoogleMap map;
	private EditText titleEditText;
	private EditText descEditText;
	
	private Double latitude;
	private Double longitude;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.add_location_form);
		
		cancelBtn = (Button) findViewById(R.id.backBtn);
		registBtn = (Button) findViewById(R.id.registBtn);
		titleEditText = (EditText) findViewById(R.id.addLocFormTitleEditText);
		descEditText = (EditText) findViewById(R.id.addLocFormDescEditText);
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.addLocFormPreviewMap)).getMap();
		
		mHandler = new AddLocationHandler();
		
		Bundle bundle = getIntent().getExtras();
		latitude = (Double) bundle.get("latitude");
		longitude = (Double) bundle.get("longitude");
		
		titleEditText.setText((String)bundle.get("title"));
		descEditText.setText((String)bundle.get("desc"));
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 18));
		map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
		
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent rtnIntent = new Intent();
				rtnIntent.putExtra("rtn", "ok");
				
				setResult(RESULT_OK, rtnIntent);
				
				finish();
			}
			
		});
		
		registBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mHandler.obtainMessage(API_REGIST_LOC);
				OpenApiExecutor.registLocInfo(AddLocationActivity.this, latitude, longitude, titleEditText.getText().toString(), descEditText.getText().toString(), mHandler);
			}
			
		});
	}
	
	public final static int API_REGIST_LOC = 1;
	private AddLocationHandler mHandler;
	class AddLocationHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch(msg.what) {
			case API_REGIST_LOC : 
				RegistLocationApiResult rstObj = msg.getData().getParcelable("apiResult");
				
				Intent rtnIntent = new Intent();
				rtnIntent.putExtra("rtn", rstObj.getResultCode());
				rtnIntent.putExtra("title", rstObj.getTitle());
				
				setResult(RESULT_OK, rtnIntent);
				
				if(rstObj.getResultCode() == ApiResult.STATUS_SUCCESS) {
					Toast.makeText(AddLocationActivity.this, "Registering location is success~!", Toast.LENGTH_SHORT).show();
				}
				
				finish();
				break;
			}
		}
	}
}
