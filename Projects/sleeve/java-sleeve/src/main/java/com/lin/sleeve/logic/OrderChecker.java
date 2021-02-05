package com.lin.sleeve.logic;

import com.lin.sleeve.bo.SkuOrderBO;
import com.lin.sleeve.dto.OrderDTO;
import com.lin.sleeve.dto.SkuInfoDTO;
import com.lin.sleeve.exception.ExceptionCodes;
import com.lin.sleeve.exception.http.ParameterException;
import com.lin.sleeve.model.OrderSku;
import com.lin.sleeve.model.Sku;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/4 15:06
 */
public class OrderChecker {

    private OrderDTO orderDTO;
    private List<Sku> serverSkuList;
    private CouponChecker couponChecker;
    private Integer maxSkuLimit;

    @Getter
    private List<OrderSku> orderSkuList = new ArrayList<>();

    public OrderChecker(
            OrderDTO orderDTO,
            List<Sku> serverSkuList,
            CouponChecker couponChecker,
            Integer maxSkuLimit
    ) {
        this.orderDTO = orderDTO;
        this.serverSkuList = serverSkuList;
        this.couponChecker = couponChecker;
        this.maxSkuLimit = maxSkuLimit;
    }

    ///////////////////////////////////////////////////////////////////////////
    // checker
    ///////////////////////////////////////////////////////////////////////////
    /*
    1. 原价校验：orderTotalPrice 和 serviceTotalPrice 比较。
    2. 商品是否下架检测
    3. 售罄 + 购买数量超出限制【购买数量超出库存】
    4. sku 的购买上限【规定的每单购买数量上线】
    5. 优惠券校验
    */
    public void isOK() {
        BigDecimal serverTotalPrice = new BigDecimal("0");
        List<SkuOrderBO> skuOrderBOList = new ArrayList<>();

        //整体sku数量
        skuNotOnSale(orderDTO.getSkuInfoList().size(), serverSkuList.size());

        //每个sku数量以及库存
        for (int i = 0; i < this.serverSkuList.size(); i++) {
            Sku sku = serverSkuList.get(i);
            SkuInfoDTO skuInfoDTO = orderDTO.getSkuInfoList().get(i);

            containsSaleOutSku(sku);
            beyondSkuStock(sku, skuInfoDTO);
            beyondMasSkuLimit(skuInfoDTO);
            serverTotalPrice = serverTotalPrice.add(calculateSkuPrice(sku, skuInfoDTO));
            skuOrderBOList.add(new SkuOrderBO(sku, skuInfoDTO));

            orderSkuList.add(new OrderSku(skuInfoDTO, sku));
        }

        //原总价
        totalPriceIsOK(orderDTO.getTotalPrice(), serverTotalPrice);

        //优惠券校验
        if (couponChecker != null) {
            couponChecker.isOK();
            couponChecker.finalTotalPriceIsOK(orderDTO.getFinalTotalPrice(), serverTotalPrice);
            couponChecker.canBeUsed(skuOrderBOList, serverTotalPrice);
        }
    }

    private void containsSaleOutSku(Sku sku) {
        if (sku.getStock() <= 0) {
            throw new ParameterException(ExceptionCodes.C_50001);
        }
    }

    private void skuNotOnSale(int clientCount, int serverCount) {
        if (serverCount != clientCount) {
            throw new ParameterException(ExceptionCodes.C_50002);
        }
    }

    private void beyondSkuStock(Sku sku, SkuInfoDTO skuInfoDTO) {
        if (sku.getStock() < skuInfoDTO.getCount()) {
            throw new ParameterException(ExceptionCodes.C_50003);
        }
    }

    private void beyondMasSkuLimit(SkuInfoDTO skuInfoDTO) {
        if (skuInfoDTO.getCount() > maxSkuLimit) {
            throw new ParameterException(ExceptionCodes.C_50004);
        }
    }

    private void totalPriceIsOK(BigDecimal orderTotalPrice, BigDecimal serverOrderPrice) {
        if (orderTotalPrice.compareTo(serverOrderPrice) != 0) {
            throw new ParameterException(ExceptionCodes.C_50005);
        }
    }

    private BigDecimal calculateSkuPrice(Sku sku, SkuInfoDTO skuInfoDTO) {
        if (skuInfoDTO.getCount() <= 0) {
            throw new ParameterException(ExceptionCodes.C_50007);
        }
        return sku.getActualPrice().multiply(new BigDecimal(skuInfoDTO.getCount()));
    }

    ///////////////////////////////////////////////////////////////////////////
    // checked order info
    ///////////////////////////////////////////////////////////////////////////
    public String getLeaderImg() {
        return this.serverSkuList.get(0).getImg();
    }

    public String getLeaderTitle() {
        return this.serverSkuList.get(0).getTitle();
    }

    public Integer getTotalCount() {
        return orderDTO.getSkuInfoList().stream().map(SkuInfoDTO::getCount)
                .reduce(Integer::sum)
                .orElse(0);
    }

}
