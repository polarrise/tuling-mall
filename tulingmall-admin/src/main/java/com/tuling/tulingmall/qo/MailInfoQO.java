package com.tuling.tulingmall.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailInfoQO {

    @ApiModelProperty("模板编号")
    private String templateCode;
}
