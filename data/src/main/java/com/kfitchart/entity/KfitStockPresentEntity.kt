package com.kfitchart.entity

import com.kfitchart.entity.KfitFluctuationFlagType.DOWN
import com.kfitchart.entity.KfitFluctuationFlagType.FLAT
import com.kfitchart.entity.KfitFluctuationFlagType.INIT
import com.kfitchart.entity.KfitFluctuationFlagType.LOWER
import com.kfitchart.entity.KfitFluctuationFlagType.UP
import com.kfitchart.entity.KfitFluctuationFlagType.UPPER
import java.util.Locale

/**
 * @author hanjun.Kim
 * 현재가 정보
 */
data class KfitStockPresentEntity(
    val code: String, // 종목코드
    val name: String, // 종목명
    val type: String, // 종목형태(상품형태)
    val open: String, // 시가
    val high: String, // 고가
    val low: String, // 저가
    val close: String, // 종가
    val sign: KfitFluctuationFlagType, // 대비기호
    val change: String, // 등락폭
    val chgRate: String, // 등락률
    val volume: String, // 거래량
    val market: String, // 마켓정보(국내,해외)
    val decimal: String, // 소수점 자리수
    val timezone: String, // 타임존
    val capitalization: String? = null, // 시가총액          (optional)
    val realCode: String? = null, // 실시간 등록 코드.   (optional)
    val nextKey: List<String>? = null, // 다음 조회키        (optional)
    val isIndex: Boolean = false, // 지수 여부 (optional)
    val isExpected: Boolean = false, // 예상 지수 여부 (optional)
    val indexType: KfitIndexType? = null, // 지수 타입 (optional)
    val isMarketLive: Boolean, // 장상태(Live)
) {

    companion object {

        fun empty(): KfitStockPresentEntity {
            return KfitStockPresentEntity(
                code = "",
                name = "",
                type = "",
                open = "",
                high = "",
                low = "",
                close = "",
                sign = KfitFluctuationFlagType.INIT,
                change = "",
                chgRate = "",
                volume = "",
                market = "",
                decimal = "",
                timezone = "",
                capitalization = null,
                realCode = null,
                nextKey = listOf(),
                isMarketLive = false,
            )
        }
    }
}

enum class KfitIndexType(val key: String) {
    INDEX("I"), // 지수
    FUTURE("F"), // 선물
    ;

    companion object {
        fun findType(key: String): KfitIndexType? = values().find { it.key == key }
    }
}

/**
 * 대비기호 타입
 *
 * @see INIT 장전 종목 배치 수신 이후 초기화 시점
 * @see UPPER 상한가
 * @see UP 상승
 * @see FLAT 보합
 * @see LOWER 하한가
 * @see DOWN 하락
 */
enum class KfitFluctuationFlagType(val value: String) {
    INIT("0"),
    UPPER("1"),
    UP("2"),
    FLAT("3"),
    LOWER("4"),
    DOWN("5"),
    ;
}

fun KfitStockPresentEntity.isIndexChart(): Boolean {
    return this.isIndex
}

fun KfitStockPresentEntity.indexType(): KfitIndexType? {
    return this.indexType
}

fun KfitStockPresentEntity.isDomestic(): Boolean {
    return when {
        this.market.lowercase(Locale.ENGLISH) == "krw" -> true
        this.market.lowercase(Locale.ENGLISH) == "usd" -> false
        else -> false
    }
}
