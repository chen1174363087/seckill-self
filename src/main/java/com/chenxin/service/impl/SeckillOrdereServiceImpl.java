package com.chenxin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenxin.dao.SeckillMapper;
import com.chenxin.entity.OrderEntity;
import org.springframework.stereotype.Service;
import com.chenxin.service.ISeckillOrderService;

/**
 * <p>
 * 订单服务 服务实现类
 * </p>
 *
 * @author chenxin
 * @since 2020-07-02
 */
@Service
public class SeckillOrdereServiceImpl extends ServiceImpl<SeckillMapper, OrderEntity> implements ISeckillOrderService {

}
