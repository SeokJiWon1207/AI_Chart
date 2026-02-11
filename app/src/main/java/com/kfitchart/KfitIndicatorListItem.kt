package com.kfitchart

import com.google.gson.annotations.SerializedName

data class KfitIndicatorListItem(
    @SerializedName("use")
    val use: String,
    @SerializedName("detailType")
    val detailType: String,
    @SerializedName("subType")
    val subType: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("tag")
    val tag: String,
) {
    constructor() : this("", "", "", "", "", "")
    constructor(code: Int, message: String) : this("", "", "", "", message, code.toString())
    constructor(title: String) : this("", "", "", "", title, "")
}
