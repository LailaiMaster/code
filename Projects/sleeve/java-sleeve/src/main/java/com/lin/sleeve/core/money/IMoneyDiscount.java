package com.lin.sleeve.core.money;

import java.math.BigDecimal;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/4 15:58
 */
public interface IMoneyDiscount {

    BigDecimal discount(BigDecimal original, BigDecimal discount);

}
