package com.mazihao.shengxianmarket.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mazihao.shengxianmarket.common.Constant;
import com.mazihao.shengxianmarket.exception.shengxianmarketException;
import com.mazihao.shengxianmarket.exception.shengxianmarketExceptionEnum;
import com.mazihao.shengxianmarket.model.dao.ProductMapper;
import com.mazihao.shengxianmarket.model.pojo.Product;
import com.mazihao.shengxianmarket.model.query.ProductListQuery;
import com.mazihao.shengxianmarket.model.request.AddCategoryReq;
import com.mazihao.shengxianmarket.model.request.AddProductReq;
import com.mazihao.shengxianmarket.model.request.ProductListReq;
import com.mazihao.shengxianmarket.model.request.UpdateProductReq;
import com.mazihao.shengxianmarket.service.CategoryService;
import com.mazihao.shengxianmarket.service.ProductService;
import com.mazihao.shengxianmarket.vo.CategoryVO;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Resource
    private ProductMapper productMapper;

    @Resource
    private CategoryService categoryService;

    @Override
    public void add(AddProductReq addProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(addProductReq, product);
        Product productOld = productMapper.selectByName(addProductReq.getName());
        if (productOld != null) {
            throw new shengxianmarketException(shengxianmarketExceptionEnum.NAME_NOT_PERMIT_SAME);
        }
        int count = productMapper.insertSelective(product);
        if (count == 0) {
            throw new shengxianmarketException(shengxianmarketExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public void update(Product updateProduct) {
        Product productOld = productMapper.selectByName(updateProduct.getName());
        if (productOld != null && !productOld.getId().equals(updateProduct.getId()))
            throw new shengxianmarketException(shengxianmarketExceptionEnum.NAME_NOT_PERMIT_SAME);
        int count = productMapper.updateByPrimaryKeySelective(updateProduct);
        if (count == 0)
            throw new shengxianmarketException(shengxianmarketExceptionEnum.UPDATE_FAILED);
    }

    @Override
    public void delete(Integer id) {
        Product productOld = productMapper.selectByPrimaryKey(id);
        if (productOld == null)
            throw new shengxianmarketException(shengxianmarketExceptionEnum.DELETE_FAILED);
        int count = productMapper.deleteByPrimaryKey(id);
        if (count == 0)
            throw new shengxianmarketException(shengxianmarketExceptionEnum.DELETE_FAILED);
    }

    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus) {
        productMapper.batchUpdateSellStatus(ids, sellStatus);
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectListForAdmin();
        PageInfo pageInfo = new PageInfo(products);
        return pageInfo;
    }

    @Override
    public Product detail(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);
        return product;
    }

    @Override
    public PageInfo list(ProductListReq productListReq) {
        // 构建Query对象
        ProductListQuery productListQuery = new ProductListQuery();
        // 搜索处理
        if (!StringUtils.isEmpty(productListReq.getKeyword())) {
            String keyword = new StringBuilder().append("%").append(productListReq.getKeyword()).append("%").toString();
            productListQuery.setKeyword(keyword);
        }

        // 处理目录：如果查某个目录下的商品，不仅是需要查出该目录下的，还要把所有子目录的所有商品都查出来，所以要拿到一个目录id的list
        if (productListReq.getCategoryId() != null) {
            List<CategoryVO> categoryVOList = categoryService.listCategoryForCustomer(productListReq.getCategoryId());
            ArrayList<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(productListReq.getCategoryId());
            getCategoryIds(categoryVOList, categoryIds);
            productListQuery.setCategoryIds(categoryIds);
        }

        // 排序处理
        String orderBy = productListReq.getOrderBy();
        if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize(), orderBy);
        } else {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize());
        }

        List<Product> productList = productMapper.selectList(productListQuery);
        PageInfo pageInfo = new PageInfo(productList);
        return pageInfo;
    }

    private void getCategoryIds(List<CategoryVO> categoryVOList, ArrayList<Integer> categoryIds) {
        for (int i = 0; i < categoryVOList.size(); i++) {
            CategoryVO categoryVO = categoryVOList.get(i);
            if (categoryVO != null) {
                categoryIds.add(categoryVO.getId());
                getCategoryIds(categoryVO.getChildCategory(), categoryIds);
            }
        }
    }

}
