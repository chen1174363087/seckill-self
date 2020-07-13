package com.chenxin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chenxin.entity.CxOrder;

import java.util.List;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author chenxin
 * @since 2020-07-03
 */
public interface IOrderService extends IService<CxOrder> {
    boolean createOrder(CxOrder cxOrder);

    int insertBatchSelf(List<CxOrder> entities) throws Exception;
}
