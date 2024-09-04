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
}
