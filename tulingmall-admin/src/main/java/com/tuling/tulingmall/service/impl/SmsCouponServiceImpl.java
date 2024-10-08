package com.tuling.tulingmall.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.tuling.tulingmall.dao.SmsCouponProductCategoryRelationDao;
import com.tuling.tulingmall.dao.SmsCouponProductRelationDao;
import com.tuling.tulingmall.dto.SmsCouponParam;
import com.tuling.tulingmall.mapper.SmsCouponMapper;
import com.tuling.tulingmall.mapper.SmsCouponProductCategoryRelationMapper;
import com.tuling.tulingmall.mapper.SmsCouponProductRelationMapper;
import com.tuling.tulingmall.model.SmsCoupon;
import com.tuling.tulingmall.model.SmsCouponProductCategoryRelation;
import com.tuling.tulingmall.model.SmsCouponProductRelation;
import com.tuling.tulingmall.service.SmsCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 优惠券管理Service实现类
 * Created on 2018/8/28.
 */
@Service
@DS("promotion")
public class SmsCouponServiceImpl implements SmsCouponService {
    @Autowired
    private SmsCouponMapper couponMapper;
    @Autowired
    private SmsCouponProductRelationMapper productRelationMapper;
    @Autowired
    private SmsCouponProductCategoryRelationMapper productCategoryRelationMapper;
    @Autowired
    private SmsCouponProductRelationDao productRelationDao;
    @Autowired
    private SmsCouponProductCategoryRelationDao productCategoryRelationDao;
    @Override
    public int create(SmsCouponParam couponParam) {
        couponParam.setCount(couponParam.getPublishCount());
        couponParam.setUseCount(0);
        couponParam.setReceiveCount(0);
        //插入优惠券表
        int count = couponMapper.insert(couponParam);
        //插入优惠券和商品关系表
        if(couponParam.getUseType().equals(2)){
            for(SmsCouponProductRelation productRelation:couponParam.getProductRelationList()){
                productRelation.setCouponId(couponParam.getId());
            }
            productRelationDao.insertList(couponParam.getProductRelationList());
        }
        //插入优惠券和商品分类关系表
        if(couponParam.getUseType().equals(1)){
            for (SmsCouponProductCategoryRelation couponProductCategoryRelation : couponParam.getProductCategoryRelationList()) {
                couponProductCategoryRelation.setCouponId(couponParam.getId());
            }
            productCategoryRelationDao.insertList(couponParam.getProductCategoryRelationList());
        }
        return count;
    }

    @Override
    public int delete(Long id) {
        //删除优惠券
        int count = couponMapper.deleteById(id);
        //删除商品关联
        deleteProductRelation(id);
        //删除商品分类关联
        deleteProductCategoryRelation(id);
        return count;
    }

    private void deleteProductCategoryRelation(Long id) {
        UpdateWrapper<SmsCouponProductCategoryRelation> wrapper = new UpdateWrapper<>();
        wrapper.eq("coupon_id",id);
        productCategoryRelationMapper.delete(wrapper);
    }

    private void deleteProductRelation(Long id) {
        UpdateWrapper<SmsCouponProductRelation> wrapper = new UpdateWrapper<>();
        wrapper.eq("coupon_id",id);
        productRelationMapper.delete(wrapper);
    }

    @Override
    public int update(Long id, SmsCouponParam couponParam) {
        couponParam.setId(id);
        int count =couponMapper.updateById(couponParam);
        //删除后插入优惠券和商品关系表
        if(couponParam.getUseType().equals(2)){
            for(SmsCouponProductRelation productRelation:couponParam.getProductRelationList()){
                productRelation.setCouponId(couponParam.getId());
            }
            deleteProductRelation(id);
            productRelationDao.insertList(couponParam.getProductRelationList());
        }
        //删除后插入优惠券和商品分类关系表
        if(couponParam.getUseType().equals(1)){
            for (SmsCouponProductCategoryRelation couponProductCategoryRelation : couponParam.getProductCategoryRelationList()) {
                couponProductCategoryRelation.setCouponId(couponParam.getId());
            }
            deleteProductCategoryRelation(id);
            productCategoryRelationDao.insertList(couponParam.getProductCategoryRelationList());
        }
        return count;
    }

    @Override
    public List<SmsCoupon> list(String name, Integer type, Integer pageSize, Integer pageNum) {
        QueryWrapper<SmsCoupon> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(name)){
            wrapper.like("name","%"+name+"%");
        }
        if(type!=null){
            wrapper.like("type",type);
        }
        PageHelper.startPage(pageNum,pageSize);
        return couponMapper.selectList(wrapper);
    }

    @Override
    public SmsCouponParam getItem(Long id) {
        return couponMapper.getItem(id);
    }
}
