package com.mazihao.shengxianmarket.service;

import com.mazihao.shengxianmarket.exception.shengxianmarketException;
import com.mazihao.shengxianmarket.model.pojo.User;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

public interface UserService {
    public User getUser();

    public void register(String username, String password) throws shengxianmarketException, NoSuchAlgorithmException;

    public User login(String username, String password) throws shengxianmarketException, NoSuchAlgorithmException;

    public void updateInformation(User user) throws shengxianmarketException;

    boolean checkAdminRole(User user);
}
