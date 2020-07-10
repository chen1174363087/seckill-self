package com.chenxin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chenxin.dto.Response;
import com.chenxin.entity.CxUser;
import com.chenxin.service.IUserService;
import com.chenxin.util.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public Response login(@RequestBody CxUser cxUserEntity) {
        Response response = new Response();
        if (cxUserEntity == null || StringUtils.isEmpty(cxUserEntity.getUsername())) {
            LOGGER.error("用户名为空");
            response.put("code", 200);
            response.put("msg", "用户名为空");
            return response;
        }

        QueryWrapper<CxUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", cxUserEntity.getUsername());
        CxUser cxUserEntity1 = iUserService.getOne(queryWrapper);
        if (cxUserEntity1 == null) {
            LOGGER.error("用户不存在");
            response.put("code", 200);
            response.put("msg", "用户不存在");
            return response;
        }

        if (!cxUserEntity.getPassword().equals(cxUserEntity1.getPassword())) {
            LOGGER.error("密码错误");
            response.put("code", 200);
            response.put("msg", "密码错误");
            return response;
        }

        String token = jwtUtils.generateToken(cxUserEntity.getUsername(), new Date());
        LOGGER.info("username：{}，token：{}", cxUserEntity.getUsername(), token);
        response.put("code", 200);
        response.put("msg", "success");
        response.put("content", token);
        return response;
    }
}
