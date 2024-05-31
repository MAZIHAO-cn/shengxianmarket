package com.mazihao.shengxianmarket.service;

import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import com.mazihao.shengxianmarket.model.request.CreateOrderReq;
import com.mazihao.shengxianmarket.vo.CartVO;
import com.mazihao.shengxianmarket.vo.OrderVO;

import java.io.IOException;
import java.util.List;

public interface OrderService {

    String create(CreateOrderReq createOrderReq);

    OrderVO detail(String orderNo);

    PageInfo listForCustomer(Integer pageNum, Integer pageSize);

    void cancel(String orderNo);

    String qrcode(String orderNo) throws IOException, WriterException;

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    void pay(String orderNo);

    void delivered(String orderNo);

    void finished(String orderNo);
}
