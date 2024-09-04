package com.tuling.tulingmall.mainTemplate;

import cn.hutool.core.bean.BeanUtil;
import com.tuling.tulingmall.dto.THrMailTemplate;
import com.tuling.tulingmall.qo.MailInfoQO;
import com.tuling.tulingmall.vo.MailInfoVO;
import com.tuling.tulingmall.vo.MailReceiverVO;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractMailTemplate <T>{

    @ApiModelProperty("邮件信息查询QO")
    public MailInfoQO mailInfoQO;

    /**
     * 查询邮件发送人
     * @return
     */
    protected abstract String getMailSenderAddress();

    /**
     * 查询邮件模板
     * @return
     */
    protected abstract THrMailTemplate getMailTemplate();

    /**
     * 查询邮件变量
     * @return
     */
    protected abstract<T> T getMailContentVar();

    /**
     * 查询邮件收件人
     * @return
     */
    protected abstract List<MailReceiverVO> getMailReceiverList();

    /**
     * 查询邮件抄送人
     * @return
     */
    protected abstract List<MailReceiverVO> getMailCCReceiverList();


    /**
     * 替换邮件模板变量
     * @return
     */
    private String replaceMailTemplateVar(){
        Map<String, Object> mailTemplateVarMap = BeanUtil.beanToMap(getMailContentVar());
        StringBuilder mailContentResult = new StringBuilder(getMailTemplate().getTemplateContent());
        Set<Map.Entry<String, Object>> entries = mailTemplateVarMap.entrySet();
        for(Map.Entry<String, Object> entry: mailTemplateVarMap.entrySet()){
            replaceMailContentByEntry(mailContentResult,entry);
        }
        return mailContentResult.toString();
    }

    /**
     * 根据键值对替换邮件内容
     * @param mailContentResult
     * @param entry
     */
    private void replaceMailContentByEntry(StringBuilder mailContentResult , Map.Entry<String, Object> entry){
        int startIndex = mailContentResult.indexOf(entry.getKey());
        if(startIndex == -1){
            return;
        }
        int endIndex = startIndex + entry.getKey().length();
        mailContentResult.replace(startIndex,endIndex, StringUtils.isEmpty(entry.getKey()) ? "/" :entry.getValue().toString());
        replaceMailContentByEntry(mailContentResult,entry);
    }

    /**
     * 封装邮件模板：为了防止恶意的操作，一般模板方法前面会加上final关键字，不允许被覆写
     * @return
     */
    public final MailInfoVO mailTemplateMethod(){
        return new MailInfoVO().setSenderAddress(getMailSenderAddress())
                               .setTitle(getMailTemplate().getTemplateName())
                               .setContent(replaceMailTemplateVar())
                               .setReceiverAddressList(getMailReceiverList())
                               .setCcReceiverAddressList(getMailCCReceiverList());
    }

}
