package com.mazihao.shengxianmarket.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import com.mazihao.shengxianmarket.common.Constant;
import com.mazihao.shengxianmarket.exception.shengxianmarketException;
import com.mazihao.shengxianmarket.exception.shengxianmarketExceptionEnum;
import com.mazihao.shengxianmarket.filter.UserFilter;
import com.mazihao.shengxianmarket.model.dao.CartMapper;
import com.mazihao.shengxianmarket.model.dao.OrderItemMapper;
import com.mazihao.shengxianmarket.model.dao.OrdersMapper;
import com.mazihao.shengxianmarket.model.dao.ProductMapper;
import com.mazihao.shengxianmarket.model.pojo.Order;
import com.mazihao.shengxianmarket.model.pojo.OrderItem;
import com.mazihao.shengxianmarket.model.pojo.Product;
import com.mazihao.shengxianmarket.model.request.CreateOrderReq;
import com.mazihao.shengxianmarket.service.CartService;
import com.mazihao.shengxianmarket.service.OrderService;
import com.mazihao.shengxianmarket.service.UserService;
import com.mazihao.shengxianmarket.util.OrderCodeFactory;
import com.mazihao.shengxianmarket.util.QRCodeGenerator;
import com.mazihao.shengxianmarket.vo.CartVO;
import com.mazihao.shengxianmarket.vo.OrderItemVO;
import com.mazihao.shengxianmarket.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    public CartService cartService;

    @Resource
    public ProductMapper productMapper;

    @Resource
    public CartMapper cartMapper;

    @Resource
    public OrdersMapper ordersMapper;

    @Resource
    public OrderItemMapper orderItemMapper;

    @Value("${file.upload.ip}")
    public String ip;

    @Resource
    public UserService userService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String create(CreateOrderReq createOrderReq) {
        // 拿到用户id
        Integer userId = UserFilter.currentUser.getId();
        // 从购物车查找已经勾选的商品
        List<CartVO> cartVOList = cartService.list(userId);
        ArrayList<CartVO> cartVOListTemp = new ArrayList<>();
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            if (cartVO.getSelected().equals(Constant.Cart.CHECKED)) {
                cartVOListTemp.add(cartVO);
            }
        }
        cartVOList = cartVOListTemp;
        // 如果购物车已勾选的为空，报错
        if (CollectionUtils.isEmpty(cartVOList)) {
            throw new shengxianmarketException(shengxianmarketExceptionEnum.CART_EMPTY);
        }
        // 判断商品是否存在、上下架状态、库存
        validSaleStatusAndStock(cartVOList);
        // 把购物车对象转为订单item对象
        List<OrderItem> orderItemList = cartVOListToOrderItemList(cartVOList);
        // 扣库存
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            int stock = product.getStock() - orderItem.getQuantity();
            if (stock < 0) {
                throw new shengxianmarketException(shengxianmarketExceptionEnum.NOT_ENOUGH);
            }
            product.setStock(stock);
            productMapper.updateByPrimaryKeySelective(product);
        }
        // 把购物车中的以勾选商品删除
        cleanCart(cartVOList);
        // 生成订单
        Order order = new Order();
        // 生成订单号，有独立的规则
        String orderNo = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());
        order.setPostage(0);
        order.setPaymentType(1);
        // 插入到Order表
        ordersMapper.insertSelective(order);
        // 循环保存每个商品到order_item表
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            orderItem.setOrderNo(order.getOrderNo());
            orderItemMapper.insertSelective(orderItem);
        }
        // 把结果返回
        return orderNo;
    }

    private Integer totalPrice(List<OrderItem> orderItemList) {
        Integer totalPrice = 0;
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            totalPrice = totalPrice + orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    // 检验商品是否有效的function
    private void validSaleStatusAndStock(List<CartVO> cartVOList) {
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            Product product = productMapper.selectByPrimaryKey(cartVO.getProductId());
            // 如果商品为空或者商品的状态是未上架
            if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
                throw new shengxianmarketException(shengxianmarketExceptionEnum.NOT_SALE);
            }
            // 判断商品库存
            if (cartVO.getQuantity() > product.getStock()) {
                throw new shengxianmarketException(shengxianmarketExceptionEnum.NOT_ENOUGH);
            }
        }
    }

    private List<OrderItem> cartVOListToOrderItemList(List<CartVO> cartVOList) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartVO.getProductId());
            // 记录商品快照信息
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    private void cleanCart(List<CartVO> cartVOList) {
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }

    @Override
    public OrderVO detail(String orderNo) {
        Order order = ordersMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new shengxianmarketException(shengxianmarketExceptionEnum.NO_ORDER);
        }
        // 如果订单存在，需要判断所属
        Integer userId = UserFilter.currentUser.getId();
        if (!order.getUserId().equals(userId)) {
            throw new shengxianmarketException(shengxianmarketExceptionEnum.NOT_YOUR_ORDER);
        }
        OrderVO orderVO = getOrderVO(order);
        return orderVO;
    }

    @Override
    public PageInfo listForCustomer(Integer pageNum, Integer pageSize) {
        Integer userId = UserFilter.currentUser.getId();
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = ordersMapper.selectForCustomer(userId);
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo PageInfo = new PageInfo<>(orderList);
        PageInfo.setList(orderVOList);
        return PageInfo;
    }

    private List<OrderVO> orderListToOrderVOList(List<Order> orderList) {
        List<OrderVO> orderVOList = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            OrderVO orderVO = getOrderVO(order);
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }

    private OrderVO getOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        // 获取订单对应的orderItemVOList
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderItemNo(order.getOrderNo());
        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        orderVO.setOrderStatusName(Constant.OrderStatusEnum.codeOf(orderVO.getOrderStatus()).getValue());
        return orderVO;
    }

    @Override
    public void cancel(String orderNo) {
        Order order = ordersMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new shengxianmarketException(shengxianmarketExceptionEnum.NO_ORDER);
        }
        // 验证用户身份
        Integer userId = UserFilter.currentUser.getId();
        if (!order.getUserId().equals(userId)) {
            throw new shengxianmarketException(shengxianmarketExceptionEnum.NOT_YOUR_ORDER);
        }
        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());
            order.setEndTime(new Date());
            ordersMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new shengxianmarketException(shengxianmarketExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public String qrcode(String orderNo) throws IOException, WriterException {
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String address = ip + ":" + request.getLocalPort();
        String payUrl = "http://" + address + "/pay?orderNo=" + orderNo;
        QRCodeGenerator.generateQRCodeImage(payUrl, 350, 350, Constant.FILE_UPLOAD_DIR + orderNo + ".png");
        String pngAddress = "http://" + address + "/images/" + orderNo + ".png";
        return pngAddress;
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = ordersMapper.selectForAdmin();
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo PageInfo = new PageInfo<>(orderList);
        PageInfo.setList(orderVOList);
        return PageInfo;
    }

    @Override
    public void pay(String orderNo) {
        Order order = ordersMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new shengxianmarketException(shengxianmarketExceptionEnum.NO_ORDER);
        }
        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.PAID.getCode());
            order.setPayTime(new Date());
            ordersMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new shengxianmarketException(shengxianmarketExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public void delivered(String orderNo) {
        Order order = ordersMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new shengxianmarketException(shengxianmarketExceptionEnum.NO_ORDER);
        }
        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            ordersMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new shengxianmarketException(shengxianmarketExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public void finished(String orderNo) {
        Order order = ordersMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new shengxianmarketException(shengxianmarketExceptionEnum.NO_ORDER);
        }
        // 如果是普通用户，就要校验订单的所属
        if (!userService.checkAdminRole(UserFilter.currentUser) && !order.getUserId().equals(UserFilter.currentUser.getId())) {
            throw new shengxianmarketException(shengxianmarketExceptionEnum.NOT_YOUR_ORDER);
        }
        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.FINISHED.getCode());
            order.setEndTime(new Date());
            ordersMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new shengxianmarketException(shengxianmarketExceptionEnum.WRONG_ORDER_STATUS);
        }
    }
}
