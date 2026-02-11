package com.kakaopay.app.stock.util

import java.math.BigDecimal

fun String?.toBigDecimalOrZero(): BigDecimal {
    return runCatching { BigDecimal(this@toBigDecimalOrZero) }.getOrNull()
        ?: BigDecimal.ZERO
}

fun BigDecimal.isNotZero(): Boolean {
    return isNotEqual(BigDecimal.ZERO)
}

fun BigDecimal.isNotEqual(target: BigDecimal): Boolean {
    return compareTo(target) != 0
}