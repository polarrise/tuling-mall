package com.tuling.tulingmall.feignapi.sms;

import com.tuling.tulingmall.common.api.CommonResult;
import com.tuling.tulingmall.model.SmsCouponHistory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @auth roykingw
 */
@FeignClient(name = "tulingmall-promotion",path = "/coupon")
public interface SmsPromotionFeignApi {

    @RequestMapping(value = "/add/{couponId}", method = RequestMethod.POST)
    @ResponseBody
    CommonResult userActivelyGet(@PathVariable("couponId") Long couponId);

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    CommonResult<List<SmsCouponHistory>> listUserCoupons(
            @RequestParam(value = "useStatus", required = false) Integer useStatus);
}
