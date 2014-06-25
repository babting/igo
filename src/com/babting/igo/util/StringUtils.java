package com.babting.igo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;

import android.util.Log;

public class StringUtils {
	private static final String TAG = "StringUtils";
	
	/**
	 * InputStream의 내용을 String으로 변환해 줌.
	 * @param is
	 * @return string
	 */
	public static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		try {
			String line = reader.readLine();
			while (line != null) {
				sb.append(line + "\n");
				line = reader.readLine();
			}
		} catch (SocketTimeoutException ste) {
			Log.w(TAG, ste.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			try {
				is.close();
			} catch (SocketTimeoutException ste) {
				Log.w(TAG, ste.getMessage());
			} catch (IOException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		return sb.toString();
	}
}
