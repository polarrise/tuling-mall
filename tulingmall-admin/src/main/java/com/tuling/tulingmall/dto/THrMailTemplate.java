package com.tuling.tulingmall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class THrMailTemplate {

    @ApiModelProperty("模板编号")
    private String templateCode;

    @ApiModelProperty("模板名称")
    private String templateName;

    @ApiModelProperty("模板内容")
    private String templateContent;

}
