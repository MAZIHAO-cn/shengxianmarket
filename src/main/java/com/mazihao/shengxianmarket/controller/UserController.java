package com.mazihao.shengxianmarket.controller;

import com.mazihao.shengxianmarket.common.ApiResrResponse;
import com.mazihao.shengxianmarket.common.Constant;
import com.mazihao.shengxianmarket.exception.shengxianmarketException;
import com.mazihao.shengxianmarket.exception.shengxianmarketExceptionEnum;
import com.mazihao.shengxianmarket.model.pojo.User;
import com.mazihao.shengxianmarket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/test")
    @ResponseBody
    public User personalPage() {
        return userService.getUser();
    }

    @PostMapping("/register")
    @ResponseBody
    public ApiResrResponse register(@RequestParam("username") String username, @RequestParam("password") String password) throws shengxianmarketException, NoSuchAlgorithmException {
        if(StringUtils.isEmpty(username)) {
            return ApiResrResponse.error(shengxianmarketExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isEmpty(password)) {
            return ApiResrResponse.error(shengxianmarketExceptionEnum.NEED_PASSWORD);
        }
        // 密码长度不能小于8位
        if (password.length() < 8) {
            return ApiResrResponse.error(shengxianmarketExceptionEnum.PASSWORD_TOO_SHORT);
        }
        userService.register(username, password);
        return ApiResrResponse.success();
    }

    @PostMapping("/login")
    @ResponseBody
    public ApiResrResponse login(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session) throws NoSuchAlgorithmException, shengxianmarketException {
        if(StringUtils.isEmpty(username)) {
            return ApiResrResponse.error(shengxianmarketExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isEmpty(password)) {
            return ApiResrResponse.error(shengxianmarketExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(username, password);
        user.setPassword(null);
        session.setAttribute(Constant.USER, user);
        return ApiResrResponse.success(user);
    }

    @PostMapping("/user/update")
    @ResponseBody
    public ApiResrResponse updateUserInfo(HttpSession session, @RequestParam("signature") String signature) throws shengxianmarketException {
        User currentUser = (User)session.getAttribute(Constant.USER);
        if (currentUser == null) {
            return ApiResrResponse.error(shengxianmarketExceptionEnum.NEED_LOGIN);
        }
        User user = new User();
        user.setId(currentUser.getId());
        user.setPersonalizedSignature(signature);
        userService.updateInformation(user);
        return ApiResrResponse.success();
    }

    @PostMapping("/user/logout")
    @ResponseBody
    public ApiResrResponse logout(HttpSession session) {
        session.removeAttribute(Constant.USER);
        return ApiResrResponse.success();
    }

    @PostMapping("/adminLogin")
    @ResponseBody
    public ApiResrResponse adminLogin(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session) throws NoSuchAlgorithmException, shengxianmarketException {
        if(StringUtils.isEmpty(username)) {
            return ApiResrResponse.error(shengxianmarketExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isEmpty(password)) {
            return ApiResrResponse.error(shengxianmarketExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(username, password);
        // 校验是不是管理员
        if (userService.checkAdminRole(user)) {
            user.setPassword(null);
            session.setAttribute(Constant.USER, user);
        } else {
            return ApiResrResponse.error(shengxianmarketExceptionEnum.NEED_ADMIN);
        }
        return ApiResrResponse.success(user);
    }
}
