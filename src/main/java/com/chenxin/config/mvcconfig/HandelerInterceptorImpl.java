package com.chenxin.config.mvcconfig;

import com.chenxin.util.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Configuration
@Qualifier(value = "interceptor")
public class HandelerInterceptorImpl implements HandlerInterceptor {

    private final static Logger LOGGER = LoggerFactory.getLogger(HandelerInterceptorImpl.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        LOGGER.info("HandelerInterceptorImpl preHandle() method start");
        String token = httpServletRequest.getHeader("token");
        Map<String, Object> result = jwtUtils.validateToken(token);
        LOGGER.info("JWT token 解析结果：{}", result.toString());
        if (StringUtils.isEmpty(result.get("username"))) {
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
