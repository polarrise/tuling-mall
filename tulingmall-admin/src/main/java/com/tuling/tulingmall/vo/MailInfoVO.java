package com.tuling.tulingmall.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MailInfoVO {

    @ApiModelProperty("发件人")
    private String senderAddress;

    @ApiModelProperty("邮件标题")
    private String title;

    @ApiModelProperty("邮件正文")
    private String content;

    @ApiModelProperty("收件人列表")
    private List<MailReceiverVO> receiverAddressList;

    @ApiModelProperty("抄送人列表")
    private List<MailReceiverVO> ccReceiverAddressList;

}
