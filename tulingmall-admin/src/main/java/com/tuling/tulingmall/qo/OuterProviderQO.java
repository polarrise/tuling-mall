package com.tuling.tulingmall.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OuterProviderQO {

    @NotEmpty
    @ApiModelProperty(value = "系统编码")
    private String systemCode;
}
