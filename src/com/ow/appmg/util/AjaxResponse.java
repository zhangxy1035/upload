package com.ow.appmg.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

/**
 * 
 * @author David
 * 
 */
public class AjaxResponse {

	/**
	 * 1：普通请求  2：jsonp请求
	 */
	public static int jsonpParameterType = 1;
	/**
	 * 1：普通请求  2：jsonp请求
	 */
	public static int responseType = 1;
	
	/**
	 * 解析参数
	 * @param req
	 * @param parameterName
	 * @param jsonOrJsonp
	 * @return
	 */
	public static String parseString (HttpServletRequest req,String parameterName,int jsonOrJsonp){
		String returnString = "";
		if (jsonOrJsonp == 1) {
			returnString = req.getParameter(parameterName);
		} else if (jsonOrJsonp == 2) {
			try {
				returnString = new String(req.getParameter(parameterName).getBytes("ISO-8859-1"),"utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return returnString;
	}
	
	/**
	 * 正式环境
	 * 普通请求
	 * @param req
	 * @param res
	 * @param object
	 */
	public static void sendResponse(HttpServletRequest req,HttpServletResponse res,Object object){
		sendCommonResponse(req,res,object);
	}
	
	/**
	 * 开发环境
	 * 普通请求/jsonp请求
	 * @param res
	 * @param object
	 */
	public static void sendResponse(HttpServletRequest req,HttpServletResponse res,Object object,int type){
		if (type == 1) {
			sendCommonResponse(req,res,object);
		} else if (type ==2) {
			sendJSONPResponse(req,res,object);
		}
	}

	/**
	 * 发送数据
	 * @param res
	 * @param object
	 */
	private static void sendCommonResponse(HttpServletRequest req,HttpServletResponse res,Object object){
		try {
	        //设置页面不缓存
			res.setContentType("application/json");
			res.setHeader("Pragma", "No-cache");
			res.setHeader("Cache-Control", "no-cache");
			res.setCharacterEncoding("UTF-8");
			
	        PrintWriter out= null;
	        out = res.getWriter();
	        out.print(object);
	        out.flush();
	        out.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * 发送JSONP数据
	 * @param res
	 * @param object
	 */
	private static void sendJSONPResponse(HttpServletRequest req,HttpServletResponse res,Object object){
		String callbackFunName = req.getParameter("callbackparam");
		try {
	        //设置页面不缓存
			res.setContentType("application/json");
			res.setHeader("Pragma", "No-cache");
			res.setHeader("Cache-Control", "no-cache");
			res.setCharacterEncoding("UTF-8");
			
	        PrintWriter out= null;
	        out = res.getWriter();
	        out.print(callbackFunName+"("+object.toString()+")");
	        out.flush();
	        out.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	/**
	 * 发送json数据，存在一两位校验位
	 * @param bool
	 * @param user
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	
	public static  void CheckGet(boolean bool,HttpServletRequest req,HttpServletResponse res) throws Exception{
		res.setContentType("application/json");
		res.setHeader("Pragma", "No-cache");
		res.setHeader("Cache-Control", "no-cache");
		res.setCharacterEncoding("UTF-8");
		JSONArray jsonarry = new JSONArray();  
		jsonarry.add(bool);
        PrintWriter out = null;
        out = res.getWriter();
        out.write(jsonarry.toString());
        out.flush();
        out.close();
	}
}
