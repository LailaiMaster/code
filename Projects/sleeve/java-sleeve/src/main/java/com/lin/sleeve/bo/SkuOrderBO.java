package com.lin.sleeve.bo;

import com.lin.sleeve.dto.SkuInfoDTO;
import com.lin.sleeve.model.Sku;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/4 17:15
 */
@Getter
@Setter
@ToString
public class SkuOrderBO {

    private BigDecimal price;
    private Integer count;
    private Long categoryId;

    public SkuOrderBO(Sku sku, SkuInfoDTO skuInfo) {
        this.price = sku.getActualPrice();
        this.count = skuInfo.getCount();
        this.categoryId = sku.getCategoryId();
    }

    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(count));
    }

}
