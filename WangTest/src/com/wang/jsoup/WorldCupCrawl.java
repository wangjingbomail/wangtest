package com.wang.jsoup;

import java.net.URLEncoder;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 抓世界杯的数据
 * @author jingbo7
 *
 */
public class WorldCupCrawl {

	
	public static void main(String[] args) throws Exception{
//	    getCapability2("Wayne rooney");	
//		getCapability2("Adam KWARASEY");
		try {
		    getCapability2("Simon MIGNOLET");
		}catch(Exception e) {
			;
		}
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static String getCapability(String name) throws Exception{
		
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpPost httpPost = new HttpPost("http://fm007.cn/plus/advancedsearch.php");
		HttpParams httpParams = httpClient.getParams();
		httpParams.setParameter("mid", "17");
		httpParams.setParameter("dopost", "search");
		httpParams.setParameter("name", name);

		HttpResponse httpResponse = httpClient.execute(httpPost);
		
		System.out.println(EntityUtils.toString(httpResponse.getEntity()));
		
		
		
		return "";
		
	
	} 
	
	public static void getCapability2(String name2) throws Exception{
		HttpClient httpClient = new DefaultHttpClient();
		
		
		HttpGet httpGet = new HttpGet("http://www.fm007.cn/plus/advancedsearch.php?mid=17&dopost=search&name="+URLEncoder.encode(name2));
		
		HttpResponse response = httpClient.execute(httpGet);
		
		String docStr = EntityUtils.toString( response.getEntity()) ;
		
		Document doc = Jsoup.parse(docStr);
		
		Elements tbodys = doc.getElementsByTag("tbody");
		
		Element tbody = tbodys.get(1);
		
		Elements trs = tbody.getElementsByTag("tr");
		
		for(Element tr:trs) {
			Elements tds = tr.getElementsByTag("td");
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", tds.get(1).text());
			jsonObject.put("score", Integer.valueOf(tds.get(4).text()) );
			jsonObject.put("potential", Integer.valueOf( tds.get(5).text()));
			
			System.out.println(jsonObject.toString());
		}
		
		
		
		
		
	}
}
