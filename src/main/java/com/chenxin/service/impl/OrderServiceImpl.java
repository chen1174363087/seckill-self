package com.chenxin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenxin.common.CommConfig;
import com.chenxin.dao.OrderMapper;
import com.chenxin.entity.CxOrder;
import com.chenxin.entity.CxProduct;
import com.chenxin.service.ICxProductService;
import com.chenxin.service.IOrderService;
import com.chenxin.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.List;


/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author chenxin
 * @since 2020-07-03
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, CxOrder> implements IOrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    @Qualifier(value = "jedisTemplate")
    private Jedis jedis;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ICxProductService productService;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    /**
     * 秒杀创建订单逻辑
     *
     * @param cxOrder
     */
    @Override
    @Transactional
    public void createOrder(CxOrder cxOrder) {
        /**
         * redis事物没有原子性，这种查库存-创建下单消息-减库存解决不了超卖问题，
         * 还是用mysql：查库存-创建订单-减库存在一个事物中
         */
//        //库存
//        String stockValue = redisUtils.get(CommConfig.SEC_KILL_STOCK);
//        LOGGER.info("【createOrder】秒杀库存是：{}", stockValue);
//        int stock = Integer.valueOf(stockValue);
//        if (StringUtils.isEmpty(stock)) {
//            LOGGER.error("【createOrder】{}", msg);
//            return msg;
//        }
//
//        if (stock <= 0) {
//            msg = "该轮已售罄，敬请期待！";
//            LOGGER.error("【createOrder】{}", msg);
//            return msg;
//        }
//        //减库存
//        redisUtils.set(CommConfig.SEC_KILL_STOCK, String.valueOf(stock - 1));

        //库存（排它锁）
        CxProduct product = productService.getByIdForUpdate(CommConfig.SEC_KILL_PRODUCT_ID);
        LOGGER.info("秒杀库存：{}", product);
        if (product == null) {
            return;
        }

        int stock = product.getStock();
        if (stock < 1) {
            return;
        }

        //创建订单
        this.saveOrUpdate(cxOrder);

        //减库存
        product.setStock(--stock);
        productService.updateById(product);
        // 更新一下redis库存
        redisUtils.set(CommConfig.SEC_KILL_STOCK, String.valueOf(stock));
    }

    @Override
    public int insertBatchSelf(List<CxOrder> entities) throws Exception {
        return orderMapper.insertBatchSelf(entities);
    }
}
