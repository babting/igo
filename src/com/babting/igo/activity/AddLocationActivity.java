package com.babting.igo.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.babting.igo.R;
import com.babting.igo.api.OpenApiExecutor;
import com.babting.igo.api.flickr.FlickrConstants;
import com.babting.igo.api.flickr.FlickrOAuthUtil;
import com.babting.igo.api.flickr.GetOAuthTokenTask;
import com.babting.igo.api.flickr.OAuthTask;
import com.babting.igo.api.flickr.UploadPhotoTask;
import com.babting.igo.api.result.ApiResult;
import com.babting.igo.api.result.RegistLocationApiResult;
import com.babting.igo.constants.MsgConstants;
import com.babting.igo.model.Categorys;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;

public class AddLocationActivity extends FragmentActivity {
	private static final String SP_PARAMS_BACKUP_KEY = "SP_ADDLOCATION_PARAMS_BACKUP";
	private static final int REQUEST_REG_LOC = 0;
	private static final int RESULT_LOAD_IMAGE = 1;
	
	private Button cancelBtn;
	private Button registBtn;
	private Button imgSearchBtn;
	
	private GoogleMap map;
	private EditText titleEditText;
	private EditText descEditText;
	private EditText commentEditText;
	
	private Double latitude = 0.0;
	private Double longitude = 0.0;
	private String title = "";
	private String desc = "";
	private String comment = "";
	
	private ImageView uploadImgPreViewImageView;
	private String uploadImgUri;
	
	private List<String> selectedCategoryStrList; // 선택된 카테고리 list
	
	private static boolean isUploadPhotoComplete = true;
	private static boolean isRegLocComplete = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.add_location_form);
		
		cancelBtn = (Button) findViewById(R.id.backBtn);
		registBtn = (Button) findViewById(R.id.registBtn);
		imgSearchBtn = (Button) findViewById(R.id.imgSearchBtn);
		
		titleEditText = (EditText) findViewById(R.id.addLocFormTitleEditText);
		descEditText = (EditText) findViewById(R.id.addLocFormDescEditText);
		commentEditText = (EditText) findViewById(R.id.addLocFormCommentEditText);
		
		uploadImgPreViewImageView = (ImageView) findViewById(R.id.uploadImgPreViewImageView);
		
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.addLocFormPreviewMap)).getMap();
		
		selectedCategoryStrList = new ArrayList<String>();
		
		Bundle bundle = getIntent().getExtras();
		
		if(bundle.get("latitude") == null || bundle.get("longitude") == null) {
			SharedPreferences settings = getSharedPreferences(SP_PARAMS_BACKUP_KEY, Context.MODE_PRIVATE);
			latitude = Double.valueOf(settings.getString("latitude", "0.0"));
			longitude = Double.valueOf(settings.getString("longitude", "0.0"));
			title = settings.getString("title", "");
			desc = settings.getString("desc", "");
			
			Editor editor = settings.edit();
			editor.remove("latitude");
			editor.remove("longitude");
			editor.remove("title");
			editor.remove("desc");
			editor.commit();
		} else {
			latitude = (Double) bundle.get("latitude");
			longitude = (Double) bundle.get("longitude");
			title = bundle.getString("title");
			desc = bundle.getString("desc");
		}
		
		titleEditText.setText(title);
		descEditText.setText(desc);
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 18));
		map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
		
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent rtnIntent = new Intent();
				rtnIntent.putExtra("rtn", "ok");
				
				setResult(RESULT_OK, rtnIntent);
				
				finish();
			}
			
		});
		
		registBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getBaseContext(), FlickrActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				
				intent.putExtra("latitude", latitude);
				intent.putExtra("longitude", longitude);
				intent.putExtra("title", titleEditText.getText().toString());
				intent.putExtra("desc", descEditText.getText().toString());
				intent.putExtra("comment", commentEditText.getText().toString());
				String[] categoryArr = new String[selectedCategoryStrList.size()];
				intent.putExtra("selectedCategoryStrList", selectedCategoryStrList.toArray(categoryArr));
				intent.putExtra("uploadImgUri", uploadImgUri);
				startActivityForResult(intent, REQUEST_REG_LOC);
			}
			
		});
		
		imgSearchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				OAuth oauth = FlickrOAuthUtil.getOAuthToken(AddLocationActivity.this);
				if (oauth == null || oauth.getUser() == null) {
					loadAuthTask();
				} else {
					load(oauth);
					
					Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					 
					startActivityForResult(i, RESULT_LOAD_IMAGE);
				}
			}
			
		});
		
		Integer[] categoryImgBtnIds = Categorys.getIds();
		for(final Integer id : categoryImgBtnIds) {
			ImageButton imgBtn = (ImageButton) findViewById(id);
			// 카테고리 이미지 버튼 클릭
			imgBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Categorys selectedCategorys = Categorys.getCategorysInfo(id);
					
					if(!selectedCategoryStrList.contains(selectedCategorys.getCode())) {
						v.setBackgroundColor(Color.CYAN);
						selectedCategoryStrList.add(selectedCategorys.getCode());
					} else {
						v.setBackgroundColor(Color.TRANSPARENT);
						selectedCategoryStrList.remove(selectedCategorys.getCode());
					}
				}
				
			});
		}
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		SharedPreferences settings = getSharedPreferences(SP_PARAMS_BACKUP_KEY, Context.MODE_PRIVATE);
		Editor editor = settings.edit();
		
		editor.putString("latitude", Double.toString(latitude));
		editor.putString("longitude", Double.toString(longitude));
		editor.putString("title", title);
		editor.putString("desc", desc);
		
		editor.commit();
	}
	
	public void loadAuthTask() {
		OAuthTask task = new OAuthTask(AddLocationActivity.this);
		task.execute();
	}
	
	public void onOAuthDone(OAuth result) {
		if (result == null) {
			Toast.makeText(this, "Authorization failed", Toast.LENGTH_LONG).show();
		} else {
			User user = result.getUser();
			OAuthToken token = result.getToken();
			if (user == null || user.getId() == null || token == null
					|| token.getOauthToken() == null
					|| token.getOauthTokenSecret() == null) {
				Toast.makeText(this, "Authorization failed", Toast.LENGTH_LONG).show();
				return;
			}
			String message = String.format(Locale.US, "Authorization Succeed: user=%s, userId=%s, oauthToken=%s, tokenSecret=%s", 
					user.getUsername(), user.getId(), token.getOauthToken(), token.getOauthTokenSecret());
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			saveOAuthToken(user.getUsername(), user.getId(), token.getOauthToken(), token.getOauthTokenSecret());
			
			load(result);
		}
	}
	
	private void load(OAuth oauth) {
		if (oauth != null) {
			//new LoadUserTask(this, userIcon).execute(oauth);
			//new LoadPhotosTask(this).execute(oauth);
		}
	}
	
	public void removeOAuthToken() {
		SharedPreferences settings = getSharedPreferences(FlickrConstants.PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = settings.edit();
		
		editor.remove(FlickrConstants.KEY_OAUTH_TOKEN);
		editor.remove(FlickrConstants.KEY_TOKEN_SECRET);
		editor.commit();
	}
	
	public void saveOAuthToken(String userName, String userId, String token, String tokenSecret) {
    	SharedPreferences sp = getSharedPreferences(FlickrConstants.PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(FlickrConstants.KEY_OAUTH_TOKEN, token);
		editor.putString(FlickrConstants.KEY_TOKEN_SECRET, tokenSecret);
		editor.putString(FlickrConstants.KEY_USER_NAME, userName);
		editor.putString(FlickrConstants.KEY_USER_ID, userId);
		editor.commit();
    }
	
	public void setUser(User user) {
		Toast.makeText(this, "Flickr : " + user.getUsername() + " / " + user.getRealName() + " / " + user.getId(), Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
			      
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
				 
			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
				 
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			
			Toast.makeText(this, picturePath, Toast.LENGTH_SHORT).show();
			
			Bitmap bitmapOrg = BitmapFactory.decodeFile(picturePath);
			uploadImgPreViewImageView.setImageBitmap(bitmapOrg);
			
			uploadImgUri = picturePath;
			
			synchronized (this) {
				isUploadPhotoComplete = false;	
			}
		} else if (requestCode == REQUEST_REG_LOC && resultCode == RESULT_OK && null != data) {
			Intent rtnIntent = new Intent();
			rtnIntent.putExtra("title", titleEditText.getText().toString());
			String[] categoryArr = new String[selectedCategoryStrList.size()];
			rtnIntent.putExtra("categorys", selectedCategoryStrList.toArray(categoryArr));
			
			setResult(RESULT_OK, rtnIntent);
			
			synchronized (this) {
				isRegLocComplete = true;
			}
			
			Toast.makeText(AddLocationActivity.this, "Registering location is success~!", Toast.LENGTH_SHORT).show();
			
			// String picturePath contains the path of selected Image
			UploadPhotoTask task = new UploadPhotoTask(this, uploadImgUri);
			OAuth oauth = FlickrOAuthUtil.getOAuthToken(this);
			task.execute(oauth);
			
			if(isRegLocComplete && isUploadPhotoComplete) {
				finish();
			}
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		String scheme = intent.getScheme();
		OAuth savedToken = FlickrOAuthUtil.getOAuthToken(this);
		
		// Flickr 인증완료 후 다시 앱으로 돌아오면 이곳이 실행됨.
		if(FlickrConstants.CALLBACK_SCHEME.equals(scheme) && (savedToken == null || savedToken.getUser() == null)) {
			Uri uri = intent.getData();
			String query = uri.getQuery();
			String[] data = query.split("&");
			if (data != null && data.length == 2) {
				String oauthToken = data[0].substring(data[0].indexOf("=") + 1);
				String oauthVerifier = data[1].substring(data[1].indexOf("=") + 1);

				OAuth oauth = FlickrOAuthUtil.getOAuthToken(this);
				if (oauth != null && oauth.getToken() != null && oauth.getToken().getOauthTokenSecret() != null) {
					GetOAuthTokenTask task = new GetOAuthTokenTask(this);
					task.execute(oauthToken, oauth.getToken().getOauthTokenSecret(), oauthVerifier);
				}
			}
		}
	}

	public synchronized void uploadPhotoComplete() {
		isUploadPhotoComplete = true;
		
		if(isRegLocComplete && isUploadPhotoComplete) {
			finish();
		}
	}
}
