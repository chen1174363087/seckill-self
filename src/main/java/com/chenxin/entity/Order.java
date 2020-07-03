package com.chenxin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;


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
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

//    private Long id;

    private Long productId;

    private String username;

    private String telphone;

    private String address;

    private String status;

    private LocalDateTime createTime;


}
