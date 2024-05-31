package com.mazihao.shengxianmarket.controller;

import com.github.pagehelper.PageInfo;
import com.mazihao.shengxianmarket.common.ApiResrResponse;
import com.mazihao.shengxianmarket.common.Constant;
import com.mazihao.shengxianmarket.exception.shengxianmarketExceptionEnum;
import com.mazihao.shengxianmarket.model.dao.CategoryMapper;
import com.mazihao.shengxianmarket.model.pojo.Category;
import com.mazihao.shengxianmarket.model.pojo.User;
import com.mazihao.shengxianmarket.model.request.AddCategoryReq;
import com.mazihao.shengxianmarket.model.request.UpdateCategoryReq;
import com.mazihao.shengxianmarket.service.CategoryService;
import com.mazihao.shengxianmarket.service.UserService;
import com.mazihao.shengxianmarket.vo.CategoryVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * 目录Controller
 */
@Controller
public class CategoryController {
    @Resource
    private UserService userService;
    @Resource
    private CategoryService categoryService;

    // 管理员添加目录
    @ApiOperation("后台添加目录")
    @PostMapping("/admin/category/add")
    @ResponseBody
    public ApiResrResponse addCategory(@Valid @RequestBody AddCategoryReq addCategoryReq) {
        categoryService.add(addCategoryReq);
        return ApiResrResponse.success();
    }

    // 更新目录
    @ApiOperation("后台更新目录")
    @PostMapping("/admin/category/update")
    @ResponseBody
    public ApiResrResponse updateCategory(@Valid @RequestBody UpdateCategoryReq updateCategoryReq) {
        Category category = new Category();
        BeanUtils.copyProperties(updateCategoryReq, category);
        categoryService.update(category);
        return ApiResrResponse.success();
    }

    @ApiOperation("后台删除目录")
    @PostMapping("/admin/category/delete")
    @ResponseBody
    public ApiResrResponse deleteCategory(@RequestParam Integer id) {
        categoryService.delete(id);
        return ApiResrResponse.success();
    }

    @ApiOperation("后台目录列表")
    @GetMapping("/admin/category/list")
    @ResponseBody
    public ApiResrResponse listCategoryForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = categoryService.listForAdmin(pageNum, pageSize);
        return ApiResrResponse.success(pageInfo);
    }

    @ApiOperation("前台目录列表")
    @GetMapping("/category/list")
    @ResponseBody
    public ApiResrResponse listCategoryForCustomer() {
        List<CategoryVO> categoryVOS = categoryService.listCategoryForCustomer(0);
        return ApiResrResponse.success(categoryVOS);
    }


}
