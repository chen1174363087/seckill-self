package com.chenxin.entity;

import com.chenxin.enums.OrderStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;


/**
 * <p>
 * 订单表
 * </p>
 *
 * @author chenxin
 * @since 2020-07-03
 */
@Data
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CxOrder cxOrder = (CxOrder) o;
        return Objects.equals(id, cxOrder.id) &&
                Objects.equals(productId, cxOrder.productId) &&
                Objects.equals(username, cxOrder.username) &&
                Objects.equals(telphone, cxOrder.telphone) &&
                Objects.equals(address, cxOrder.address) &&
                Objects.equals(status, cxOrder.status) &&
                Objects.equals(createTime, cxOrder.createTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id, productId, username, telphone, address, status, createTime);
    }
}
