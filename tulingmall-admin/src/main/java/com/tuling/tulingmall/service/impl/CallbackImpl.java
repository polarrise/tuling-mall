package com.tuling.tulingmall.service.impl;

import com.tuling.tulingmall.service.Callback;
import lombok.extern.slf4j.Slf4j;

/**
 * @author WangJinbiao
 * @date 2024/10/08 21：25
 */
@Slf4j
public class CallbackImpl implements Callback {

    @Override
    public void onCallback() {
        log.info("回调方法被调用");
    }

}
