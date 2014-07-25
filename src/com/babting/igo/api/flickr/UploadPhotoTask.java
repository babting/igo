package com.babting.igo.api.flickr;

import java.io.ByteArrayOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.babting.igo.activity.AddLocationActivity;
import com.babting.igo.activity.FlickrActivity;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;
import com.googlecode.flickrjandroid.uploader.Uploader;

public class UploadPhotoTask extends AsyncTask<OAuth, Void, User> {
    /**
     * 
     */
    private final AddLocationActivity flickrjAndroidSampleActivity;
    private String imgUri;
    private final Logger logger = LoggerFactory.getLogger(UploadPhotoTask.class);
    
    public UploadPhotoTask(AddLocationActivity flickrjAndroidSampleActivity, String imgUri) {
        this.flickrjAndroidSampleActivity = flickrjAndroidSampleActivity;
        this.imgUri = imgUri;
    }
    
    /**
     * The progress dialog before going to the browser.
     */
    private ProgressDialog mProgressDialog;
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = ProgressDialog.show(flickrjAndroidSampleActivity, "", "Uploading photo..."); 
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                UploadPhotoTask.this.cancel(true);
            }
        });
    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected User doInBackground(OAuth... params) {
        OAuth oauth = params[0];
        User user = oauth.getUser();
        OAuthToken token = oauth.getToken();
        try {
            Flickr f = FlickrHelper.getInstance().getFlickrAuthed(token.getOauthToken(), token.getOauthTokenSecret());
            
            Uploader uploader = f.getUploader();
            
            Bitmap bitmapOrg = BitmapFactory.decodeFile(imgUri);
            
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 90, bao);
            
            byte[] ba = bao.toByteArray();
            
            UploadMetaData metaData = new UploadMetaData();
            metaData.setTitle("tetst");
            metaData.setPublicFlag(true);
            metaData.setFamilyFlag(false);
            metaData.setFriendFlag(false);
            metaData.setContentType(Flickr.CONTENTTYPE_PHOTO);
            metaData.setSafetyLevel(Flickr.SAFETYLEVEL_SAFE);
            
            uploader.upload(imgUri, ba, metaData);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(User user) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        
        flickrjAndroidSampleActivity.uploadPhotoComplete();
    }
}

