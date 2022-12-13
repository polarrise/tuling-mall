package com.tuling.tulingmall.ordercurr.controller;

import com.tuling.tulingmall.common.api.CommonResult;
import com.tuling.tulingmall.common.exception.BusinessException;
import com.tuling.tulingmall.ordercurr.component.rocketmq.OrderMessageSender;
import com.tuling.tulingmall.ordercurr.domain.OrderMessage;
import com.tuling.tulingmall.ordercurr.domain.SecKillOrderParam;
import com.tuling.tulingmall.ordercurr.model.OmsOrder;
import com.tuling.tulingmall.ordercurr.model.OmsOrderItem;
import com.tuling.tulingmall.ordercurr.model.UmsMemberReceiveAddress;
import com.tuling.tulingmall.ordercurr.service.SecKillOrderService;
import com.tuling.tulingmall.ordercurr.service.impl.OrderConstant;
import com.tuling.tulingmall.ordercurr.service.impl.SecKillOrderServiceImpl;
import com.tuling.tulingmall.ordercurr.util.RocksDBUtil;
import com.tuling.tulingmall.promotion.domain.FlashPromotionProduct;
import io.swagger.annotations.Api;

import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.rocksdb.RocksDBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 秒杀订单管理Controller
 */
@Slf4j
@Controller
@Api(tags = "SeckillOrderController",description = "秒杀订单管理")
@RequestMapping("/seckillOrder")
public class SecKillOrderController {

    @Autowired
    private OrderMessageSender orderMessageSender;

    @Autowired
    private SecKillOrderService secKillOrderService;

    @ApiOperation("生成秒杀订单")
    @RequestMapping(value = "/generateOrder",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult generateOrder(@RequestBody SecKillOrderParam secKillOrderParam,
                                      @RequestHeader("memberId") Long memberId) throws BusinessException {
        return secKillOrderService.generateSecKillOrder(secKillOrderParam,memberId,null,1);
    }

    @ApiOperation("查询秒杀订单是否生成")
    @RequestMapping(value = "/checkOrder",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult checkOrder(@RequestParam("orderId") Long orderId) throws BusinessException {
        return secKillOrderService.checkOrder(orderId);
    }

    @ApiOperation("压测RockctMQ")
    @RequestMapping(value = "/pressureMQ",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult pressureMQ(@RequestBody SecKillOrderParam secKillOrderParam,
                                   @RequestHeader("memberId") Long memberId) throws BusinessException {
        Long productId = secKillOrderParam.getProductId();

        Long orderId = secKillOrderParam.getOrderId();
        Long orderItemId = secKillOrderParam.getOrderItemId();

        //【2】 从缓存中获取产品信息
        FlashPromotionProduct product = secKillOrderService.getProductInfo(secKillOrderParam.getFlashPromotionId(),productId);

        //准备创建订单
        //生成下单商品信息
        String orderSn = orderId.toString();
        OmsOrderItem orderItem = new OmsOrderItem();
        orderItem.setId(orderItemId);
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setProductPic(product.getPic());
        orderItem.setProductBrand(product.getBrandName());
        orderItem.setProductSn(product.getProductSn());
        orderItem.setProductPrice(product.getFlashPromotionPrice());
        orderItem.setProductQuantity(1);
        orderItem.setProductCategoryId(product.getProductCategoryId());
        orderItem.setPromotionAmount(product.getPrice().subtract(product.getFlashPromotionPrice()));
        orderItem.setPromotionName("秒杀特惠活动");
        orderItem.setGiftIntegration(product.getGiftPoint());
        orderItem.setGiftGrowth(product.getGiftGrowth());
        orderItem.setCouponAmount(new BigDecimal(0));
        orderItem.setIntegrationAmount(new BigDecimal(0));
        orderItem.setPromotionAmount(new BigDecimal(0));
        //支付金额
        BigDecimal payAmount = product.getFlashPromotionPrice().multiply(new BigDecimal(1));
        //优惠价格
        orderItem.setRealAmount(payAmount);
        orderItem.setOrderSn(orderSn);

        OmsOrder order = new OmsOrder();
        order.setId(orderId);
        order.setDiscountAmount(product.getPrice().subtract(product.getFlashPromotionPrice()));//折扣金额
        order.setFreightAmount(new BigDecimal(0));//运费金额
        order.setPromotionAmount(new BigDecimal(0));
        order.setPromotionInfo("秒杀特惠活动");
        order.setTotalAmount(payAmount);
        order.setIntegration(0);
        order.setIntegrationAmount(new BigDecimal(0));
        order.setPayAmount(payAmount);
        order.setMemberId(memberId);
        order.setMemberUsername(null);
        order.setCreateTime(new Date());
        //设置支付方式：0->未支付,1->支付宝,2->微信
        order.setPayType(secKillOrderParam.getPayType());
        //设置支付方式：0->PC订单,1->APP订单,2->小程序
        order.setSourceType(0);
        //订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
        order.setStatus(OrderConstant.ORDER_STATUS_UNPAY);
        //订单类型：0->正常订单；1->秒杀订单
        order.setOrderType(OrderConstant.ORDER_TYPE_SECKILL);
        //用户收货信息
        UmsMemberReceiveAddress address = secKillOrderParam.getMemberReceiveAddress();
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverPostCode(address.getPostCode());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverDetailAddress(address.getDetailAddress());
        //0->未确认；1->已确认
        order.setConfirmStatus(OrderConstant.CONFIRM_STATUS_NO);
        order.setDeleteStatus(OrderConstant.DELETE_STATUS_NO);
        //计算赠送积分
        order.setIntegration(product.getGiftPoint());
        //计算赠送成长值
        order.setGrowth(product.getGiftGrowth());
        //生成订单号-理论上唯一
        order.setOrderSn(orderSn);
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setOrder(new OmsOrder());
        orderMessage.setOrderItem(new OmsOrderItem());
        orderMessage.setFlashPromotionRelationId(3L);
        orderMessage.setFlashPromotionLimit(1000);
        orderMessage.setFlashPromotionEndDate(new Date());
        try {
            if(orderMessageSender.sendPressureMQMsg(orderMessage)){
               return  CommonResult.success("success");
            }else{
                return  CommonResult.failed();
            }
        } catch (Exception e) {
            return CommonResult.failed("exception",e.getMessage());
        }
    }

}
