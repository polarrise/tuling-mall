package com.tuling.tulingmall.service.impl;

import com.tuling.tulingmall.annotation.MailTemplate;
import com.tuling.tulingmall.common.api.ResultCode;
import com.tuling.tulingmall.common.exception.TulingMallException;
import com.tuling.tulingmall.mainTemplate.AbstractMailTemplate;
import com.tuling.tulingmall.qo.MailInfoQO;
import com.tuling.tulingmall.service.TestCaseService;
import com.tuling.tulingmall.vo.MailInfoVO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TestCaseServiceImpl implements TestCaseService {

    private static Map<String, AbstractMailTemplate> mailTemplateMap = new HashMap<>(16);

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
}
