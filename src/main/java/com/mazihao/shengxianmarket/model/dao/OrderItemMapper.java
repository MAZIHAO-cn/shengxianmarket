package com.mazihao.shengxianmarket.model.dao;

import com.mazihao.shengxianmarket.model.pojo.Order;
import com.mazihao.shengxianmarket.model.pojo.OrderItem;
import com.mazihao.shengxianmarket.vo.OrderVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<OrderItem> selectByOrderItemNo(String orderNo);
}