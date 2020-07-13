package com.chenxin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenxin.entity.CxProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 产品表 Mapper 接口
 * </p>
 *
 * @author chenxin
 * @since 2020-07-13
 */
@Mapper
public interface CxProductMapper extends BaseMapper<CxProduct> {
    CxProduct getByIdForUpdate(@Param("id") long id);
}
