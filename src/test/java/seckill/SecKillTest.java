package seckill;

import com.chenxin.Application;
import com.chenxin.common.CommConfig;
import com.chenxin.entity.CxOrder;
import com.chenxin.mq.seckill.Producer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Auther chenxin4
 * @Date 2020/7/13
 * Description
 */
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class SecKillTest {
    @Autowired
    private Producer producer;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    CountDownLatch countDownLatch = new CountDownLatch(20);

    @Test
    public void testSeckill() {
        int i = 0;
        while (i < 20) {
            final int j = i;
            executorService.submit(() -> {
                CxOrder cxOrder = new CxOrder();
                cxOrder.setUsername("seckill_" + j);
                cxOrder.setAddress("地址：seckill_" + j);
                cxOrder.setProductId(CommConfig.SEC_KILL_PRODUCT_ID);
                cxOrder.setTelphone("_123456_" + j);

                try {
                    countDownLatch.await();
                    producer.buildCreateOrder(cxOrder);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            i++;
            countDownLatch.countDown();
        }
    }
}
