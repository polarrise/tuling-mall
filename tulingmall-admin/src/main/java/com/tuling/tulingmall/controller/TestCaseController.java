package com.tuling.tulingmall.controller;


import com.tuling.tulingmall.annotation.ApiAnnotation;
import com.tuling.tulingmall.annotation.DigitalAngel;
import com.tuling.tulingmall.common.api.CommonResult;
import com.tuling.tulingmall.qo.MailInfoQO;
import com.tuling.tulingmall.qo.OuterProviderQO;
import com.tuling.tulingmall.service.TestCaseService;
import com.tuling.tulingmall.vo.MailInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "TestCaseController", description = "测试用例Controller")
@RequestMapping("/testCase")
public class TestCaseController {

    @Autowired
    private TestCaseService testCaseService;

    @ApiAnnotation
    @ApiOperation("测试api权限")
    @RequestMapping(value = "/testApiAuth", method = RequestMethod.POST)
    public CommonResult<String> testApiAuth(@RequestBody OuterProviderQO outerProviderQO) {
        return CommonResult.success("测试成功");
    }

    @ApiOperation("测试获取邮件信息")
    @RequestMapping(value = "/getMailInfo", method = RequestMethod.POST)
    public CommonResult<MailInfoVO> getMailInfo(@RequestBody MailInfoQO mailInfoQO) {
        return CommonResult.success(testCaseService.getMailInfo(mailInfoQO));
    }

    @ApiOperation("测试批处理")
    @RequestMapping(value = "/testBatchInsert", method = RequestMethod.POST)
    public CommonResult<String> testBatchInsert() {
        testCaseService.testBatchInsert();
        return CommonResult.success("成功");
    }

    @ApiOperation("集合拆分-多线程并行处理")
    @RequestMapping(value = "/parallelSubList", method = RequestMethod.POST)
    public CommonResult<String> parallelSubList() {
        testCaseService.parallelSubList();
        return CommonResult.success("成功");
    }

    @ApiOperation("理解回调思想-同步回调")
    @RequestMapping(value = "/toPay1", method = RequestMethod.POST)
    public CommonResult<String> syncCallBack() {
        testCaseService.toPay1();
        return CommonResult.success("成功");
    }

    @ApiOperation("理解回调思想-异步回调")
    @RequestMapping(value = "/toPay2", method = RequestMethod.POST)
    public CommonResult<String> toPay2() {
        testCaseService.toPay2();
        return CommonResult.success("成功");
    }

    @ApiOperation("测试-TransmittableThreadLocal")
    @DigitalAngel(memo = "标注该接口需要校验是否存在DIGITAL_ANGEL角色")
    @RequestMapping(value = "/testTransmittableThreadLocal", method = RequestMethod.POST)
    public CommonResult<String> testTransmittableThreadLocal() {
        testCaseService.testTransmittableThreadLocal();
        return CommonResult.success("成功");
    }
}
