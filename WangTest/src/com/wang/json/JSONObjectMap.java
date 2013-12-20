package com.wang.json;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import net.sf.json.JSONObject;

import org.junit.Test;


public class JSONObjectMap {


	
	@Test
	public void test1() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("abc", "12");
		map.put("23", "中国");
		JSONObject jsonObject = JSONObject.fromObject(map);
		String jsonStr = jsonObject.toString();
		
		Assert.assertEquals(map, jsonObject);
		
		Map<String, String> map2 = JSONObject.fromObject(jsonStr);
		
		Assert.assertEquals(map, map2);
	}
}
