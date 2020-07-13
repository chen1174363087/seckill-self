package com.chenxin.common;

import com.chenxin.entity.CxOrder;

/**
 * @Auther chenxin4
 * @Date 2020/7/13
 * Description
 */
public class CommonUtils {
    public static String getOrderKey(CxOrder cxOrder) {
        return new StringBuilder("seckill：")
                .append(cxOrder.getUsername())
                .append(cxOrder.getTelphone()).toString();
    }
}
