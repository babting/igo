package com.babting.igo.api.flickr;

import android.content.Context;
import android.content.SharedPreferences;

import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;

public class FlickrOAuthUtil {
	public static OAuth getOAuthToken(Context context) {
		//Restore preferences
		SharedPreferences settings = context.getSharedPreferences(FlickrConstants.PREFS_NAME, Context.MODE_PRIVATE);
		String oauthTokenString = settings.getString(FlickrConstants.KEY_OAUTH_TOKEN, null);
		String tokenSecret = settings.getString(FlickrConstants.KEY_TOKEN_SECRET, null);
   
		if (oauthTokenString == null && tokenSecret == null) {
			return null;
		}
   
		OAuth oauth = new OAuth();
		String userName = settings.getString(FlickrConstants.KEY_USER_NAME, null);
		String userId = settings.getString(FlickrConstants.KEY_USER_ID, null);
   
		if (userId != null) {
			User user = new User();
			user.setUsername(userName);
			user.setId(userId);
			oauth.setUser(user);
		}
		
		OAuthToken oauthToken = new OAuthToken();
		oauth.setToken(oauthToken);
		oauthToken.setOauthToken(oauthTokenString);
		oauthToken.setOauthTokenSecret(tokenSecret);
		return oauth;
   }
}
