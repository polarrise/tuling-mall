package com.tuling.tulingmall.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MailReceiverVO {

    @ApiModelProperty("人员id")
    private Integer empId;

    @ApiModelProperty("姓名")
    private String name;
}
