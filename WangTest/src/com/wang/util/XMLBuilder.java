package com.wang.util;

import java.io.ByteArrayOutputStream;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XMLBuilder {


//	private Document doc;
	private Element element;

	private XMLBuilder() {
		;
	}
	
	public static XMLBuilder getBuilder(String name){
		
		if ( name==null || "".equals(name)) {
			return null;
			//TODO
		}
		
		XMLBuilder xmlBuilder = new XMLBuilder();
		
		
		xmlBuilder.element = new Element(name);
//		xmlBuilder.doc = new Document( xmlBuilder.element);
		
		return xmlBuilder;
	}
	
	
	public void append(String name, String value){
		if (name==null || "".equals(name)) {
			//TODO
		}
		
		Element element = new Element(name);
		element.addContent(value);
		
		this.element.addContent(element);
	}
	
	public void appendCDATA(String name, String value) {
		if (name==null || "".equals(name)) {
			//TODO
		}
		
		if ( value==null || "".equals(value)) {
			//TODO
		}
		
		Element element = new Element(name);
		element.addContent(new CDATA(value));
		
		this.element.addContent(element);
	}
	
	/**
	 * 增加嵌套的xml
	 * @param xmlBuilder
	 */
	public void appendBuilder(XMLBuilder xmlBuilder) {
		this.element.addContent(xmlBuilder.getElement());
	}
	
	
	@Override
	public String toString() {
		
		XMLOutputter outputter = new XMLOutputter();

        Format f = Format.getPrettyFormat();
        f.setEncoding("UTF-8");
        f.setIndent(" ");
        f.setOmitDeclaration(true);

        outputter.setFormat(f);
        
        Document doc = new Document( element);
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        try {
            outputter.output(doc, byteArrayOutputStream);
        }catch(Exception e) {
        	//TODO
        }
        
        return byteArrayOutputStream.toString();
	}
	
	
	
	
	
	
	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	public static void main(String[] args) {
		
//		test();
		test2();
	}
	
	public static void test() {
		XMLBuilder xmlBuilder = XMLBuilder.getBuilder("xml");
		xmlBuilder.appendCDATA("ToUserName", "4956556144");
		xmlBuilder.appendCDATA("FromUserName", "4956556144");
		xmlBuilder.append("CreateTime", "1348831860");
		xmlBuilder.appendCDATA("MsgType", "text");
		xmlBuilder.appendCDATA("content", "this is test");
		xmlBuilder.append("MsgId", "1234567890123456");
		
		
		
		System.out.println(xmlBuilder.toString());
	}
	
	
	public static void test2() {
		XMLBuilder xmlBuilder = XMLBuilder.getBuilder("xml");
		xmlBuilder.appendCDATA("ToUserName", "4956556144");
		xmlBuilder.appendCDATA("FromUserName", "4956556144");
		xmlBuilder.append("CreateTime", "1348831860");
		xmlBuilder.appendCDATA("MsgType", "text");
		xmlBuilder.appendCDATA("content", "this is test");
		xmlBuilder.append("MsgId", "1234567890123456");
		
		
		XMLBuilder xmlBuilder2 = XMLBuilder.getBuilder("bean");
		xmlBuilder2.append("property", "value");
		xmlBuilder.appendBuilder(xmlBuilder2);
		
		
		
		
		System.out.println(xmlBuilder.toString());
	}
	
	
	
	
}
