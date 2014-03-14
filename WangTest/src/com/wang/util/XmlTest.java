package com.wang.util;

import java.io.ByteArrayOutputStream;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;



/**
 * 测试xml
 * @author jingbo7
 *
 */
public class XmlTest {
	
	public static void main(String[] args) throws Exception{
		
//		test1();
//		test2();
		test3();
		
	}
	
//	  Element eroot = new Element("response");
//      toXml(obj, eroot);
//      Document doc = new Document(eroot);
//
//      XMLOutputter outputter = new XMLOutputter();
//      Format f = Format.getPrettyFormat();
//      f.setEncoding("GBK");
//
//      outputter.setFormat(f);
//      outputter.output(doc, out);
//	
	
	/**
	 * 带有申明的
	 * @throws Exception
	 */
	public static void test1 () throws Exception{
		Element element = new Element("xml");
		
		Element child = new Element("ToUserName");
		child.addContent("34566");
		
		Element child2 = new Element("FromUserName");
		child2.addContent("5656767");
		
		element.addContent(child);
		element.addContent(child2);
		

        XMLOutputter outputter = new XMLOutputter();
        Format f = Format.getPrettyFormat();
        f.setEncoding("UTF-8");

        outputter.setFormat(f);
        
        Document doc = new Document(element);
        
        outputter.output(doc, System.out);
        
		
	}
	
	
	
	/**
	 * 不带有申明的
	 * @throws Exception
	 */
	public static void test2 () throws Exception{
		Element element = new Element("xml");
		
		Element child = new Element("ToUserName");
		child.addContent("34566");
		
		Element child2 = new Element("FromUserName");
		child2.addContent("5656767");
		
		element.addContent(child);
		element.addContent(child2);
		

		XMLOutputter outputter = new XMLOutputter();

        Format f = Format.getPrettyFormat();
        f.setEncoding("UTF-8");
        f.setOmitDeclaration(true);

        outputter.setFormat(f);
        
        Document doc = new Document(element);
        
        outputter.output(doc, System.out);
        	
	}
	
	
	/**
	 * 带CATA的
	 * @throws Exception
	 */
	public static void test3 () throws Exception{
		Element element = new Element("xml");
		
//		CDATA cData = new CDATA("");
		Element child = new Element("ToUserName");
		child.addContent(new CDATA("12356"));
		
		Element child2 = new Element("FromUserName");
		child2.addContent("5656767");
		
		element.addContent(child);
		element.addContent(child2);
		

		XMLOutputter outputter = new XMLOutputter();

        Format f = Format.getPrettyFormat();
        f.setEncoding("UTF-8");
        f.setOmitDeclaration(true);

        outputter.setFormat(f);
        
      
        Document doc = new Document(element);
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        outputter.output(doc, byteArrayOutputStream);
        
        System.out.println(byteArrayOutputStream.toString());
//        outputter.output(cdata, out);

	}
	
	

}
