package com.babting.igo.xml_parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.babting.igo.model.xml.NaverSearchApiModel;

public class NaverSearchApiXmlParserDOM {
	private DocumentBuilderFactory parserFact;
	private DocumentBuilder parser;
	
	private String message = "";
	private String errorCode = "";
	
	private List<NaverSearchApiModel> naverApiSearchModelList;
	public List<NaverSearchApiModel> getNaverApiSearchModelList() {
		return naverApiSearchModelList;
	}

	private NaverSearchApiModel naverApiSearchModel;
	
	public NaverSearchApiXmlParserDOM(InputStream inputSream) {
		naverApiSearchModelList = new ArrayList<NaverSearchApiModel>();
		
		this.parserFact = DocumentBuilderFactory.newInstance();
		
		try {
			this.parser = this.parserFact.newDocumentBuilder();
			Document doc = this.parser.parse(inputSream);
			this.parse(doc);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
	private void parse(Document doc) {
		NodeList nodeLst = doc.getElementsByTagName("item");
		
		for (int s = 0; s < nodeLst.getLength(); s++) {
		    Node fstNode = nodeLst.item(s);
		    if(fstNode.getNodeType() == Node.ELEMENT_NODE) {
		    	Element fstElmnt = (Element) fstNode;
		    	
		    	NodeList titleElementList = fstElmnt.getElementsByTagName("title");
		    	NodeList addressElementList = fstElmnt.getElementsByTagName("address");
		    	NodeList descriptionElementList = fstElmnt.getElementsByTagName("description");
		    	NodeList mapxElementList = fstElmnt.getElementsByTagName("mapx");
		    	NodeList mapyElementList = fstElmnt.getElementsByTagName("mapy");
		    	
		    	naverApiSearchModel = new NaverSearchApiModel();
		    	naverApiSearchModel.setTitle(getValue(titleElementList));
		    	naverApiSearchModel.setAddress(getValue(addressElementList));
		    	naverApiSearchModel.setDescription(getValue(descriptionElementList));
		    	naverApiSearchModel.setMapX(getValue(mapxElementList));
		    	naverApiSearchModel.setMapY(getValue(mapyElementList));
		    	
		    	naverApiSearchModelList.add(naverApiSearchModel);
		    }
		}
	}
	
	private String getValue(NodeList nodeList) {
		Element titleElmnt = (Element)nodeList.item(0);
    	
    	NodeList titleNodeList = titleElmnt.getChildNodes();
    	
    	if(titleNodeList.getLength() > 0) {
    		return titleNodeList.item(0).getNodeValue().replace("<b>", "").replace("</b>", "");
    	} else {
    		return "";
    	}
	}
	
	public String getMessage() {
		return message;
	}

	public String getErrorCode() {
		return errorCode;
	}
}
