package com.mazihao.shengxianmarket.controller;

import com.github.pagehelper.PageInfo;
import com.mazihao.shengxianmarket.common.ApiResrResponse;
import com.mazihao.shengxianmarket.common.Constant;
import com.mazihao.shengxianmarket.exception.shengxianmarketException;
import com.mazihao.shengxianmarket.exception.shengxianmarketExceptionEnum;
import com.mazihao.shengxianmarket.model.pojo.Product;
import com.mazihao.shengxianmarket.model.request.AddProductReq;
import com.mazihao.shengxianmarket.model.request.UpdateProductReq;
import com.mazihao.shengxianmarket.service.ProductService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@RestController
public class ProductAdminController {
    @Resource
    private ProductService productService;

    @ApiOperation("后台添加商品")
    @PostMapping("/admin/product/add")
    public ApiResrResponse addProduct(@Valid @RequestBody AddProductReq addProductReq) {
        productService.add(addProductReq);
        return ApiResrResponse.success();
    }

    @ApiOperation("图片上传")
    @PostMapping("/admin/upload/file")
    public ApiResrResponse upload(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid + suffixName;
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);
        if(!fileDirectory.exists()){
            if (!fileDirectory.mkdir()) {
                throw new shengxianmarketException(shengxianmarketExceptionEnum.MKDIR_FAILED);
            }
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return ApiResrResponse.success(getHost(new URI(httpServletRequest.getRequestURI() + "")) + "/images/" + newFileName);
        } catch (URISyntaxException e) {
            return ApiResrResponse.error(shengxianmarketExceptionEnum.UPLOAD_FAILED);
        }
    }

    private URI getHost(URI uri) {
        URI effectiveURI;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
        } catch (URISyntaxException e) {
            effectiveURI = null;
            e.printStackTrace();
        }
        return effectiveURI;
    }

    @ApiOperation("后台更新商品")
    @PostMapping("/admin/product/update")
    public ApiResrResponse updateProduct(@Valid @RequestBody UpdateProductReq updateProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq, product);
        productService.update(product);
        return ApiResrResponse.success();
    }

    @ApiOperation("后台删除商品")
    @PostMapping("/admin/product/delete")
    public ApiResrResponse deleteProduct(@RequestParam Integer id) {
        productService.delete(id);
        return ApiResrResponse.success();
    }

    @ApiOperation("后台批量上下架商品")
    @PostMapping("/admin/product/batchUpdateSellStatus")
    public ApiResrResponse batchUpdateSellStatus(@RequestParam Integer[] ids, @RequestParam Integer sellStatus) {
        productService.batchUpdateSellStatus(ids, sellStatus);
        return ApiResrResponse.success();
    }

    @ApiOperation("后台商品列表")
    @GetMapping("/admin/product/list")
    public ApiResrResponse list(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = productService.listForAdmin(pageNum,pageSize);
        return ApiResrResponse.success(pageInfo);
    }
}
