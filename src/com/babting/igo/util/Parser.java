package com.babting.igo.util;

import java.io.InputStream;

import com.babting.igo.exception.ParserException;

public interface Parser {
	/**
	 * json content�� valueType�� deserialization �Ѵ�.
	 * @param jsonContent
	 * @param valueType
	 * @return
	 * @throws ParserException
	 */
	public <T> T readValue(String jsonContent,  Class<T> valueType) throws ParserException;
	public <T> T readValue(InputStream jsonContent,  Class<T> valueType) throws ParserException;
	/**
	 * object�� string���� serialization �Ѵ�.
	 * @param value
	 * @return
	 * @throws ParserException
	 */
	public String writeValueAsString(Object value) throws ParserException;
}
