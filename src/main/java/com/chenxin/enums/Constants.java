package com.chenxin.enums;

/**
 * @Auther chenxin4
 * @Date 2020/7/3
 * Description
 */
public enum Constants {
    ERR_MSG_USERNAME_EMPTY("username为空"),
    ERR_MSG_LOGIN_DOU("账号在别处登录"),
    ERR_MSG_NOT_A_ACOUNT("账户不存在，请注册"),
    ERR_MSG_TOKEN_ERR("解析错误"),
    ERR_MSG_TOKEN_EXP("TOKEN已过期"),
    ERR_MSG_TOKEN_EMPTY("TOKEN为空"),;

    private String errMsg;

    Constants(String errMsg) {
        this.errMsg = errMsg;
    }
}
