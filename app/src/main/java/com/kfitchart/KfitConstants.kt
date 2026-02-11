package com.kfitchart

/**
 * 캔들차트(Kfit) 공통 상수
 */

// 종목 정보
const val KFIT_KEY_STOCK_ID = "key_stock_id"
const val KFIT_KEY_EXCHANGE_ID = "key_exchange_id"
const val KFIT_KEY_ISIN_CODE = "key_isin_code"
const val KFIT_KEY_IS_INDEX = "key_is_index"

// 차트 호출 봉 갯수
const val DEFAULT_CHART_COUNT = 300

// 차트 종류 구분
const val CHART_FILE_NAME_STOCK_DOMESTIC = "stock"
const val CHART_FILE_NAME_STOCK_FOREIGN = "overseas"
const val CHART_FILE_NAME_INDEX_DOMESTIC = "indexchart"
const val CHART_FILE_NAME_INDEX_FOREIGN = "overseasindex"
const val CHART_FILE_NAME_INDEX_FOREIGN_FUTURE = "overseasfuture"

// 설정 메뉴
const val GE_PACKET_PERIOD_TICK = "0"
const val GE_PACKET_PERIOD_MINUTE = "1"
const val GE_PACKET_PERIOD_DAILY = "2"
const val GE_PACKET_PERIOD_WEEKLY = "3"
const val GE_PACKET_PERIOD_MONTHLY = "4"

// 설정 프리퍼런스 플래그
const val PREF_FLAG_TOOLTIP = "kfit_isFirst"
const val PREF_FLAG_PERIOD_MODE = "kfit_period_mode"
const val PREF_FLAG_PERIOD_TICK = "kfit_period_tick_unit"
const val PREF_FLAG_PERIOD_MINUTE = "kfit_period_minute_unit"
