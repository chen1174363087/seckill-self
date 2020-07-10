package com.chenxin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenxin.entity.CxOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author chenxin
 * @since 2020-07-03
 */
@Mapper
public interface OrderMapper extends BaseMapper<CxOrder> {
    int insertBatchSelf(List<CxOrder> entities) throws Exception;
}
