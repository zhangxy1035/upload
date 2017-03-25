package com.ow.appmg.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ow.appmg.entity.User;
import com.ow.appmg.util.MyBatisDao;
@MyBatisDao
@Repository("UserMapper")
public interface UserMapperDao {
	public List<User> findAll();
}