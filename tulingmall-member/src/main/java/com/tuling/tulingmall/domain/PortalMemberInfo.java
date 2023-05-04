package com.tuling.tulingmall.domain;

import com.tuling.tulingmall.model.UmsMember;
import com.tuling.tulingmall.model.UmsMemberLevel;
import lombok.Data;

/**
 * @author ：图灵学院
 * @version: V1.0
 * @slogan: 天下风云出我辈，一入代码岁月催
 * @description:
 **/
@Data
public class PortalMemberInfo extends UmsMember {
    private UmsMemberLevel umsMemberLevel;
}
