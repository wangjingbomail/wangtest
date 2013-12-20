package com.wang.util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;

public class XmlDocumentTest {


	public static void main(String[] args) throws Exception{
		test();
	}
	
	public static void test()  throws Exception{
		String xmlStr = "<?xml version=\"1.0\" encoding=\"gbk\"?><response><result>succ</result><errorcode>0</errorcode><xuid>2002711103</xuid></response>";
        Document document = DocumentHelper.parseText(xmlStr);
        
        final String resultString = document.valueOf("response");
        
        System.out.println(resultString);
	}
}
