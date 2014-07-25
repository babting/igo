package com.babting.igo.activity;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.babting.igo.R;
import com.babting.igo.api.OpenApiExecutor;
import com.babting.igo.api.flickr.FlickrConstants;
import com.babting.igo.api.flickr.FlickrOAuthUtil;
import com.babting.igo.api.flickr.GetOAuthTokenTask;
import com.babting.igo.api.flickr.OAuthTask;
import com.babting.igo.api.flickr.UploadPhotoTask;
import com.babting.igo.api.result.RegistLocationApiResult;
import com.babting.igo.constants.MsgConstants;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;

public class FlickrActivity extends Activity {
	private String[] selectedCategoryStrList; // 선택된 카테고리 list
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.flickr);
		
		mHandler = new AddLocationHandler();
		
		Bundle bundle = getIntent().getExtras();
		
		selectedCategoryStrList = bundle.getStringArray("selectedCategoryStrList");
		
		mHandler.obtainMessage(MsgConstants.API_REGIST_LOC);
		OpenApiExecutor.registLocInfo(
				FlickrActivity.this, 
				bundle.getDouble("latitude"), 
				bundle.getDouble("longitude"), 
				bundle.getString("title"), 
				bundle.getString("desc"), 
				bundle.getString("comment"), 
				selectedCategoryStrList,
				mHandler);
		
		String uploadImgUri = bundle.getString("uploadImgUri");
		
		// String picturePath contains the path of selected Image
		if(uploadImgUri != null && !"".equals(uploadImgUri)) {
			OAuth oauth = FlickrOAuthUtil.getOAuthToken(this);
			if (oauth == null || oauth.getUser() == null) {
				loadAuthTask();
			} else {
				load(oauth);
			}
			
			//UploadPhotoTask task = new UploadPhotoTask(FlickrActivity.this, uploadImgUri);
			//task.execute(oauth);
		}
	}
	
	public void loadAuthTask() {
		OAuthTask task = new OAuthTask(FlickrActivity.this);
		task.execute();
	}
	
	/*
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Intent intent = getIntent();
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
	*/
	
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
	
	/*
	public void setPhotoList(PhotoList photoList) {
		Toast.makeText(this, "Flickr photo size : " + photoList.size(), Toast.LENGTH_LONG).show();
		
		for(Photo photo : photoList) {
			ImageView imgView = new ImageView(this);
			imgView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	        ImageDownloadTask task = new ImageDownloadTask(imgView);
	        Drawable drawable = new ImageUtils.DownloadedDrawable(task);
	        imgView.setImageDrawable(drawable);
	        task.execute(photo.getSmallUrl());
	        
	        this.linearLayout.addView(imgView);
	        
	        setContentView(this.linearLayout);
		}
	}
	*/
	
	private AddLocationHandler mHandler;
	class AddLocationHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch(msg.what) {
			case MsgConstants.API_REGIST_LOC : 
				RegistLocationApiResult rstObj = msg.getData().getParcelable("apiResult");
				
				Intent rtnIntent = new Intent();
				rtnIntent.putExtra("rtn", rstObj.getResultCode());
				rtnIntent.putExtra("title", rstObj.getTitle());
				
				StringBuffer categoryStrSb = new StringBuffer();
				
				for(int i = 0 ; i < selectedCategoryStrList.length ; i++) {
					categoryStrSb.append(selectedCategoryStrList[i]);
					if(i < selectedCategoryStrList.length - 1) categoryStrSb.append("|");
				}
				
				rtnIntent.putExtra("categorys", categoryStrSb.toString());
				
				setResult(RESULT_OK, rtnIntent);
				
				finish();
				break;
			}
		}
	}
}
