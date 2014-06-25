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
	 * �̹� ������ �����Ǿ� �ִ� �ν��Ͻ��� �����Ѵ�. for singleton
	 * @return JacksonParser�� �ν��Ͻ�
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
			throw new ParserException("Jackson deserialize�ÿ� ������ �߻��Ͽ����ϴ�.", e);
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
			throw new ParserException("Jackson serialize�ÿ� ������ �߻��Ͽ����ϴ�.", e);
		}
	}
}
