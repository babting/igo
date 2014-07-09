package com.babting.igo.xml_parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.babting.igo.model.xml.NaverSearchApiModel;

import android.util.Xml;

public class NaverSearchApiXmlParserSAX extends DefaultHandler {
	private SAXParserFactory parserFact;
	private SAXParser parser;
	
	private boolean isTitle = false;
	private boolean isLink = false;
	private boolean isCategory = false;
	private boolean isDescription = false;
	private boolean isTel = false;
	private boolean isAddress = false;
	private boolean isRoadAddress = false;
	private boolean isMapX = false;
	private boolean isMapY = false;
	private boolean isItem = false;
	private boolean isError = false;
	private boolean isMessage = false;
	private boolean isErrorCode = false;
	private String message = "";
	private String errorCode = "";
	
	private List<NaverSearchApiModel> naverApiSearchModelList;
	public List<NaverSearchApiModel> getNaverApiSearchModelList() {
		return naverApiSearchModelList;
	}

	private NaverSearchApiModel naverApiSearchModel;
	
	public NaverSearchApiXmlParserSAX(InputStream inputSream) {
		naverApiSearchModelList = new ArrayList<NaverSearchApiModel>();
		
		this.parserFact = SAXParserFactory.newInstance();
		
		try {
			this.parser = parserFact.newSAXParser();
			this.parser.parse(inputSream, this);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		
		if(qName.equals("item")) {
			if(naverApiSearchModel != null) {
				naverApiSearchModelList.add(naverApiSearchModel);
			}
			naverApiSearchModel = new NaverSearchApiModel();
			
			isItem = true;
		} else if(qName.equals("title")) {
			isTitle = true;
		} else if(qName.equals("link")) {
			isLink = true;
		} else if(qName.equals("category")) {
			isCategory = true;
		} else if(qName.equals("description")) {
			isDescription = true;
		} else if(qName.equals("telephone")) {
			isTel = true;
		} else if(qName.equals("address")) {
			isAddress = true;
		} else if(qName.equals("roadAddress")) {
			isRoadAddress = true;
		} else if(qName.equals("mapx")) {
			isMapX = true;
		} else if(qName.equals("mapy")) {
			isMapY = true;
		} else if(qName.equals("error")) {
			isError = true;
		} else if(qName.equals("message")) {
			isMessage = true;
		} else if(qName.equals("errorCode")) {
			isErrorCode = true;
		}
			
	}
	
	@Override
	public void error(SAXParseException e) throws SAXException {
		// TODO Auto-generated method stub
		super.error(e);
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		
		if(isTitle && isItem) {
			naverApiSearchModel.setTitle(new String(ch, start, length));
			isTitle = false;
		} else if(isLink && isItem) {
			naverApiSearchModel.setLink(new String(ch, start, length));
			isLink = false;
		} else if(isCategory && isItem) {
			naverApiSearchModel.setCategory(new String(ch, start, length));
			isCategory = false;
		} else if(isDescription && isItem) {
			naverApiSearchModel.setDescription(new String(ch, start, length));
			isDescription = false;
		} else if(isTel && isItem) {
			naverApiSearchModel.setTelephone(new String(ch, start, length));
			isTel = false;
		} else if(isAddress && isItem) {
			naverApiSearchModel.setAddress(new String(ch, start, length));
			isAddress = false;
		} else if(isRoadAddress && isItem) {
			naverApiSearchModel.setRoadAddress(new String(ch, start, length));
			isRoadAddress = false;
		} else if(isMapX && isItem) {
			naverApiSearchModel.setMapX(new String(ch, start, length));
			isMapX = false;
		} else if(isMapY && isItem) {
			naverApiSearchModel.setMapY(new String(ch, start, length));
			isMapY = false;
		} else if(isMessage) {
			message = new String(ch, start, length);
			isMessage = false;
		} else if(isError && isErrorCode) {
			errorCode = new String(ch, start, length);
			isErrorCode = false;
		}
 	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		
		if("item".equals(qName)) {
			isItem = false;
		}
	}
	
	public String getMessage() {
		return message;
	}

	public String getErrorCode() {
		return errorCode;
	}
}
