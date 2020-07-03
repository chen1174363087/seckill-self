package com.chenxin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenxin.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author chenxin
 * @since 2020-07-03
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}
