package com.chenxin.dto;

import java.util.HashMap;

/**
 * @Auther chenxin4
 * @Date 2020/7/3
 * Description
 */
public class Response extends HashMap{
    public static Response ok(){
        Response ok = new Response();
        ok.put("code",200);
        ok.put("msg","success");
        return ok;
    }

    public static Response error(){
        Response error = new Response();
        error.put("code",500);
        error.put("msg","error");
        return error;
    }
}
