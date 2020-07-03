package com.chenxin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenxin.dao.OrderMapper;
import com.chenxin.entity.Order;
import com.chenxin.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author chenxin
 * @since 2020-07-03
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public int insertBatchSelf(List<Order> entities) throws Exception{
        return orderMapper.insertBatchSelf(entities);
    }
}
