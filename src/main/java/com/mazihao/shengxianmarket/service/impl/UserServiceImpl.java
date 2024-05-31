package com.mazihao.shengxianmarket.service.impl;

import com.mazihao.shengxianmarket.exception.shengxianmarketException;
import com.mazihao.shengxianmarket.exception.shengxianmarketExceptionEnum;
import com.mazihao.shengxianmarket.model.dao.UserMapper;
import com.mazihao.shengxianmarket.model.pojo.User;
import com.mazihao.shengxianmarket.service.UserService;
import com.mazihao.shengxianmarket.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;


    @Override
    public User getUser() {
        return userMapper.selectByPrimaryKey(1);
    }

    @Override
    public void register(String username, String password) throws shengxianmarketException, NoSuchAlgorithmException {
        // 查询用户是否重名
        User result = userMapper.selectByName(username);
        if (result != null) {
            throw new shengxianmarketException(shengxianmarketExceptionEnum.NAME_NOT_PERMIT_SAME);
        }

        // 写进数据库
        User user = new User();
        user.setUsername(username);
        // user.setPassword(password);
        user.setPassword(MD5Utils.getMD5Str(password));
        int count = userMapper.insertSelective(user);
        if (count == 0) {
            throw new shengxianmarketException(shengxianmarketExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public User login(String username, String password) throws shengxianmarketException, NoSuchAlgorithmException {
        String md5Password = null;
        md5Password = MD5Utils.getMD5Str(password);
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            throw new shengxianmarketException(shengxianmarketExceptionEnum.WRONG_PASSWORD);
        }
        return user;
    }

    @Override
    public void updateInformation(User user) throws shengxianmarketException {
        //更新个性签名
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 1) {
            throw new shengxianmarketException(shengxianmarketExceptionEnum.UPDATE_FAILED);
        }

    }

    @Override
    public boolean checkAdminRole(User user) {
        return user.getRole().equals(2);
    }
}
