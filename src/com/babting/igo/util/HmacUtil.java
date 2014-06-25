package com.babting.igo.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;

public class HmacUtil {
	private static final String TAG = "HmacUtil";

	private static final String HMAC_ALGORITHM = "HmacSHA1";
	private static final char[] INT_TO_BASE64 = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

	/**
	 * 보안키로 해시값을 생성한다.
	 * 
	 * @param data
	 * @param key
	 * @return
	 */
	public static String generateHash(String data, String key) {
		String hashValue = null;
		byte[] rawHmac = null;

		try {
			// get an hmac_sha1 key from the raw key bytes
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_ALGORITHM);

			// get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(HMAC_ALGORITHM);
			mac.init(signingKey);

			// compute the hmac on input data bytes
			synchronized (mac) {
				rawHmac = mac.doFinal(data.getBytes());
			}
		} catch (Exception e) {
			Log.e(TAG, "Failed to generate HMAC : " + e.getMessage());
		} finally {
			hashValue = encodeBase64(rawHmac);
		}

		return hashValue;
	}
	
	public static String encodeBase64(byte[] src) {
		int aLen = src.length;
		int numFullGroups = aLen / 3;
		int numBytesInPartialGroup = aLen - (3 * numFullGroups);
		int resultLen = 4 * (aLen + 2) / 3;
		StringBuffer result = new StringBuffer(resultLen);
		char[] intToAlpha = INT_TO_BASE64;

		int inCursor = 0;
		for (int i = 0; i < numFullGroups; ++i) {
			int byte0 = src[(inCursor++)] & 0xFF;
			int byte1 = src[(inCursor++)] & 0xFF;
			int byte2 = src[(inCursor++)] & 0xFF;
			result.append(intToAlpha[(byte0 >> 2)]);
			result.append(intToAlpha[(byte0 << 4 & 0x3F | byte1 >> 4)]);
			result.append(intToAlpha[(byte1 << 2 & 0x3F | byte2 >> 6)]);
			result.append(intToAlpha[(byte2 & 0x3F)]);
		}

		if (numBytesInPartialGroup != 0) {
			int byte0 = src[(inCursor++)] & 0xFF;
			result.append(intToAlpha[(byte0 >> 2)]);
			if (numBytesInPartialGroup == 1) {
				result.append(intToAlpha[(byte0 << 4 & 0x3F)]);
				result.append("==");
			} else {
				int byte1 = src[(inCursor++)] & 0xFF;
				result.append(intToAlpha[(byte0 << 4 & 0x3F | byte1 >> 4)]);
				result.append(intToAlpha[(byte1 << 2 & 0x3F)]);
				result.append('=');
			}

		}

		return result.toString();
	}
}
