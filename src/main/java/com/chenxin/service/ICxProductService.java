package com.chenxin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chenxin.entity.CxProduct;

/**
 * <p>
 * 产品表 服务类
 * </p>
 *
 * @author chenxin
 * @since 2020-07-13
 */
public interface ICxProductService extends IService<CxProduct> {
    CxProduct getByIdForUpdate(long id);
}
