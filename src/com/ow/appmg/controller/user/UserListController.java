package com.ow.appmg.controller.user;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ow.appmg.dao.UserMapperDao;
import com.ow.appmg.entity.User;
import com.ow.appmg.util.AjaxResponse;



@Controller
@RequestMapping("/user/userlist")
public class UserListController {
	
	@Resource
	private UserMapperDao dao;
	public void setDao(UserMapperDao dao) {
		this.dao = dao;
	}
	
	@RequestMapping("/userlist")
	public void datalist(HttpServletRequest req,HttpServletResponse res) throws Exception{
		res.setContentType("text/plain");  
		List<User> list = dao.findAll();
		JSONArray jsonArray = JSONArray.fromObject(list);
		AjaxResponse.sendResponse(req, res, jsonArray);
	}

}
