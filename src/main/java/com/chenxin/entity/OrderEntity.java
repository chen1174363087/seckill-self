package com.chenxin.entity;

import java.util.Date;

/**
 * @Auther chenxin4
 * @Date 2020/7/2
 * Description 秒杀订单实体
 */
public class OrderEntity {
    private Long id;
    private String username;
    private String telphone;
    private String address;//收货地址
    private Long productId; //售卖商品id
    private Date createTime; //订单创建时间
    private String status; //订单状态

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OrderEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", telphone='" + telphone + '\'' +
                ", address='" + address + '\'' +
                ", productId=" + productId +
                ", createTime=" + createTime +
                ", status='" + status + '\'' +
                '}';
    }
}
