package com.babting.igo.api.flickr;

import javax.xml.parsers.ParserConfigurationException;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.interestingness.InterestingnessInterface;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.photos.PhotosInterface;

public final class FlickrHelper {

    private static FlickrHelper instance = null;
    private static final String API_KEY = "49bcc5914e10fde0b132230bb5c2142b";
    public static final String API_SEC = "6f84861f713571d2";

    private FlickrHelper() {

    }

    public static FlickrHelper getInstance() {
        if (instance == null) {
            instance = new FlickrHelper();
        }

        return instance;
    }

    public Flickr getFlickr() {
    	try {
    		return this.getFlickr(new REST());
    	} catch (ParserConfigurationException e) {
	        return null;
		}
    }
    
    public Flickr getFlickr(REST rest) {
        Flickr f = new Flickr(API_KEY, API_SEC, rest);
        return f;
    }

    public Flickr getFlickrAuthed(String token, String secret) {
    	Flickr f = getFlickr();
        RequestContext requestContext = RequestContext.getRequestContext();
        OAuth auth = new OAuth();
        auth.setToken(new OAuthToken(token, secret));
        requestContext.setOAuth(auth);
        return f;
    }

    public InterestingnessInterface getInterestingInterface() {
        Flickr f = getFlickr();
        if (f != null) {
            return f.getInterestingnessInterface();
        } else {
            return null;
        }
    }

    public PhotosInterface getPhotosInterface() {
        Flickr f = getFlickr();
        if (f != null) {
            return f.getPhotosInterface();
        } else {
            return null;
        }
    }
}