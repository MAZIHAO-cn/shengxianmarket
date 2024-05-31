package com.mazihao.shengxianmarket.model.dao;

import com.mazihao.shengxianmarket.model.pojo.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByOrderNo(String orderNo);

    List<Order> selectForCustomer(Integer userId);

    List<Order> selectForAdmin();
}