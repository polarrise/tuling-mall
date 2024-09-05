package com.tuling.tulingmall.service.impl;

import com.tuling.tulingmall.annotation.MailTemplate;
import com.tuling.tulingmall.common.api.ResultCode;
import com.tuling.tulingmall.common.exception.TulingMallException;
import com.tuling.tulingmall.component.BatchInsertUtils;
import com.tuling.tulingmall.mainTemplate.AbstractMailTemplate;
import com.tuling.tulingmall.mapper.UmsAdminMapper;
import com.tuling.tulingmall.model.UmsAdmin;
import com.tuling.tulingmall.qo.MailInfoQO;
import com.tuling.tulingmall.service.TestCaseService;
import com.tuling.tulingmall.vo.MailInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class TestCaseServiceImpl implements TestCaseService {

    private static Map<String, AbstractMailTemplate> mailTemplateMap = new HashMap<>(16);

    @Resource
    private UmsAdminMapper adminMapper;

    @Resource
    private BatchInsertUtils batchInsertUtils;


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
}
