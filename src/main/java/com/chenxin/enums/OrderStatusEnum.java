package com.chenxin.enums;

/**
 * @Auther chenxin4
 * @Date 2020/7/3
 * Description
 */
public enum OrderStatusEnum {
    NOT_PAID("A", "待支付"),
    PAID("B", "已支付"),
    CANCEL("C", "已取消"),
    TICKET("D", "已出票");

    private String status;
    private String msg;

    OrderStatusEnum(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
