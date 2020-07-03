package com.chenxin.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chenxin.dao.UserMapper;
import com.chenxin.entity.User;
import com.chenxin.enums.Constants;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther chenxin4
 * @Date 2020/7/3
 * Description
 */
@Component
@Scope("singleton")
public class JwtUtils {
    @Autowired
    private UserMapper userRepository;

    @Value("${jwt.expiretime}")
    private long EXPIRATION_TIME;
    static final String SECRET = "ThisIsASecret";
    static final String TOKEN_PREFIX = "Bearer";
    static final String HEADER_STRING = "Authorization";

    public String generateToken(String username, Date generateTime) {
        HashMap<String, Object> map = new HashMap<>();
        //可以把任何安全的数据放到map里面
        map.put("username", username);
        map.put("generateTime", generateTime);
        String jwt = Jwts.builder()
                .setClaims(map)
                .setExpiration(new Date(generateTime.getTime() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        return jwt;
    }

    /**
     * @param token
     * @return
     */
    public Map<String, Object> validateToken(String token) {
        Map<String, Object> resp = new HashMap<>();
        if (token != null) {
            // 解析token
            try {
                Map<String, Object> body = Jwts.parser()
                        .setSigningKey(SECRET)
                        .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                        .getBody();
                String username = (String) (body.get("username"));
                Date generateTime = new Date((Long) body.get("generateTime"));

                if (username == null || username.isEmpty()) {
                    resp.put("ERR_MSG", Constants.ERR_MSG_USERNAME_EMPTY);
                    return resp;
                }
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("username", username);
                User userEntity = userRepository.selectOne(queryWrapper);
                if (userEntity == null) {
                    resp.put("ERR_MSG", Constants.ERR_MSG_NOT_A_ACOUNT);
                    return resp;
                }
                //账号在别处登录
//                if (userRepository.selectOne(username).getLastLoginTime().after(generateTime)) {
//                    resp.put("ERR_MSG", Constants.ERR_MSG_LOGIN_DOU);
//                    return resp;
//                }
                resp.put("username", username);
                resp.put("generateTime", generateTime);
                return resp;
            } catch (SignatureException | MalformedJwtException e) {
                // TODO: handle exception
                // don't trust the JWT!
                // jwt 解析错误
                resp.put("ERR_MSG", Constants.ERR_MSG_TOKEN_ERR);
                return resp;
            } catch (ExpiredJwtException e) {
                // TODO: handle exception
                // jwt 已经过期，在设置jwt的时候如果设置了过期时间，这里会自动判断jwt是否已经过期，如果过期则会抛出这个异常，我们可以抓住这个异常并作相关处理。
                resp.put("ERR_MSG", Constants.ERR_MSG_TOKEN_EXP);
                return resp;
            }
        } else {
            resp.put("ERR_MSG", Constants.ERR_MSG_TOKEN_EMPTY);
            return resp;
        }
    }
}
