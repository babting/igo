package com.babting.igo.api.flickr;

import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.babting.igo.activity.AddLocationActivity;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.oauth.OAuthToken;

public class OAuthTask extends AsyncTask<Void, Integer, String> {

    private static final Uri OAUTH_CALLBACK_URI = Uri.parse(FlickrConstants.CALLBACK_SCHEME + "://oauth"); 

    /**
     * The context.
     */
    private Context mContext;

    /**
     * The progress dialog before going to the browser.
     */
    private ProgressDialog mProgressDialog;

    /**
     * Constructor.
     * 
     * @param context
     */
    public OAuthTask(Context context) {
            super();
            this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = ProgressDialog.show(mContext, "", "Generating the authorization request..."); 
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                    OAuthTask.this.cancel(true);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected String doInBackground(Void... params) {
        try {
            Flickr f = FlickrHelper.getInstance().getFlickr();
            OAuthToken oauthToken = f.getOAuthInterface().getRequestToken(OAUTH_CALLBACK_URI.toString());
            saveTokenSecrent(oauthToken.getOauthTokenSecret());
            URL oauthUrl = f.getOAuthInterface().buildAuthenticationUrl(Permission.DELETE, oauthToken);
            return oauthUrl.toString();
        } catch (Exception e) {
            return "error:" + e.getMessage(); //$NON-NLS-1$
        }
    }

    /**
     * Saves the oauth token secrent.
     * 
     * @param tokenSecret
     */
    private void saveTokenSecrent(String tokenSecret) {
    	AddLocationActivity act = (AddLocationActivity) mContext;
    	act.saveOAuthToken(null, null, null, tokenSecret);
    	//Log.d("igo flickr", tokenSecret);
    }

    @Override
    protected void onPostExecute(String result) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (result != null && !result.startsWith("error") ) {
        	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
        	intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            mContext.startActivity(intent);
        } else {
            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        }
    }

}