package com.ow.appmg.test;



import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;





public class Test {
	public static void main(String[] args) throws JSONException {
		String xml ="<dependency>"+"<groupId>junit</groupId>"+"<artifactId>junit</artifactId>"
				+"<version>${junitversion}</version>"+"<scope>test</scope>"+" </dependency>";
		JSONObject xmlJSONObj = XML.toJSONObject(xml);
		System.out.println(xmlJSONObj.toString());
//		String jsonPrettyPrintString = xmlJSONObj.toString(4);

//		/* 第二种方法，使用json-lib提供的方法 */
//		//创建 XMLSerializer对象
//		XMLSerializer xmlSerializer = new XMLSerializer();
//		//将xml转为json（注：如果是元素的属性，会在json里的key前加一个@标识）
//		String result = xmlSerializer.read(xml).toString();
//		//输出json内容
//		System.out.println(result);
	}


}

