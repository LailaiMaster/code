package com.lin.sleeve.core.money;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/4 15:59
 */
public class HalfUpRound implements IMoneyDiscount {

    @Override
    public BigDecimal discount(BigDecimal original, BigDecimal discount) {
        return original.multiply(discount).setScale(2, RoundingMode.HALF_UP);
    }

}
