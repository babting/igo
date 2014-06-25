package com.babting.igo.activity;

import com.babting.igo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AddLocationActivity extends Activity {
	private TextView txtView;
	private Button btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.add_location_form);
		
		txtView = (TextView) findViewById(R.id.txtMsg);
		btn = (Button) findViewById(R.id.backBtn);
		
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent rtnIntent = new Intent();
				rtnIntent.putExtra("rtn", "ok");
				
				setResult(RESULT_OK, rtnIntent);
				
				finish();
			}
			
		});
		
		Bundle bundle = getIntent().getExtras();
		String latitude = Double.toString((double) bundle.get("latitude"));
		
		txtView.setText(latitude);
	}
}
