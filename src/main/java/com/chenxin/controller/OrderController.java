package com.chenxin.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chenxin.dto.Response;
import com.chenxin.entity.Order;
import com.chenxin.entity.User;
import com.chenxin.mq.seckill.Producer;
import com.chenxin.service.IOrderService;
import com.chenxin.util.JwtUtils;
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

    @PostMapping("/create")
    public Response createOrder(HttpServletRequest request, @RequestBody Order order) {
        Response response = new Response();
        String token = request.getHeader(TOKEN);
        if (order == null) {
            LOGGER.error("用户为空");
            response.put("code", 200);
            response.put("msg", "用户为空");
            return response;
        }

        //解析token
        Map<String, Object> validateRes = jwtUtils.validateToken(token);
        if (validateRes.get("username") == null || !validateRes.get("username").equals(order.getUsername())) {
            response.put("code", 200);
            response.put("msg", "订单信息用户名错误");
            return response;
        }
        producer.buildCreateOrder(order);

        response.put("code", 200);
        response.put("msg", "申请成功，排队中...");
        return response;
    }

    @PostMapping("/list")
    public Response getOrder(HttpServletRequest request, @RequestBody User user) {
        Response response = new Response();
        String token = request.getHeader(TOKEN);
        if (StringUtils.isEmpty(token)) {
            LOGGER.error("token为空");
            response.put("code", 200);
            response.put("msg", "token为空");
            return response;
        }

        if (user == null) {
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

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", user.getUsername());
        List<Order> orders = orderService.list(queryWrapper);

        response.put("code", 200);
        response.put("msg", "success");
        response.put("content", orders);
        return response;
    }
}
