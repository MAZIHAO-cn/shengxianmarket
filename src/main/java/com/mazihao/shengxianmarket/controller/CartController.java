package com.mazihao.shengxianmarket.controller;

import com.mazihao.shengxianmarket.common.ApiResrResponse;
import com.mazihao.shengxianmarket.filter.UserFilter;
import com.mazihao.shengxianmarket.service.CartService;
import com.mazihao.shengxianmarket.vo.CartVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Resource
    public CartService cartService;

    @GetMapping("/list")
    @ApiOperation("购物车列表")
    public ApiResrResponse list() {
        // 内部获取用户ID，防止横向越权
        List<CartVO> cartList = cartService.list(UserFilter.currentUser.getId());
        return ApiResrResponse.success(cartList);
    }

    @PostMapping("/add")
    @ApiOperation("添加商品到购物车")
    public ApiResrResponse add(@RequestParam Integer productId, @RequestParam Integer count) {
        int userId = UserFilter.currentUser.getId();
        List<CartVO> cartVOList = cartService.add(userId, productId, count);
        return ApiResrResponse.success(cartVOList);
    }

    @PostMapping("/update")
    @ApiOperation("更新购物车")
    public ApiResrResponse update(@RequestParam Integer productId, @RequestParam Integer count) {
        int userId = UserFilter.currentUser.getId();
        List<CartVO> cartVOList = cartService.update(userId, productId, count);
        return ApiResrResponse.success(cartVOList);
    }

    @PostMapping("/delete")
    @ApiOperation("删除购物车")
    public ApiResrResponse delete(@RequestParam Integer productId) {
        // 不能传入userId， cartId也不能，否则可以删除别人的购物车
        int userId = UserFilter.currentUser.getId();
        List<CartVO> cartVOList = cartService.delete(userId, productId);
        return ApiResrResponse.success(cartVOList);
    }

    @PostMapping("/select")
    @ApiOperation("选择/不选择购物车的某商品")
    public ApiResrResponse select(@RequestParam Integer productId, @RequestParam Integer selected) {
        // 不能传入userId， cartId也不能，否则可以删除别人的购物车
        int userId = UserFilter.currentUser.getId();
        List<CartVO> cartVOList = cartService.selectOrNot(userId, productId, selected);
        return ApiResrResponse.success(cartVOList);
    }

    @PostMapping("/selectAll")
    @ApiOperation("全选/全不选购物车")
    public ApiResrResponse selectAll(@RequestParam Integer selected) {
        // 不能传入userId， cartId也不能，否则可以删除别人的购物车
        int userId = UserFilter.currentUser.getId();
        List<CartVO> cartVOList = cartService.selectOrNotAll(userId, selected);
        return ApiResrResponse.success(cartVOList);
    }

}
