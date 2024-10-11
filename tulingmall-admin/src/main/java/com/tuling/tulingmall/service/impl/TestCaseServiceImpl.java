package com.tuling.tulingmall.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.tuling.tulingmall.annotation.MailTemplate;
import com.tuling.tulingmall.aspect.RoleContext;
import com.tuling.tulingmall.common.api.ResultCode;
import com.tuling.tulingmall.common.exception.TulingMallException;
import com.tuling.tulingmall.component.BatchInsertUtils;
import com.tuling.tulingmall.mainTemplate.AbstractMailTemplate;
import com.tuling.tulingmall.mapper.UmsAdminMapper;
import com.tuling.tulingmall.model.UmsAdmin;
import com.tuling.tulingmall.qo.MailInfoQO;
import com.tuling.tulingmall.service.Callback;
import com.tuling.tulingmall.service.TestCaseService;
import com.tuling.tulingmall.vo.MailInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
public class TestCaseServiceImpl implements TestCaseService {

    private static Map<String, AbstractMailTemplate> mailTemplateMap = new HashMap<>(16);

    @Resource
    private UmsAdminMapper adminMapper;

    @Resource
    private BatchInsertUtils batchInsertUtils;

    @Autowired
    @Qualifier("commonPool")
    private ExecutorService tulingThreadPoolExecutor;


    @EventListener
    public void initMailTemplateMap(ContextRefreshedEvent contextRefreshedEvent){
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(MailTemplate.class);
        if(CollectionUtils.isEmpty(beansWithAnnotation)){
            return;
        }
        beansWithAnnotation.forEach((key,value)->{
            String bizType = value.getClass().getAnnotation(MailTemplate.class).value();
            mailTemplateMap.put(bizType , (AbstractMailTemplate) value);
        });
    }

    @Override
    public MailInfoVO getMailInfo(MailInfoQO mailInfoQO) {
        AbstractMailTemplate abstractMailTemplate = mailTemplateMap.get(mailInfoQO.getTemplateCode());
        Optional.ofNullable(abstractMailTemplate).orElseThrow(()->new TulingMallException(ResultCode.FAILED.getCode(),"模板编号有误"));

        abstractMailTemplate.mailInfoQO = mailInfoQO;
        return abstractMailTemplate.mailTemplateMethod();
    }

    /**
     * 在MyBatis中，批处理操作是一种高效执行多条语句的方式，特别是当你需要在一个事务中插入、更新或删除多条记录时。批处理可以显著减少与数据库的交互次数，从而提高性能。
     * 执行批处理的基本步骤
     * 开启批处理模式：在获取SqlSession时，需要指定执行器（Executor）类型为ExecutorType.BATCH。
     * 执行SQL语句：执行需要批处理的SQL语句，此时语句并不会立即执行，而是被添加到批处理队列中。
     * 提交事务：调用SqlSession.commit()方法，此时MyBatis会将批处理队列中的语句一次性发送给数据库执行。
     * 处理批处理结果：提交事务后，可以通过批处理结果进行后续处理。
     */
    @Override
    public void testBatchInsert() {
        List<UmsAdmin> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UmsAdmin umsAdmin = new UmsAdmin();
            umsAdmin.setUsername("rise"+i);
            umsAdmin.setNickName("nickName"+i);
            list.add(umsAdmin);
        }
        int i = batchInsertUtils.batchInsertOrUpdateData(list, UmsAdminMapper.class, (a, b) -> b.insert(a));
        log.info("成功写入：{}条数据",i);
    }

    @Override
    public void parallelSubList() {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            list.add(i);
        }
        int totalCount = list.size();
        int pageSize = 50;
        int threadCount = totalCount % pageSize == 0 ?  totalCount / pageSize : totalCount / pageSize + 1;
        log.info("线程拆分数量:{}",threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int index = 0; index < threadCount; index++){
            List<Integer> subList = list.subList(index * pageSize, index == threadCount - 1 ? totalCount : (index + 1) * pageSize);
            tulingThreadPoolExecutor.submit(()->{
                try {
                    log.info("当前执行线程名称:{},subList集合范围：{}",Thread.currentThread().getName(), JSONUtil.toJsonStr(subList.get(0) + "-" + subList.get(subList.size()-1)));
                }catch (Exception e){
                    log.info("当前任务执行失败：{}",e.getMessage());
                }finally {
                    // 请注意，这里无论成功或者失败，必须countDown,不然会造成计数器归不了零，从而造成程序一直会阻塞在await方法
                    countDownLatch.countDown();
                }
            });
        }

        try {
            countDownLatch.await();
        }catch (InterruptedException e){
            log.info("任务出错了,中断异常："+e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void toPay1() {
        syncPayCallBack(new Callback() {
            @Override
            public void onCallback() {
                log.info("支付回调处理完成");
            }
        });
    }

    @Override
    public void toPay2() {

    }

    private void syncPayCallBack(Callback callback) {
        log.info("支付逻辑处理完成...同步执行支付回调");
        callback.onCallback();
    }

    private void asyncPayCallBack(Callback callback) throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("执行支付逻辑处理...");
            try {
                Thread.sleep(2000); // 模拟耗时操作
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            callback.onCallback(); // 异步操作完成后回调
        });
        // 等待异步任务完成
        future.join(); // 使用 join() 阻塞主线程，直到异步任务完成
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 1.接口同步回调
        TestCaseServiceImpl tc = new TestCaseServiceImpl();
        Callback callback = new CallbackImpl();
        //tc.syncPayCallBack(callback);
        //tc.syncPayCallBack(new Callback() {
        //    @Override
        //    public void onCallback() {
        //        log.info("支付回调处理完成");
        //    }
        //});

        // 2. lambda表达式同步回调
        //tc.syncPayCallBack(()->log.info("支付回调处理完成"));

        // 3. lambda表达式异步回调
        tc.asyncPayCallBack(()-> log.info("支付异步回调处理完成,线程名称:{}",Thread.currentThread().getName()));

        // 异步任务
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000); // 模拟耗时操作
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "异步任务完成";
        }).thenAccept(result -> System.out.println("回调收到结果: " + result)); // 回调处理结果

       log.info("主线程继续运行");

        // 等待异步任务完成
        future.join(); // 使用 join() 阻塞主线程，直到异步任务完成
    }

    @Override
    public void testTransmittableThreadLocal() {
        // 主线程获取角色信息
        Boolean hasRole = RoleContext.getHasRole();
        log.info("主线程: 获取角色信息，是否拥有 'DIGITAL_ANGEL' 角色: " + hasRole);

        // 提交多个任务到自定义线程池
        for (int i = 0; i < 10; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    // 增加任务的执行时间，模拟长时间运行的任务
                    TimeUnit.MILLISECONDS.sleep((long) (Math.random() * 1000));
                    // 在子线程中获取角色信息
                    Boolean childThreadHasRole = RoleContext.getHasRole();
                    log.info("子线程: 获取角色信息，是否拥有 'DIGITAL_ANGEL' 角色: " + childThreadHasRole);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, TtlExecutors.getTtlExecutorService(tulingThreadPoolExecutor));
        }
    }
}
