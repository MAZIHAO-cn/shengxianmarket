package com.mazihao.shengxianmarket.service;

import com.github.pagehelper.PageInfo;
import com.mazihao.shengxianmarket.model.pojo.Category;
import com.mazihao.shengxianmarket.model.pojo.Product;
import com.mazihao.shengxianmarket.model.request.AddCategoryReq;
import com.mazihao.shengxianmarket.model.request.AddProductReq;
import com.mazihao.shengxianmarket.model.request.ProductListReq;
import com.mazihao.shengxianmarket.vo.CategoryVO;

import java.util.List;

/**
 * 商品Service
 */
public interface ProductService {

    void add(AddProductReq addProductReq);

    void update(Product updateProduct);

    void delete(Integer id);

    void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    Product detail(Integer id);

    PageInfo list(ProductListReq productListReq);
}
