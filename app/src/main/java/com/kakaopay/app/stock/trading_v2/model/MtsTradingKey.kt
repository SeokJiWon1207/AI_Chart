package com.kakaopay.app.stock.trading_v2.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MtsTradingStockKey(
    val key: String,
    val isIndex: Boolean
) : Parcelable {
    fun getId(): String = key.split("/").firstOrNull() ?: ""
    fun getExchangeId(): String = key.split("/").getOrElse(1) { "" }
    fun getIsinCode(): String = key.split("/").getOrElse(2) { "" }
}
