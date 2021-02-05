package com.lin.sleeve.model;

import com.lin.sleeve.dto.SkuInfoDTO;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用于存放到订单中的 sku 数据。【相当于快照】
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/5 17:09
 */
@Getter
@Setter
@ToString
public class OrderSku {

    private Long id;
    private Long skuId;
    private BigDecimal finalPrice;
    private BigDecimal singlePrice;
    private List<String> specValues;
    private Integer count;
    private String img;
    private String title;

    public OrderSku(SkuInfoDTO skuInfoDTO, Sku sku) {
        this.id = sku.getId();
        this.skuId = sku.getSpuId();
        this.count = skuInfoDTO.getCount();
        this.singlePrice = sku.getActualPrice();
        this.finalPrice = this.singlePrice.multiply(new BigDecimal(count));
        this.img = sku.getImg();
        this.title = sku.getTitle();
        this.specValues = sku.getSpecValueList();
    }

    public OrderSku() {
    }

}
