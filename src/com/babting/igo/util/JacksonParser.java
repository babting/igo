package com.babting.igo.util;

import java.io.InputStream;

import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.babting.igo.exception.ParserException;

public class JacksonParser implements Parser {
	protected ObjectMapper mapper;
	
	public static final JacksonParser instance = new JacksonParser();

	/**
	 * 이미 기존에 생성되어 있는 인스턴스를 리턴한다. for singleton
	 * @return JacksonParser의 인스턴스
	 */
	public static JacksonParser getInstance() {
		return instance;
	}
	
	private JacksonParser() {
		mapper = new ObjectMapper();
		// don't make null properties write out
		mapper.getSerializationConfig().withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		// for special charactor parsing
		mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		// ignoring unknown Properties
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	/* (non-Javadoc)
	 * @see com.nhn.android.appstore.common.jsonparser.JsonParser#readValue(java.lang.String, java.lang.Class)
	 */
	@Override
	public <T> T readValue(String jsonContent, Class<T> valueType) throws ParserException {
		try {
//			mapper.readValue(jsonContent, valueType);
			return mapper.readValue(jsonContent, valueType);
		} catch (Exception e) {
			throw new ParserException("Jackson deserialize시에 오류가 발생하였습니다.", e);
		}
	}
	
	@Override
	public <T> T readValue(InputStream inputStream, Class<T> valueType) throws ParserException {
		return this.readValue(inputStream.toString(), valueType);
	}

	/* (non-Javadoc)
	 * @see com.nhn.android.appstore.common.jsonparser.JsonParser#writeValueAsString(java.lang.Object)
	 */
	@Override
	public String writeValueAsString(Object value) throws ParserException {
		try {
			return mapper.writeValueAsString(value);
		} catch (Exception e) {
			throw new ParserException("Jackson serialize시에 오류가 발생하였습니다.", e);
		}
	}
}
