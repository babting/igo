package com.babting.igo.api;

public class DefaultConstants {
	public enum HttpMethod {
		HTTP_GET, HTTP_POST, HTTP_DELETE;
	}
	
	public static final String HTTP_SOCKET_TIME_OUT_PARAM = "http.socket.timeout";

	/** HTTP session connection time-out */
	public static final String HTTP_CONNECTION_TIME_OUT_PARAM = "http.connection.timeout";
	
	/** HTTP session timeout. */
	public static final Integer HTTP_TIME_OUT_SOCKET = 10000; //

	public static final Integer HTTP_TIME_OUT_CONNECTION = 10000; //
}
