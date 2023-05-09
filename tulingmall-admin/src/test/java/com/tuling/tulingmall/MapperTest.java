package com.tuling.tulingmall;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tuling.tulingmall.mapper.PmsProductCategoryMapper;
import com.tuling.tulingmall.mapper.SmsCouponMapper;
import com.tuling.tulingmall.mapper.UmsAdminMapper;
import com.tuling.tulingmall.model.PmsProductCategory;
import com.tuling.tulingmall.model.SmsCoupon;
import com.tuling.tulingmall.model.UmsAdmin;
import com.tuling.tulingmall.service.PmsProductCategoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * @author roy
 * @desc
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MapperTest {

    @Autowired
    private UmsAdminMapper adminMapper;

    @Autowired
    private SmsCouponMapper smsCouponMapper;

    @Autowired
    private PmsProductCategoryMapper productCategoryMapper;

    @Autowired
    private PmsProductCategoryService PmsProductCategoryService;

    @Test
    public void getUser(){
        List<UmsAdmin> umsAdmins = adminMapper.selectList(null);
        umsAdmins.forEach(System.out::println);
    }

    @Test
    public void getCoupon(){
        List<SmsCoupon> smsCoupons = smsCouponMapper.selectList(null);
        smsCoupons.forEach(System.out::println);
        System.out.println(smsCouponMapper.deleteById(24));
    }

    @Test
    public void pcmTest(){
//        PmsProductCategory result = productCategoryMapper.selectById(1);
//        System.out.println(result);
//        int res = productCategoryMapper.updateById(result);
//        System.out.println(res);

//        int res = PmsProductCategoryService.updateNavStatus(Arrays.asList(1654744088165003265L),1);
//        System.out.println(res);
        smsCouponMapper.selectList(null).forEach(System.out::println);
    }
}
