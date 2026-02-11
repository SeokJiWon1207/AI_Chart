package com.kfitchart.util

import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.abs

//원화 소수점 자리수
const val KFIT_KRW_DECIMAL_SIZE = 0

//외화 최소 소수점 자리수
const val KFIT_MIN_DECIMAL_SIZE = 2

//외화 최대 소수점 자리수
const val KFIT_MAX_DECIMAL_SIZE = 4

/**
 * 통화 타입
 *
 * 기본 값 : USD
 *
 * KRW : 한화
 * USD : 외화
 */
enum class KfitCurrencyType {
    KRW, USD;

    companion object {
        fun findType(value: String) =
            KfitCurrencyType.values().find { value.uppercase(Locale.ENGLISH) == it.name } ?: USD
    }
}


/**
 * 기호 표시 가져오기
 *
 * 양수 +
 * 음수 -
 * 0 공백
 */
fun getKfitSign(value: Double): String {
    return if (value > 0.0) {
        "+"
    } else if (value < 0.0) {
        "-"
    } else {
        ""
    }
}


/**
 * 통화 표시 단위 가져오기
 *
 * @param currencyType 통화 타입
 */
fun getKfitHeadAndTailUnit(currencyType: KfitCurrencyType?): Pair<String, String> {
    return when (currencyType) {
        KfitCurrencyType.KRW -> "" to "원"
        KfitCurrencyType.USD -> "$" to ""
        else -> "" to ""
    }
}


/**
 * 소수점 포맷 가져오기
 *
 * ex)
 * ###,###.00
 * ###,###
 */
fun getKfitDecimalFormat(decimalSize: Int): String {
    val integerPattern = "###,##0"
    var decimalPattern = ""

    for (i in 0 until decimalSize) {
        if (i == 0) {
            decimalPattern += "."
        }
        decimalPattern += "0"
    }

    return integerPattern + decimalPattern
}

/**
 * double 타입으로 변환
 * 기본값 0.0
 */
fun String?.toKfitDoubleOrZero(): Double = this?.toDoubleOrNull() ?: 0.0


/**
 * 소수점 자리 가져오기
 *
 * 1달러 이상이면 2자리
 * 1달러 미만이면 4자리
 */
fun getKfitUnderOneDollarDecimalSize(value: String?): Int {
    val doubleValue = value.toKfitDoubleOrZero()
    val absDoubleValue = abs(doubleValue)

    return if (absDoubleValue >= 1.0) {
        KFIT_MIN_DECIMAL_SIZE
    } else {
        KFIT_MAX_DECIMAL_SIZE
    }
}


/**
 * 퍼센트 표시
 *
 * 기본값 0.0
 *
 * @param isSign 기호 표시 여부
 *
 *
 * ex) -0.01, false -> 0.01%
 * ex) 0.1, false -> 0.1%
 * ex) -0.01, true -> -0.01%
 * ex) 0.1, true -> +0.1%
 */
fun String?.toKfitPercent(isSign: Boolean = false): String {
    val doubleValue = this.toKfitDoubleOrZero()

    return runCatching<String> {
        val absStr = abs(doubleValue)
        val decimalSize = doubleValue.toBigDecimal().scale()
        val decimalFormat = getKfitDecimalFormat(decimalSize)

        //기호 표시
        val sign = if (isSign) getKfitSign(doubleValue) else ""

        return DecimalFormat(sign + decimalFormat).format(absStr) + "%"
    }.getOrDefault(doubleValue.toString())
}


/**
 * 종목 가격 포맷팅
 *
 * 기본값 0.0 (null or emptyString 의 경우)
 *
 * @param currencyType : 통화 타입
 * [currencyType]이 null일 경우 통화 표시가 없음
 *
 * ex) 0.1, USD -> $0.1000
 * ex) 1, USD -> $1.00
 *
 * ex) 1, KRW -> 1원
 * ex) 1000, KRW -> 1,000원
 *
 * ex) 0.1, null -> 0.1
 * ex) 1, null -> 1
 *
 * 타입 변환 에러시 더블 형 변환 값을 리턴
 */
fun String?.toKfitStockDecimalFormat(
    currencyType: KfitCurrencyType? = null,
    isSign: Boolean = false
): String {
    //double 타입형 변환
    val doubleValue = this.toKfitDoubleOrZero()
    val absDoubleValue = abs(doubleValue)

    return runCatching<String> {
        //통화 타입 > 단위 표시
        val (headUnit, tailUnit) = getKfitHeadAndTailUnit(currencyType)

        //기호 표시
        val sign = if (isSign) getKfitSign(doubleValue) else ""

        //소수점 자리수
        val decimalSize = when (currencyType) {
            KfitCurrencyType.USD -> {
                getKfitUnderOneDollarDecimalSize(this)
            }
            else -> KFIT_KRW_DECIMAL_SIZE
        }

        //소수점 자리 포맷
        val decimalFormat = getKfitDecimalFormat(decimalSize)

        //포맷팅한 결과
        DecimalFormat(sign + headUnit + decimalFormat + tailUnit).format(absDoubleValue)
    }.getOrDefault(doubleValue.toString())
}


/**
 * 지수 가격 포맷팅
 *
 * 기본값 0
 * 0보다 크면 2자리 고정
 */
fun String?.toKfitIndexDecimalFormat(): String {
    val doubleValue = this.toKfitDoubleOrZero()

    return runCatching<String> {
        if (doubleValue > 0.0) {
            val dec = DecimalFormat("###,###.00")
            dec.format(doubleValue)
        } else {
            "0"
        }
    }.getOrDefault("0")
}

/**
 * 지정 소수점 자리 포맷팅
 *
 * @param decimalSize 소수점 자리수
 *
 * ex) 0.1, 2 -> 0.10
 * ex) 0.1, 4 -> 0.1000
 */
fun String?.toKfitDecimalSizeFormat(decimalSize: Int): String {
    val doubleValue = this.toKfitDoubleOrZero()

    return runCatching<String> {
        val decimalFormat = getKfitDecimalFormat(decimalSize)
        return DecimalFormat(decimalFormat).format(doubleValue)
    }.getOrDefault(doubleValue.toString())
}


