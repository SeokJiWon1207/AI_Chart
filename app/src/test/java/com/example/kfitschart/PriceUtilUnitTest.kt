package com.example.kfitschart

import com.kfitchart.util.KfitCurrencyType
import com.kfitchart.util.getKfitDecimalFormat
import com.kfitchart.util.getKfitHeadAndTailUnit
import com.kfitchart.util.getKfitSign
import com.kfitchart.util.getKfitUnderOneDollarDecimalSize
import com.kfitchart.util.toKfitDecimalSizeFormat
import com.kfitchart.util.toKfitDoubleOrZero
import com.kfitchart.util.toKfitIndexDecimalFormat
import com.kfitchart.util.toKfitPercent
import com.kfitchart.util.toKfitStockDecimalFormat
import org.junit.Assert
import org.junit.Test

class PriceUtilUnitTest {

    //통화 타입으로 변경
    val krw = KfitCurrencyType.findType("krw")
    val usd = KfitCurrencyType.findType("usd")

    @Test
    fun 부호_가져오기(){
        Assert.assertEquals(getKfitSign(0.0), "")
        Assert.assertEquals(getKfitSign(1.0), "+")
        Assert.assertEquals(getKfitSign(-1.0), "-")
    }

    @Test
    fun 통화_표시_단위_가져오기(){
        Assert.assertEquals(getKfitHeadAndTailUnit(krw), "" to "원")
        Assert.assertEquals(getKfitHeadAndTailUnit(usd), "$" to "")
        Assert.assertEquals(getKfitHeadAndTailUnit(null), "" to "")
    }

    @Test
    fun 소수점_포맷_가져오기(){
        Assert.assertEquals(getKfitDecimalFormat(0), "###,##0")
        Assert.assertEquals(getKfitDecimalFormat(1), "###,##0.0")
        Assert.assertEquals(getKfitDecimalFormat(2), "###,##0.00")
        Assert.assertEquals(getKfitDecimalFormat(3), "###,##0.000")
        Assert.assertEquals(getKfitDecimalFormat(4), "###,##0.0000")
    }

    @Test
    fun 달러_소수점_자리() {
        Assert.assertEquals(getKfitUnderOneDollarDecimalSize(""), 4)
        Assert.assertEquals(getKfitUnderOneDollarDecimalSize("1"), 2)

        Assert.assertEquals(getKfitUnderOneDollarDecimalSize("0"), 4)
        Assert.assertEquals(getKfitUnderOneDollarDecimalSize("0.2"), 4)

        Assert.assertEquals(getKfitUnderOneDollarDecimalSize("-1"), 2)
        Assert.assertEquals(getKfitUnderOneDollarDecimalSize("-0.2"), 4)
    }

    @Test
    fun 퍼센트_변환() {
        Assert.assertEquals("".toKfitPercent(), "0.0%")
        Assert.assertEquals(null.toKfitPercent(), "0.0%")
        Assert.assertEquals("0.001".toKfitPercent(), "0.001%")
        Assert.assertEquals("-0.001".toKfitPercent(), "0.001%")
        Assert.assertEquals("-0.1".toKfitPercent(), "0.1%")

        Assert.assertEquals("-0.1".toKfitPercent(isSign = true), "-0.1%")
        Assert.assertEquals("0.1".toKfitPercent(isSign = true), "+0.1%")
        Assert.assertEquals("-1000.12345678".toKfitPercent(isSign = true), "-1,000.12345678%")
        Assert.assertEquals("1000.12345678".toKfitPercent(isSign = true), "+1,000.12345678%")
    }

    @Test
    fun 종목_가격_표시() {
        Assert.assertEquals("".toKfitStockDecimalFormat(), "0")
        Assert.assertEquals("".toKfitStockDecimalFormat(krw), "0원")
        Assert.assertEquals("".toKfitStockDecimalFormat(usd), "$0.0000")
        Assert.assertEquals("".toKfitStockDecimalFormat(currencyType = krw, isSign = true), "0원")
        Assert.assertEquals("".toKfitStockDecimalFormat(currencyType = usd, isSign = true), "$0.0000")

        Assert.assertEquals("12345".toKfitStockDecimalFormat(krw), "12,345원")
        Assert.assertEquals("12345".toKfitStockDecimalFormat(currencyType = krw, isSign = true), "+12,345원")
        Assert.assertEquals("-12345".toKfitStockDecimalFormat(krw), "12,345원")
        Assert.assertEquals("-12345".toKfitStockDecimalFormat(currencyType = krw, isSign = true), "-12,345원")
        Assert.assertEquals("12345.5".toKfitStockDecimalFormat(currencyType = krw, isSign = true), "+12,346원")
        Assert.assertEquals("-12345.5".toKfitStockDecimalFormat(currencyType = krw, isSign = true), "-12,346원")

        Assert.assertEquals("12345".toKfitStockDecimalFormat(usd), "$12,345.00")
        Assert.assertEquals("12345".toKfitStockDecimalFormat(currencyType = usd, isSign = true), "+$12,345.00")
        Assert.assertEquals("-12345".toKfitStockDecimalFormat(currencyType = usd, isSign = true), "-$12,345.00")
        Assert.assertEquals("-0.1".toKfitStockDecimalFormat(usd), "$0.1000")
        Assert.assertEquals("0.1".toKfitStockDecimalFormat(usd), "$0.1000")
        Assert.assertEquals("-0.1".toKfitStockDecimalFormat(currencyType = usd, isSign = true), "-$0.1000")
        Assert.assertEquals("0.1".toKfitStockDecimalFormat(currencyType = usd, isSign = true), "+$0.1000")
        Assert.assertEquals("0.10005".toKfitStockDecimalFormat(usd), "$0.1001")
        Assert.assertEquals("0.10005".toKfitStockDecimalFormat(currencyType = usd, isSign = true), "+$0.1001")
        Assert.assertEquals("-0.10005".toKfitStockDecimalFormat(currencyType = usd, isSign = true), "-$0.1001")
    }

    @Test
    fun 지수_가격_표시(){
        Assert.assertEquals("".toKfitIndexDecimalFormat(), "0")
        Assert.assertEquals("0".toKfitIndexDecimalFormat(), "0")
        Assert.assertEquals("-1000".toKfitIndexDecimalFormat(), "0")
        Assert.assertEquals("1".toKfitIndexDecimalFormat(), "1.00")
        Assert.assertEquals("1000".toKfitIndexDecimalFormat(), "1,000.00")
        Assert.assertEquals("1000000".toKfitIndexDecimalFormat(), "1,000,000.00")
    }

    @Test
    fun 지정_소수점_자리_표시() {
        Assert.assertEquals("".toKfitDecimalSizeFormat(0), "0")
        Assert.assertEquals("".toKfitDecimalSizeFormat(1), "0.0")
        Assert.assertEquals("".toKfitDecimalSizeFormat(2), "0.00")
        Assert.assertEquals("".toKfitDecimalSizeFormat(3), "0.000")
        Assert.assertEquals("".toKfitDecimalSizeFormat(4), "0.0000")

        Assert.assertEquals("0".toKfitDecimalSizeFormat(0), "0")
        Assert.assertEquals("0".toKfitDecimalSizeFormat(1), "0.0")
        Assert.assertEquals("0".toKfitDecimalSizeFormat(2), "0.00")
        Assert.assertEquals("0".toKfitDecimalSizeFormat(3), "0.000")
        Assert.assertEquals("0".toKfitDecimalSizeFormat(4), "0.0000")

        Assert.assertEquals("1000".toKfitDecimalSizeFormat(0), "1,000")
        Assert.assertEquals("1000".toKfitDecimalSizeFormat(1), "1,000.0")
        Assert.assertEquals("1000".toKfitDecimalSizeFormat(2), "1,000.00")
        Assert.assertEquals("1000".toKfitDecimalSizeFormat(3), "1,000.000")
        Assert.assertEquals("1000".toKfitDecimalSizeFormat(4), "1,000.0000")

        Assert.assertEquals("-1000".toKfitDecimalSizeFormat(0), "-1,000")
        Assert.assertEquals("-1000".toKfitDecimalSizeFormat(1), "-1,000.0")
        Assert.assertEquals("-1000".toKfitDecimalSizeFormat(2), "-1,000.00")
        Assert.assertEquals("-1000".toKfitDecimalSizeFormat(3), "-1,000.000")
        Assert.assertEquals("-1000".toKfitDecimalSizeFormat(4), "-1,000.0000")
    }
}