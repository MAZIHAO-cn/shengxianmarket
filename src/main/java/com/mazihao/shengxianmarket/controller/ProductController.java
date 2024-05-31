package com.mazihao.shengxianmarket.controller;

import com.github.pagehelper.PageInfo;
import com.mazihao.shengxianmarket.common.ApiResrResponse;
import com.mazihao.shengxianmarket.model.pojo.Product;
import com.mazihao.shengxianmarket.model.request.ProductListReq;
import com.mazihao.shengxianmarket.service.ProductService;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 前台商品Controller
 */
@RestController
public class ProductController {
    @Resource
    private ProductService productService;

    @ApiOperation("商品详情")
    @GetMapping("product/detail")
    public ApiResrResponse detail(@RequestParam Integer id) {
        Product product = productService.detail(id);
        return ApiResrResponse.success(product);
    }

    @ApiOperation("商品详情")
    @GetMapping("product/list")
    public ApiResrResponse list(ProductListReq productListReq) {
        PageInfo list = productService.list(productListReq);
        return ApiResrResponse.success(list);
    }
}
