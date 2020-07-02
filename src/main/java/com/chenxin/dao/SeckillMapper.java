package com.chenxin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenxin.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SeckillMapper extends BaseMapper<OrderEntity> {
}
