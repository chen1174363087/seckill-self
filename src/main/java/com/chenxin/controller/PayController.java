package com.chenxin.controller;

import com.alibaba.fastjson.JSON;
import com.chenxin.common.CommConfig;
import com.chenxin.common.CommonUtils;
import com.chenxin.dto.Response;
import com.chenxin.entity.CxOrder;
import com.chenxin.enums.OrderStatusEnum;
import com.chenxin.service.IOrderService;
import com.chenxin.util.JwtUtils;
import com.chenxin.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Auther chenxin4
 * @Date 2020/7/13
 * Description
 */
@RestController
@RequestMapping("/seckill")
public class PayController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayController.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private IOrderService orderService;

    @RequestMapping(value = "/pay")
    public Response pay(HttpServletRequest request, @PathVariable("orderId") String orderId) {
        Response response = new Response();
        String token = request.getHeader(CommConfig.TOKEN);
        if (orderId == null) {
            LOGGER.error("订单号为空");
            response.put("code", 200);
            response.put("msg", "订单号为空");
            return response;
        }

        //解析token
        Map<String, Object> validateRes = jwtUtils.validateToken(token);

        CxOrder cxOrder = orderService.getById(orderId);
        if (cxOrder == null) {
            LOGGER.error("订单不存在");
            response.put("code", 200);
            response.put("msg", "订单不存在");
            return response;
        }
        if (validateRes.get("username") == null || !validateRes.get("username").equals(cxOrder.getUsername())) {
            response.put("code", 200);
            response.put("msg", "订单信息用户名错误");
            return response;
        }

        CxOrder oldOrder = new CxOrder();
        BeanUtils.copyProperties(cxOrder, oldOrder);
        //更新订单已支付
        cxOrder.setStatus(OrderStatusEnum.PAID.getStatus());
        orderService.saveOrUpdate(cxOrder);

        //删除待支付的缓存
        String orderKey = CommonUtils.getOrderKey(oldOrder);
        long deleteNum = redisUtils.srem(orderKey, JSON.toJSONString(oldOrder));
        if (deleteNum > 0) {
            redisUtils.sadd(orderKey, JSON.toJSONString(cxOrder));
        }

        response.put("code", 200);
        response.put("msg", "支付成功");
        return response;
    }
}
