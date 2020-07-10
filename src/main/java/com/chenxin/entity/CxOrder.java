package com.chenxin.entity;

import com.chenxin.enums.OrderStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;


/**
 * <p>
 * 订单表
 * </p>
 *
 * @author chenxin
 * @since 2020-07-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CxOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long productId;

    private String username;

    private String telphone;

    private String address;

    private String status = OrderStatusEnum.NOT_PAID.getStatus();

    private Date createTime;


}
