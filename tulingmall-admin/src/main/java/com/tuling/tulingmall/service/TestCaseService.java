package com.tuling.tulingmall.service;

import com.tuling.tulingmall.qo.MailInfoQO;
import com.tuling.tulingmall.vo.MailInfoVO;

public interface TestCaseService {

    /**
     * 查询邮件信息
     * @param mailInfoQO
     * @return
     */
    MailInfoVO getMailInfo(MailInfoQO mailInfoQO);


    /**
     * 利用Mybatis的SqlSession设置批处理,把需要执行的sql添加到批处理队列中，统一提交事务
     */
    void testBatchInsert();
}
