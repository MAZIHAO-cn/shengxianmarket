package com.mazihao.shengxianmarket.controller;

import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import com.mazihao.shengxianmarket.common.ApiResrResponse;
import com.mazihao.shengxianmarket.model.request.CreateOrderReq;
import com.mazihao.shengxianmarket.service.OrderService;
import com.mazihao.shengxianmarket.vo.OrderVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Struct;

@RestController
public class OrderController {

    @Resource
    public OrderService orderService;

    @PostMapping("/order/create")
    @ApiOperation("创建订单")
    public ApiResrResponse create(@RequestBody CreateOrderReq createOrderReq) {
        String orderNo = orderService.create(createOrderReq);
        return ApiResrResponse.success(orderNo);
    }

    @GetMapping("/order/detail")
    @ApiOperation("前台订单详情")
    public ApiResrResponse detail(@RequestParam String orderNo) {
        OrderVO orderVO = orderService.detail(orderNo);
        return ApiResrResponse.success(orderVO);
    }

    @GetMapping("/order/list")
    @ApiOperation("前台订单列表")
    public ApiResrResponse list(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = orderService.listForCustomer(pageNum, pageSize);
        return ApiResrResponse.success(pageInfo);
    }

    @PostMapping("/order/cancel")
    @ApiOperation("取消前台订单")
    public ApiResrResponse cancel(@RequestParam String orderNo) {
        orderService.cancel(orderNo);
        return ApiResrResponse.success();
    }

    @GetMapping("/order/qrcode")
    @ApiOperation("生成支付二维码")
    public ApiResrResponse qrcode(@RequestParam String orderNo) throws IOException, WriterException {
        String pngAddress = orderService.qrcode(orderNo);
        return ApiResrResponse.success(pngAddress);
    }

    @GetMapping("/pay")
    @ApiOperation("支付接口")
    public ApiResrResponse pay(@RequestParam String orderNo){
        orderService.pay(orderNo);
        return ApiResrResponse.success();
    }

}
