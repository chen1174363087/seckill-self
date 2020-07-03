package com.chenxin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenxin.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author chenxin
 * @since 2020-07-03
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
