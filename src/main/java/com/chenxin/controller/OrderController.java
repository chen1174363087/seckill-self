package com.chenxin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chenxin.dto.Response;
import com.chenxin.entity.CxOrder;
import com.chenxin.entity.CxUser;
import com.chenxin.mq.seckill.Producer;
import com.chenxin.service.IOrderService;
import com.chenxin.util.JwtUtils;
import com.chenxin.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author chenxin
 * @since 2020-07-03
 */
@RestController
@RequestMapping("/seckill/order")
public class OrderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    private final String TOKEN = "token";

    @Autowired
    private IOrderService orderService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private Producer producer;

    @Autowired
    private RedisUtils redisUtils;

    @PostMapping("/create")
    public Response createOrder(HttpServletRequest request, @RequestBody CxOrder cxOrder) {
        Response response = new Response();
        String token = request.getHeader(TOKEN);
        if (cxOrder == null) {
            LOGGER.error("用户为空");
            response.put("code", 200);
            response.put("msg", "用户为空");
            return response;
        }

        //解析token
        Map<String, Object> validateRes = jwtUtils.validateToken(token);
        if (validateRes.get("username") == null || !validateRes.get("username").equals(cxOrder.getUsername())) {
            response.put("code", 200);
            response.put("msg", "订单信息用户名错误");
            return response;
        }

        String msg = producer.buildCreateOrder(cxOrder);
        response.put("code", 200);
        response.put("msg", msg);
        return response;
    }

    @PostMapping("/list")
    public Response getOrder(HttpServletRequest request, @RequestBody CxUser cxUser) {
        Response response = new Response();
        String token = request.getHeader(TOKEN);
        if (StringUtils.isEmpty(token)) {
            LOGGER.error("token为空");
            response.put("code", 200);
            response.put("msg", "token为空");
            return response;
        }

        if (cxUser == null) {
            LOGGER.error("用户为空");
            response.put("code", 200);
            response.put("msg", "用户为空");
            return response;
        }

        //解析token
        Map<String, Object> validateRes = jwtUtils.validateToken(token);
        LOGGER.info("JWT token 解析结果：{}", validateRes.toString());
        if (validateRes.get("username") == null) {
            response.put("code", 200);
            response.put("msg", "请重新登录");
            return response;
        }

        String orderKey = new StringBuilder("seckill：")
                .append(cxUser.getUsername())
                .append(cxUser.getTelphone()).toString();
        Set<String> cxOrders = redisUtils.smembers(orderKey);

        response.put("code", 200);
        response.put("msg", "success");
        response.put("content", cxOrders);
        return response;
    }
}
