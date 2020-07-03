package com.chenxin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chenxin.dto.Response;
import com.chenxin.entity.User;
import com.chenxin.service.IUserService;
import com.chenxin.util.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.Date;

/**
 * @Auther chenxin4
 * @Date 2020/7/3
 * Description
 */
@RestController
@RequestMapping("/seckill")
public class LoginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private IUserService iUserService;

    @Value("${jwt.prefix}")
    private String jwtPrefix;

    @Value("${jwt.expiretime}")
    private long expiretime;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Response login(@RequestBody User userEntity) {
        Response response = new Response();
        if (userEntity == null || StringUtils.isEmpty(userEntity.getUsername())) {
            LOGGER.error("用户名为空");
            response.put("code", 200);
            response.put("msg", "用户名为空");
            return response;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userEntity.getUsername());
        User userEntity1 = iUserService.getOne(queryWrapper);
        if (userEntity1 == null) {
            LOGGER.error("用户不存在");
            response.put("code", 200);
            response.put("msg", "用户不存在");
            return response;
        }

        if (!userEntity.getPassword().equals(userEntity1.getPassword())) {
            LOGGER.error("密码错误");
            response.put("code", 200);
            response.put("msg", "密码错误");
            return response;
        }

        String token = jwtUtils.generateToken(userEntity.getUsername(), new Date());
        LOGGER.info("username：{}，token：{}", userEntity.getUsername(), token);
        response.put("code", 200);
        response.put("msg", "success");
        response.put("content", token);
        return response;
    }
}
