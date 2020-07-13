package com.chenxin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 产品表
 * </p>
 *
 * @author chenxin
 * @since 2020-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CxProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String describle;

    private Date createTime;

    private Integer stock;


}
