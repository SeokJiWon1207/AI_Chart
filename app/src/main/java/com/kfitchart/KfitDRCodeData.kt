package com.kfitchart

class DRCodeData() {
    var strCode: String? = ""
    var strName: String? = ""
    var strClose: String? = ""
    var strSign: String? = ""
    var strChange: String? = ""
    var strChgRate: String? = ""
    var strVolume: String? = ""
    var strOpen: String? = ""
    var strHigh: String? = ""
    var strLow: String? = ""
    var strRealCode: String? = ""
    var strValue: String? = "" // 거래금액
    var strDecimal: String? = "" // 소수점 자리수
    var nextKey: List<String>? = null
}

class DRCodeRealData() {
    var strRealCode: String? = null
    var strRealTime: String? = null
    var strRealSign: String? = null
    var strRealChange: String? = null
    var strRealChgRate: String? = null
    var strRealPrice: String? = null
    var strRealOpen: String? = null
    var strRealHigh: String? = null
    var strRealLow: String? = null
    var strRealVolume: String? = null
    var strRealCheVol: String? = null
    var strRealValue: String? = null
    var strRealMigyul: String? = null
}
