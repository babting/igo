package com.babting.igo.api.flickr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.AsyncTask;

import com.babting.igo.activity.FlickrActivity;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.PhotoList;

public class LoadPhotosTask extends AsyncTask<OAuth, Void, PhotoList> {
    /**
     * 
     */
    private final FlickrActivity flickrjAndroidSampleActivity;
    private final Logger logger = LoggerFactory.getLogger(LoadPhotosTask.class);
    
    public LoadPhotosTask(FlickrActivity flickrjAndroidSampleActivity) {
        this.flickrjAndroidSampleActivity = flickrjAndroidSampleActivity;
    }
    
    /* (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected PhotoList doInBackground(OAuth... params) {
        OAuth oauth = params[0];
        User user = oauth.getUser();
        OAuthToken token = oauth.getToken();
        try {
            Flickr f = FlickrHelper.getInstance().getFlickrAuthed(token.getOauthToken(), token.getOauthTokenSecret());
            
            return f.getPeopleInterface().getPhotos(user.getId(), null, 10, 1);
        } catch (Exception e) {
            flickrjAndroidSampleActivity.removeOAuthToken();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(PhotoList photoList) {
        if (photoList == null) {
            return;
        } else {
        	//flickrjAndroidSampleActivity.setPhotoList(photoList);
        }
    }
    
    
}

