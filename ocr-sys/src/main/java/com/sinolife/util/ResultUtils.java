package com.sinolife.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultUtils {

	
	/**
	 * 判断模板是否存在
	 * @return
	 */
	
	public static JSONArray GetTables(String content) {
		JSONArray words_block_lists=new JSONArray();
		//1.获得所有的 words_region_list
		JSONObject json=JSONObject.parseObject(content);
		@SuppressWarnings("unchecked")
		Map<String, Object> result=(Map<String, Object>)json.get("result");
		JSONArray words_region_list=(JSONArray) result.get("words_region_list");
		//2.保存type为table的JSON
		JSONArray tables=new JSONArray();
		for (int i = 0; i < words_region_list.size(); i++) {
			JSONObject temp=words_region_list.getJSONObject(i);
			if (temp.get("type").equals("table")) {
				tables.add(temp);
			}
		}
		//3.获取words_block_list
		for (int i = 0; i < tables.size(); i++) {
			JSONObject tep2=tables.getJSONObject(i);
			JSONArray words_block_list=(JSONArray) tep2.get("words_block_list");
			words_block_lists.add(words_block_list);
		}
		return words_block_lists;
	}
	
	public static JSONArray GetText(String content) {
		JSONArray words_block_lists=new JSONArray();
		//1.获得所有的 words_region_list
		JSONObject json=JSONObject.parseObject(content);
		@SuppressWarnings("unchecked")
		Map<String, Object> result=(Map<String, Object>)json.get("result");
		JSONArray words_region_list=(JSONArray) result.get("words_region_list");
		//2.保存type为table的JSON
		JSONArray tables=new JSONArray();
		for (int i = 0; i < words_region_list.size(); i++) {
			JSONObject temp=words_region_list.getJSONObject(i);
			if (temp.get("type").equals("text")) {
				tables.add(temp);
			}
		}
		//3.获取words_block_list
		for (int i = 0; i < tables.size(); i++) {
			JSONObject tep2=tables.getJSONObject(i);
			JSONArray words_block_list=(JSONArray) tep2.get("words_block_list");
			words_block_lists.add(words_block_list);
		}
		return words_block_lists;
	}
	
	public static boolean isNumericzidai(String str) {
		Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
		return false;
		}
		return true;
		}
}
