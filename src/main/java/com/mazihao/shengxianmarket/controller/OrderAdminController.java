package com.mazihao.shengxianmarket.controller;

import com.github.pagehelper.PageInfo;
import com.mazihao.shengxianmarket.common.ApiResrResponse;
import com.mazihao.shengxianmarket.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class OrderAdminController {

    @Resource
    public OrderService orderService;

    @GetMapping("admin/order/list")
    @ApiOperation("管理员订单列表")
    public ApiResrResponse listForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = orderService.listForAdmin(pageNum, pageSize);
        return ApiResrResponse.success(pageInfo);
    }

    @PostMapping("admin/order/delivered")
    @ApiOperation("管理员发货")
    public ApiResrResponse delivered(@RequestParam String orderNo) {
        orderService.delivered(orderNo);
        return ApiResrResponse.success();
    }

    @PostMapping("/order/finish")
    @ApiOperation("订单交易完结")
    public ApiResrResponse finished(@RequestParam String orderNo) {
        orderService.finished(orderNo);
        return ApiResrResponse.success();
    }
}
