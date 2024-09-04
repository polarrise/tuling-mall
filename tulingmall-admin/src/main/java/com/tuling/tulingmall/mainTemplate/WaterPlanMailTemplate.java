package com.tuling.tulingmall.mainTemplate;

import com.tuling.tulingmall.annotation.MailTemplate;
import com.tuling.tulingmall.dto.THrMailTemplate;
import com.tuling.tulingmall.dto.WaterMailTemplateDTO;
import com.tuling.tulingmall.vo.MailReceiverVO;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@MailTemplate(value = "WATER_PLAN_MAIL_TEMPLATE"  , name="活水计划邮件模板")
@Scope("prototype")
public class WaterPlanMailTemplate<T> extends AbstractMailTemplate<T>{

    // private CadreBoardPostPersonEntity waterPerson;

    @Override
    protected String getMailSenderAddress() {
        return "rise@163.com";
    }

    @Override
    protected THrMailTemplate getMailTemplate() {
        return new THrMailTemplate().setTemplateName("活水计划邮件发送主题").setTemplateContent("name总您好!您的年龄:age,您的邮箱：mail");
    }

    @Override
    protected <T> T getMailContentVar() {
        return (T) new WaterMailTemplateDTO().setName("jinbiao").setAge(25).setMail("jinbiao666@163.com");
    }

    @Override
    protected List<MailReceiverVO> getMailReceiverList() {
        return Collections.singletonList(new MailReceiverVO(1,"张三"));
    }

    @Override
    protected List<MailReceiverVO> getMailCCReceiverList() {
        return Collections.singletonList(new MailReceiverVO(2,"李四"));
    }
}

/**
 * 如果需要直接new具体的邮件模板对象todo:
  1.定义一个静态成员变量实现
  public static WaterPlanMailTemplate waterPlanMailTemplate;   然后通过这个静态成员变量去调具体的mapper or service
  2.定义初始化前方法
  @PostConstruct
  public void init(){
     waterPlanMailTemplate = this;
  }
  3.直接new的对象：WaterPlanMailTemplate waterPlanMailTemplate = new WaterPlanMailTemplate(mailInfoQO)
  waterPlanMailTemplate对象里面的mapper接口实现都会为空，需要在初始化前完成对waterPlanMailTemplate对象的注入，然后通过waterPlanMailTemplate对象调用

 这里都换成了策略模式：从Spring容器里面拿bean，
  默认单例bean:每次拿到之后需要把这个bean的私有成员变量清空，不然debug看到永远是上次的查询的waterPerson对象
  如果把bean改成原型bean就可以了(加@Scope("prototype")注解)。
 本质：new出来的不受容器管理，没有进行属性填充,所以调用的时候会出错!!!
 */
