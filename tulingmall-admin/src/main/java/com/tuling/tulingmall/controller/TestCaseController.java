package com.tuling.tulingmall.controller;


import com.tuling.tulingmall.annotation.ApiAnnotation;
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
}
