package com.mazihao.shengxianmarket.service;

import com.github.pagehelper.PageInfo;
import com.mazihao.shengxianmarket.model.pojo.Category;
import com.mazihao.shengxianmarket.model.request.AddCategoryReq;
import com.mazihao.shengxianmarket.vo.CategoryVO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CategoryService {

    void add(AddCategoryReq addCategoryReq);

    void update(Category updateCategory);

    void delete(Integer id);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    @Cacheable(value = "listCategoryForCustomer")
    List<CategoryVO> listCategoryForCustomer(Integer parentId);
}
