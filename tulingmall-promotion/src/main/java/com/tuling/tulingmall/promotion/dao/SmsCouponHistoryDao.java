package com.tuling.tulingmall.promotion.dao;

import com.tuling.tulingmall.model.SmsCoupon;
import com.tuling.tulingmall.promotion.domain.SmsCouponHistoryDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员优惠券领取历史自定义Dao
 */
public interface SmsCouponHistoryDao {
    List<SmsCouponHistoryDetail> getDetailList(@Param("memberId") Long memberId);

    List<SmsCoupon> queryUserCoupon(@Param("memberId") Long memberId, @Param("useStatus") Integer useStatus);
}
