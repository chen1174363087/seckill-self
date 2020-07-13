package com.chenxin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenxin.dao.CxProductMapper;
import com.chenxin.entity.CxProduct;
import com.chenxin.service.ICxProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 产品表 服务实现类
 * </p>
 *
 * @author chenxin
 * @since 2020-07-13
 */
@Service
public class CxProductServiceImpl extends ServiceImpl<CxProductMapper, CxProduct> implements ICxProductService {
    @Autowired
    private CxProductMapper productMapper;

    @Override
    public CxProduct getByIdForUpdate(long id) {
        return productMapper.getByIdForUpdate(id);
    }
}
