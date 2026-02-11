package com.example.testapi

import com.kfitchart.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.coroutines.CoroutineContext

class KfitPayRemoteDataSource : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + Job()

    private val messageFlow = MutableSharedFlow<Any>()
    private val connectionStateFlow = MutableSharedFlow<KfitConnectionStateEntity>()
    private val averagePriceFlow = MutableSharedFlow<KfitAverageBuyPriceStreamEntity>()

    private var subscribe: HashSet<Int> = hashSetOf()

    fun initialize() {
        launch {
            while (true) {
                if (subscribe.contains(1)) {
                    delay(4000)
                    messageFlow.emit(
                        KfitPresentPriceStreamEntity(
                            exchangeId = "001",// 거래소코드
                            stockId = "A000660", // 종목코드
                            endPrice = "122500", // 전일종가
                            startPrice = "122500", // 시가
                            highestPrice = "123000", // 고가
                            lowestPrice = "120500", // 저가
                            currentPrice = "125000", // 현재가
                            fluctuationFlagType = KfitFluctuationFlagType.DOWN, //대비기호
                            fluctuationPrice = "0", // 당일 변동가
                            fluctuationRate = "-14.23", // 당일 변동률
                            volume = "70",
                            totalVolume = "1929673", // 누적 거래량
                            updatedAt = 10000, // 체결시간
                            isExpected = false, //예상지수 여부
                            isIndex = false, //지수종목 여부
                            sessionId = "2",
                        )
                    )
                    delay(8000)
                    messageFlow.emit(
                        KfitPresentPriceStreamEntity(
                            exchangeId = "001",// 거래소코드
                            stockId = "A000660", // 종목코드
                            endPrice = "122500", // 전일종가
                            startPrice = "122500", // 시가
                            highestPrice = "123000", // 고가
                            lowestPrice = "120500", // 저가
                            currentPrice = "122000", // 현재가
                            fluctuationFlagType = KfitFluctuationFlagType.UP, //대비기호
                            fluctuationPrice = "-500", // 당일 변동가
                            fluctuationRate = "12.19", // 당일 변동률
                            volume = "1",
                            totalVolume = "1929603", // 누적 거래량
                            updatedAt = 1000, // 체결시간
                            isExpected = false, //예상지수 여부
                            isIndex = false, //지수종목 여부
                            sessionId = "0",
                        )
                    )
                    delay(12000)
                    messageFlow.emit(
                        KfitPresentPriceStreamEntity(
                            exchangeId = "001",// 거래소코드
                            stockId = "A000660", // 종목코드
                            endPrice = "122500", // 전일종가
                            startPrice = "122500", // 시가
                            highestPrice = "123000", // 고가
                            lowestPrice = "120500", // 저가
                            currentPrice = "125000", // 현재가
                            fluctuationFlagType = KfitFluctuationFlagType.UPPER, //대비기호
                            fluctuationPrice = "0", // 당일 변동가
                            fluctuationRate = "29.99", // 당일 변동률
                            volume = "70",
                            totalVolume = "1929673", // 누적 거래량
                            updatedAt = 10000, // 체결시간
                            isExpected = false, //예상지수 여부
                            isIndex = false, //지수종목 여부
                            sessionId = "2",
                        )
                    )
                    delay(16000)
                    messageFlow.emit(
                        KfitPresentPriceStreamEntity(
                            exchangeId = "001",// 거래소코드
                            stockId = "A000660", // 종목코드
                            endPrice = "122500", // 전일종가
                            startPrice = "122500", // 시가
                            highestPrice = "123000", // 고가
                            lowestPrice = "120500", // 저가
                            currentPrice = "125000", // 현재가
                            fluctuationFlagType = KfitFluctuationFlagType.LOWER, //대비기호
                            fluctuationPrice = "0", // 당일 변동가
                            fluctuationRate = "-29.99", // 당일 변동률
                            volume = "70",
                            totalVolume = "1929673", // 누적 거래량
                            updatedAt = 10000, // 체결시간
                            isExpected = false, //예상지수 여부
                            isIndex = false, //지수종목 여부
                            sessionId = "2",
                        )
                    )
                }

                //지수
                if (subscribe.contains(3)) {
                    delay(5000)
                    messageFlow.emit(
                        KfitPresentPriceStreamEntity(
                            exchangeId = "001",// 거래소코드
                            stockId = "A000660", // 종목코드
                            endPrice = "122500", // 전일종가
                            startPrice = "122500", // 시가
                            highestPrice = "123000", // 고가
                            lowestPrice = "120500", // 저가
                            currentPrice = "122000", // 현재가
                            fluctuationFlagType = KfitFluctuationFlagType.DOWN, //대비기호
                            fluctuationPrice = "-500", // 당일 변동가
                            fluctuationRate = "-0.41", // 당일 변동률
                            volume = "1",
                            totalVolume = "1929603", // 누적 거래량
                            updatedAt = 5000, // 체결시간
                            isExpected = false, //예상지수 여부
                            isIndex = true, //지수종목 여부
                            sessionId = "0",
                        )
                    )
                    delay(10000)
                    messageFlow.emit(
                        KfitPresentPriceStreamEntity(
                            exchangeId = "001",// 거래소코드
                            stockId = "A000660", // 종목코드
                            endPrice = "122500", // 전일종가
                            startPrice = "122500", // 시가
                            highestPrice = "123000", // 고가
                            lowestPrice = "120500", // 저가
                            currentPrice = "125000", // 현재가
                            fluctuationFlagType = KfitFluctuationFlagType.FLAT, //대비기호
                            fluctuationPrice = "0", // 당일 변동가
                            fluctuationRate = "0.00", // 당일 변동률
                            volume = "70",
                            totalVolume = "1929673", // 누적 거래량
                            updatedAt = 1, // 체결시간
                            isExpected = false, //예상지수 여부
                            isIndex = true, //지수종목 여부
                            sessionId = "0",
                        )
                    )
                }
            }
        }

        launch {
            averagePriceFlow.emit(
                KfitAverageBuyPriceStreamEntity(
                    holdingQuantity = "33",
                    averagePrice = "124000"
                )
            )
        }
    }

    fun onResume() {
        launch {
            averagePriceFlow.emit(
                KfitAverageBuyPriceStreamEntity(
                    holdingQuantity = "33",
                    averagePrice = "124000"
                )
            )
        }
    }

    fun getAveragePriceData() {
        launch(Dispatchers.IO) {
            delay(200)
            messageFlow.emit(
                KfitAverageBuyPriceStreamEntity(
                    holdingQuantity = "22",
                    averagePrice = "122000"
                )
            )
        }
    }

    //    code=AAPL, name=애플, open=146.6500, high=147.0800, low=145.6400, close=145.8500, sign=, change=0.9800, chgRate=0.67, volume=64838170, market=USD, decimal=4, timezone=America/New_York, capitalization=null, realCode=null, nextKey=null)
    suspend fun getStockPresent(): KfitStockPresentEntity {
        delay(200)
        return KfitStockPresentEntity(
            code = "A000660",
            name = "SK하이닉스",
            type = "ST",
            open = "146.6500",
            high = "147.0800",
            low = "145.6400",
            close = "0.5234",
            sign = KfitFluctuationFlagType.UP, // 대비기호
            change = "0.9800",
            chgRate = "0.67",
            decimal = "0",
            volume = "64838170",
            market = "USD",
            timezone = "America/New_York", //Asia/Seoul, America/New_York
            isMarketLive = true // true(Live), false(Close)
        )
    }

    suspend fun getIndexPresent(): KfitStockPresentEntity {
        delay(200)
        return KfitStockPresentEntity(
            code = "A000660",
            name = "코스피",
            type = "SR",
            open = "146.6500",
            high = "147.0800",
            low = "145.6400",
            close = "0.0",
            sign = KfitFluctuationFlagType.FLAT, //대비기호
            change = "0.0",
            chgRate = "0.0",
            decimal = "0",
            volume = "64838170",
            market = "KRW",
            timezone = "Asia/Seoul", //Asia/Seoul, America/New_York
            isIndex = true,
            isExpected = false,
            indexType = KfitIndexType.INDEX, //KfitIndexType.INDEX(지수), KfitIndexType.FUTURE (선물)
            isMarketLive = false // true(Live), false(Close)
        )
    }

    suspend fun getBaseChart(request: KfitBaseChartRequestEntity): KfitBaseChartResponseEntity {
        delay(200)

//        val file = "assets/BasicChart_mock.json" // res/raw/test.txt also work.
//        val inputStream = this.javaClass.classLoader.getResourceAsStream(file)


        var chartDataList: MutableList<KfitBaseChart> = ArrayList()

        // timestamp로 넘어올 데이터.
        if (request.chartDivision == "TEST") { //WEEK
            if (request.nextKey.isNullOrEmpty()) {
                val list = mutableListOf(
                    KfitBaseChart(
                        dateTime = 1621177200,
                        startPrice = "126000",
                        highPrice = "122500",
                        lowPrice = "122500",
                        closePrice = "122500",
                        volume = "45641070",
                        amount = "5652402383116",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1619362800,
                        startPrice = "131000",
                        highPrice = "128000",
                        lowPrice = "128000",
                        closePrice = "128000",
                        volume = "64375752",
                        amount = "8134200934845",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1617548400,
                        startPrice = "148000",
                        highPrice = "140000",
                        lowPrice = "140000",
                        closePrice = "140000",
                        volume = "47834586",
                        amount = "6627433740688",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1615734000,
                        startPrice = "135500",
                        highPrice = "179000",
                        lowPrice = "93500",
                        closePrice = "138000",
                        volume = "41966349",
                        amount = "5781492035784",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1613919600,
                        startPrice = "143000",
                        highPrice = "150500",
                        lowPrice = "98500",
                        closePrice = "141500",
                        volume = "97837462",
                        amount = "13693516768921",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1612105200,
                        startPrice = "130000",
                        highPrice = "134000",
                        lowPrice = "118000",
                        closePrice = "127500",
                        volume = "57611417",
                        amount = "7380693126733",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1610290800,
                        startPrice = "130000",
                        highPrice = "140000",
                        lowPrice = "121500",
                        closePrice = "127500",
                        volume = "89277006",
                        amount = "11607473943977",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1608476400,
                        startPrice = "124500",
                        highPrice = "138000",
                        lowPrice = "112500",
                        closePrice = "118000",
                        volume = "66140977",
                        amount = "8257023324672",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1606662000,
                        startPrice = "116000",
                        highPrice = "121000",
                        lowPrice = "97300",
                        closePrice = "115000",
                        volume = "72764275",
                        amount = "8259717036128",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1604847600,
                        startPrice = "97300",
                        highPrice = "102000",
                        lowPrice = "85600",
                        closePrice = "89700",
                        volume = "56036659",
                        amount = "5319685379403",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1603033200,
                        startPrice = "80200",
                        highPrice = "90900",
                        lowPrice = "79200",
                        closePrice = "83900",
                        volume = "45738652",
                        amount = "3834057765086",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1601218800,
                        startPrice = "83900",
                        highPrice = "89000",
                        lowPrice = "81300",
                        closePrice = "84000",
                        volume = "38896105",
                        amount = "3299196762939",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1599404400,
                        startPrice = "83700",
                        highPrice = "85900",
                        lowPrice = "76300",
                        closePrice = "78400",
                        volume = "61120221",
                        amount = "4975079644577",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1597676400,
                        startPrice = "78200",
                        highPrice = "80200",
                        lowPrice = "71300",
                        closePrice = "74500",
                        volume = "68790573",
                        amount = "5265548761647",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1595775600,
                        startPrice = "80100",
                        highPrice = "86400",
                        lowPrice = "79800",
                        closePrice = "82800",
                        volume = "58873519",
                        amount = "4831202314922",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1593961200,
                        startPrice = "82800",
                        highPrice = "87300",
                        lowPrice = "82000",
                        closePrice = "82800",
                        volume = "42856079",
                        amount = "3580284090315",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1592146800,
                        startPrice = "84000",
                        highPrice = "87400",
                        lowPrice = "82000",
                        closePrice = "85400",
                        volume = "49369064",
                        amount = "4205275323863",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1590332400,
                        startPrice = "93000",
                        highPrice = "93000",
                        lowPrice = "80500",
                        closePrice = "81500",
                        volume = "74974647",
                        amount = "6446637417934",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1588518000,
                        startPrice = "80400",
                        highPrice = "86500",
                        lowPrice = "79200",
                        closePrice = "85000",
                        volume = "58645834",
                        amount = "4842977270196",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1586703600,
                        startPrice = "81700",
                        highPrice = "85900",
                        lowPrice = "80000",
                        closePrice = "84100",
                        volume = "52993665",
                        amount = "4378413763523",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1584889200,
                        startPrice = "81000",
                        highPrice = "87900",
                        lowPrice = "68000",
                        closePrice = "83300",
                        volume = "89760031",
                        amount = "7271449710331",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1583074800,
                        startPrice = "83700",
                        highPrice = "95900",
                        lowPrice = "65800",
                        closePrice = "92600",
                        volume = "98649914",
                        amount = "8187898308656",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1581260400,
                        startPrice = "100500",
                        highPrice = "106000",
                        lowPrice = "87700",
                        closePrice = "104500",
                        volume = "56496031",
                        amount = "5518104721025",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1579446000,
                        startPrice = "91800",
                        highPrice = "101000",
                        lowPrice = "91500",
                        closePrice = "98700",
                        volume = "36991015",
                        amount = "3597273695248",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1577631600,
                        startPrice = "99000",
                        highPrice = "103500",
                        lowPrice = "92800",
                        closePrice = "94500",
                        volume = "38734346",
                        amount = "3777719077278",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1575817200,
                        startPrice = "95800",
                        highPrice = "97000",
                        lowPrice = "79400",
                        closePrice = "87900",
                        volume = "43071488",
                        amount = "3855439134759",
                        sessionId = "0",
                        createdTime = null
                    ),
                    KfitBaseChart(
                        dateTime = 1574002800,
                        startPrice = "80900",
                        highPrice = "86500",
                        lowPrice = "76800",
                        closePrice = "81700",
                        volume = "51332348",
                        amount = "4129554591537",
                        sessionId = "0",
                        createdTime = null
                    )
                )
                chartDataList = list
            } else if (request.nextKey!!.contains("20170724")) {
                val list = mutableListOf(
                    convertKfitBaseChart(
                        dateTime = 1622386800000,
                        startPrice = 126000,
                        highPrice = 130000,
                        lowPrice = 124000,
                        closePrice = 128500,
                        volume = 14062791,
                        amount = 1792928359000
                    ),
                    convertKfitBaseChart(
                        dateTime = 1621782000000,
                        startPrice = 122000,
                        highPrice = 126500,
                        lowPrice = 119000,
                        closePrice = 125000,
                        volume = 16334658,
                        amount = 2010538805368
                    ),
                    convertKfitBaseChart(
                        dateTime = 1621177200000,
                        startPrice = 120000,
                        highPrice = 125500,
                        lowPrice = 117000,
                        closePrice = 122500,
                        volume = 15243621,
                        amount = 1848935218748
                    ),
                    convertKfitBaseChart(
                        dateTime = 1620572400000,
                        startPrice = 131000,
                        highPrice = 131000,
                        lowPrice = 115500,
                        closePrice = 118500,
                        volume = 30490990,
                        amount = 3713417333953
                    ),
                    convertKfitBaseChart(
                        dateTime = 1619967600000,
                        startPrice = 127500,
                        highPrice = 132500,
                        lowPrice = 127000,
                        closePrice = 129500,
                        volume = 14294260,
                        amount = 1855943095460
                    ),
                    convertKfitBaseChart(
                        dateTime = 1619362800000,
                        startPrice = 132500,
                        highPrice = 136000,
                        lowPrice = 126500,
                        closePrice = 128000,
                        volume = 19590502,
                        amount = 2564840505432
                    ),
                    convertKfitBaseChart(
                        dateTime = 1618758000000,
                        startPrice = 138000,
                        highPrice = 139000,
                        lowPrice = 128500,
                        closePrice = 132500,
                        volume = 17056960,
                        amount = 2286814055052
                    ),
                    convertKfitBaseChart(
                        dateTime = 1618153200000,
                        startPrice = 140500,
                        highPrice = 140500,
                        lowPrice = 135500,
                        closePrice = 137500,
                        volume = 13805875,
                        amount = 1903651586136
                    ),
                    convertKfitBaseChart(
                        dateTime = 1617548400000,
                        startPrice = 143000,
                        highPrice = 147000,
                        lowPrice = 139500,
                        closePrice = 140000,
                        volume = 16971751,
                        amount = 2436968099500
                    ),
                    convertKfitBaseChart(
                        dateTime = 1616943600000,
                        startPrice = 135500,
                        highPrice = 143000,
                        lowPrice = 131500,
                        closePrice = 141000,
                        volume = 19454771,
                        amount = 2654507016000
                    ),
                    convertKfitBaseChart(
                        dateTime = 1616338800000,
                        startPrice = 114500,
                        highPrice = 179000,
                        lowPrice = 93500,
                        closePrice = 135000,
                        volume = 3886241,
                        amount = 520277625000
                    ),
                    convertKfitBaseChart(
                        dateTime = 1615734000000,
                        startPrice = 141500,
                        highPrice = 145000,
                        lowPrice = 136000,
                        closePrice = 138000,
                        volume = 18625337,
                        amount = 2606707394784
                    ),
                    convertKfitBaseChart(
                        dateTime = 1615129200000,
                        startPrice = 143000,
                        highPrice = 143000,
                        lowPrice = 98500,
                        closePrice = 98500,
                        volume = 25739587,
                        amount = 3501852146750
                    ),
                    convertKfitBaseChart(
                        dateTime = 1614610800000,
                        startPrice = 149000,
                        highPrice = 150500,
                        lowPrice = 136500,
                        closePrice = 140000,
                        volume = 27881750,
                        amount = 3987517182000
                    ),
                    convertKfitBaseChart(
                        dateTime = 1613919600000,
                        startPrice = 136000,
                        highPrice = 149500,
                        lowPrice = 131500,
                        closePrice = 141500,
                        volume = 44216125,
                        amount = 6204147440171
                    ),
                    convertKfitBaseChart(
                        dateTime = 1613314800000,
                        startPrice = 130000,
                        highPrice = 134000,
                        lowPrice = 125500,
                        closePrice = 133000,
                        volume = 23003344,
                        amount = 2997090523200
                    ),
                    convertKfitBaseChart(
                        dateTime = 1612710000000,
                        startPrice = 127500,
                        highPrice = 130000,
                        lowPrice = 124500,
                        closePrice = 126000,
                        volume = 12316092,
                        amount = 1559819404393
                    ),
                    convertKfitBaseChart(
                        dateTime = 1612105200000,
                        startPrice = 121000,
                        highPrice = 132500,
                        lowPrice = 118000,
                        closePrice = 127500,
                        volume = 22291981,
                        amount = 2823783199140
                    ),
                    convertKfitBaseChart(
                        dateTime = 1611500400000,
                        startPrice = 130000,
                        highPrice = 135000,
                        lowPrice = 121500,
                        closePrice = 122500,
                        volume = 30062562,
                        amount = 3844504797652
                    ),
                    convertKfitBaseChart(
                        dateTime = 1610895600000,
                        startPrice = 126000,
                        highPrice = 135000,
                        lowPrice = 125000,
                        closePrice = 128500,
                        volume = 22349460,
                        amount = 2910291509524
                    ),
                    convertKfitBaseChart(
                        dateTime = 1610290800000,
                        startPrice = 138000,
                        highPrice = 140000,
                        lowPrice = 125500,
                        closePrice = 127500,
                        volume = 36864984,
                        amount = 4852677636801
                    ),
                    convertKfitBaseChart(
                        dateTime = 1609686000000,
                        startPrice = 124500,
                        highPrice = 138000,
                        lowPrice = 120500,
                        closePrice = 138000,
                        volume = 38395298,
                        amount = 5038418714941
                    ),
                    convertKfitBaseChart(
                        dateTime = 1609081200000,
                        startPrice = 119000,
                        highPrice = 119500,
                        lowPrice = 114000,
                        closePrice = 118500,
                        volume = 13229240,
                        amount = 1545622085000
                    ),
                    convertKfitBaseChart(
                        dateTime = 1608476400000,
                        startPrice = 118000,
                        highPrice = 118500,
                        lowPrice = 112500,
                        closePrice = 118000,
                        volume = 14516439,
                        amount = 1672982524731
                    ),
                    convertKfitBaseChart(
                        dateTime = 1607871600000,
                        startPrice = 116000,
                        highPrice = 119500,
                        lowPrice = 115000,
                        closePrice = 118500,
                        volume = 14696958,
                        amount = 1729229639367
                    ),
                    convertKfitBaseChart(
                        dateTime = 1607266800000,
                        startPrice = 118500,
                        highPrice = 121000,
                        lowPrice = 114000,
                        closePrice = 115500,
                        volume = 27368870,
                        amount = 3207589154486
                    )
                )
                chartDataList = list
            }
        } else if (request.chartDivision == "MONTHtest") {
            val list = mutableListOf(
                KfitBaseChart(
                    dateTime = 1635732453,
                    startPrice = "127000",
                    highPrice = "147000",
                    lowPrice = "115500",
                    closePrice = "128000",
                    volume = "167117089",
                    amount = "21707253168149",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1633054053,
                    startPrice = "149000",
                    highPrice = "179000",
                    lowPrice = "93500",
                    closePrice = "122500",
                    volume = "315821851",
                    amount = "42208378480856",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1630462053,
                    startPrice = "98600",
                    highPrice = "121000",
                    lowPrice = "79200",
                    closePrice = "79900",
                    volume = "233319404",
                    amount = "23275228343087",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1593529200,
                    startPrice = "75200",
                    highPrice = "87300",
                    lowPrice = "71300",
                    closePrice = "82800",
                    volume = "247376147",
                    amount = "19977880480961",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1585666800,
                    startPrice = "82100",
                    highPrice = "93000",
                    lowPrice = "76500",
                    closePrice = "83700",
                    volume = "267058027",
                    amount = "22412276684447",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1577890800,
                    startPrice = "90000",
                    highPrice = "106000",
                    lowPrice = "65800",
                    closePrice = "93500",
                    volume = "279820097",
                    amount = "24967134360307",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1569855600,
                    startPrice = "80900",
                    highPrice = "97000",
                    lowPrice = "76800",
                    closePrice = "82000",
                    volume = "180695233",
                    amount = "14975244930378",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1561906800,
                    startPrice = "77000",
                    highPrice = "84600",
                    lowPrice = "66200",
                    closePrice = "76900",
                    volume = "169793711",
                    amount = "13028410590015",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1554044400,
                    startPrice = "65200",
                    highPrice = "82400",
                    lowPrice = "62400",
                    closePrice = "79000",
                    volume = "214325547",
                    amount = "15531436821707",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1546354800,
                    startPrice = "70300",
                    highPrice = "78900",
                    lowPrice = "56700",
                    closePrice = "73900",
                    volume = "213140750",
                    amount = "14950813587354",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1538319600,
                    startPrice = "71000",
                    highPrice = "74600",
                    lowPrice = "58100",
                    closePrice = "68200",
                    volume = "215732461",
                    amount = "14685553825732",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1530457200,
                    startPrice = "82200",
                    highPrice = "91200",
                    lowPrice = "72500",
                    closePrice = "86300",
                    volume = "220879826",
                    amount = "17768443986397",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1522594800,
                    startPrice = "90800",
                    highPrice = "97700",
                    lowPrice = "78700",
                    closePrice = "84500",
                    volume = "223129005",
                    amount = "19287076343761",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1514818800,
                    startPrice = "76300",
                    highPrice = "91500",
                    lowPrice = "68200",
                    closePrice = "73500",
                    volume = "270911353",
                    amount = "21223088231020",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1507561200,
                    startPrice = "77200",
                    highPrice = "90300",
                    lowPrice = "74000",
                    closePrice = "82200",
                    volume = "275431199",
                    amount = "22391925045481",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1499007600,
                    startPrice = "69000",
                    highPrice = "86300",
                    lowPrice = "61400",
                    closePrice = "66000",
                    volume = "258824939",
                    amount = "18446862186910",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1491145200,
                    startPrice = "56700",
                    highPrice = "69600",
                    lowPrice = "48400",
                    closePrice = "54000",
                    volume = "197590855",
                    amount = "11329053013741",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1483282800,
                    startPrice = "46850",
                    highPrice = "54900",
                    lowPrice = "44600",
                    closePrice = "53700",
                    volume = "254463762",
                    amount = "12636819047678",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1475506800,
                    startPrice = "43000",
                    highPrice = "46750",
                    lowPrice = "38700",
                    closePrice = "41000",
                    volume = "175739533",
                    amount = "7479528948283",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1467298800,
                    startPrice = "36400",
                    highPrice = "42000",
                    lowPrice = "30300",
                    closePrice = "34400",
                    volume = "186422957",
                    amount = "6570935195426",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1459436400,
                    startPrice = "28850",
                    highPrice = "33100",
                    lowPrice = "25650",
                    closePrice = "28150",
                    volume = "231913091",
                    amount = "6669455614262",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1451833200,
                    startPrice = "31050",
                    highPrice = "32250",
                    lowPrice = "25800",
                    closePrice = "27300",
                    volume = "184317435",
                    amount = "5354665485872",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1443625200,
                    startPrice = "31650",
                    highPrice = "38100",
                    lowPrice = "28550",
                    closePrice = "30700",
                    volume = "228058009",
                    amount = "7412776371496",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1435676400,
                    startPrice = "35150",
                    highPrice = "43200",
                    lowPrice = "30300",
                    closePrice = "37100",
                    volume = "273991123",
                    amount = "9949935589304",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1427814000,
                    startPrice = "50600",
                    highPrice = "51700",
                    lowPrice = "40600",
                    closePrice = "46150",
                    volume = "193271650",
                    amount = "8828128756065",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1420124400,
                    startPrice = "46850",
                    highPrice = "51400",
                    lowPrice = "43100",
                    closePrice = "47650",
                    volume = "160255489",
                    amount = "7503207228625",
                    sessionId = "0",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1412089200,
                    startPrice = "48050",
                    highPrice = "49950",
                    lowPrice = "40300",
                    closePrice = "47450",
                    volume = "137422700",
                    amount = "6368211585134",
                    sessionId = "0",
                    createdTime = null,
                )
            )
            chartDataList = list
        } else if (request.chartDivision == "30TICK") {
            val list = mutableListOf(
                KfitBaseChart(
                    dateTime = 1678266000000,
                    startPrice = "472245.4288",
                    highPrice = "472350.4288",
                    lowPrice = "471381.6150",
                    closePrice = "471602.5588",
                    volume = "30",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null,
                ),
                KfitBaseChart(
                    dateTime = 1678265400000,
                    startPrice = "471640.3900",
                    highPrice = "472119.9988",
                    lowPrice = "469938.2200",
                    closePrice = "469938.2200",
                    volume = "30",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678265200000,
                    startPrice = "469897.5000",
                    highPrice = "469995.4388",
                    lowPrice = "468800.0000",
                    closePrice = "469397.5588",
                    volume = "30",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678263400000,
                    startPrice = "469301.2800",
                    highPrice = "469599.9988",
                    lowPrice = "469000.0100",
                    closePrice = "469161.2850",
                    volume = "30",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678263390000,
                    startPrice = "469000.0100",
                    highPrice = "469970.4288",
                    lowPrice = "469000.0100",
                    closePrice = "469431.4950",
                    volume = "32",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678263380000,
                    startPrice = "469308.2200",
                    highPrice = "469910.0000",
                    lowPrice = "469263.2200",
                    closePrice = "469613.7750",
                    volume = "30",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678263370000,
                    startPrice = "469613.7800",
                    highPrice = "470777.9788",
                    lowPrice = "469593.2200",
                    closePrice = "470461.2750",
                    volume = "30",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678262230000,
                    startPrice = "470627.5000",
                    highPrice = "470693.8588",
                    lowPrice = "469647.4500",
                    closePrice = "469848.1600",
                    volume = "30",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678262200000,
                    startPrice = "469789.4300",
                    highPrice = "470303.8588",
                    lowPrice = "469302.4600",
                    closePrice = "469595.0000",
                    volume = "30",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678262150000,
                    startPrice = "470348.8588",
                    highPrice = "470655.0000",
                    lowPrice = "469630.0000",
                    closePrice = "469902.4600",
                    volume = "30",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678262100000,
                    startPrice = "470399.9988",
                    highPrice = "470483.8588",
                    lowPrice = "469647.4500",
                    closePrice = "469774.4350",
                    volume = "30",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678262020000,
                    startPrice = "469721.9300",
                    highPrice = "470963.8588",
                    lowPrice = "469721.9300",
                    closePrice = "470470.0000",
                    volume = "30",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678262000000,
                    startPrice = "471059.9988",
                    highPrice = "471563.8600",
                    lowPrice = "470517.4600",
                    closePrice = "471483.1750",
                    volume = "30",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678261950000,
                    startPrice = "471479.4900",
                    highPrice = "472238.8588",
                    lowPrice = "471200.1200",
                    closePrice = "471989.4600",
                    volume = "30",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678261900000,
                    startPrice = "471989.4600",
                    highPrice = "472379.9988",
                    lowPrice = "470815.1200",
                    closePrice = "470815.1200",
                    volume = "30",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678261800000,
                    startPrice = "471239.9988",
                    highPrice = "472058.8588",
                    lowPrice = "470725.1200",
                    closePrice = "471631.9300",
                    volume = "30",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678261700000,
                    startPrice = "471658.2350",
                    highPrice = "472433.8588",
                    lowPrice = "471500.0000",
                    closePrice = "471500.0100",
                    volume = "30",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678261500000,
                    startPrice = "472208.8588",
                    highPrice = "473794.9888",
                    lowPrice = "471974.4950",
                    closePrice = "473794.9888",
                    volume = "30",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678261000000,
                    startPrice = "473447.5650",
                    highPrice = "473993.8488",
                    lowPrice = "473046.5300",
                    closePrice = "473046.5300",
                    volume = "30",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678260000000,
                    startPrice = "472870.3600",
                    highPrice = "473453.8600",
                    lowPrice = "471972.4700",
                    closePrice = "472598.8600",
                    volume = "30",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678242279000,
                    startPrice = "472658.8600",
                    highPrice = "473257.5500",
                    lowPrice = "472551.9300",
                    closePrice = "473257.5500",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678241566000,
                    startPrice = "473110.6950",
                    highPrice = "473453.8588",
                    lowPrice = "472610.1700",
                    closePrice = "473414.9900",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678241084000,
                    startPrice = "473138.8588",
                    highPrice = "473678.8600",
                    lowPrice = "472536.9300",
                    closePrice = "472536.9300",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678240451000,
                    startPrice = "472536.9300",
                    highPrice = "473033.8588",
                    lowPrice = "472272.4700",
                    closePrice = "472845.0000",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678239809000,
                    startPrice = "472445.1300",
                    highPrice = "473633.8588",
                    lowPrice = "472445.1300",
                    closePrice = "472790.1300",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678239220000,
                    startPrice = "473054.4950",
                    highPrice = "473927.5588",
                    lowPrice = "472946.2800",
                    closePrice = "473533.7850",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678238565000,
                    startPrice = "473526.3150",
                    highPrice = "473526.3150",
                    lowPrice = "472135.0600",
                    closePrice = "472263.2200",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678238052000,
                    startPrice = "471733.0200",
                    highPrice = "473344.9900",
                    lowPrice = "471680.0100",
                    closePrice = "473114.9988",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678237649000,
                    startPrice = "472788.7800",
                    highPrice = "473670.0000",
                    lowPrice = "472025.0300",
                    closePrice = "472703.8588",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678237171000,
                    startPrice = "472444.1050",
                    highPrice = "473065.0000",
                    lowPrice = "470776.2600",
                    closePrice = "473065.0000",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678236805000,
                    startPrice = "473064.9988",
                    highPrice = "473747.6688",
                    lowPrice = "472686.0500",
                    closePrice = "472970.1300",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678236392000,
                    startPrice = "472716.9200",
                    highPrice = "473342.5488",
                    lowPrice = "472653.7500",
                    closePrice = "472988.1488",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678236070000,
                    startPrice = "473001.0888",
                    highPrice = "473001.0888",
                    lowPrice = "471889.4450",
                    closePrice = "472487.5488",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235786000,
                    startPrice = "472487.5488",
                    highPrice = "472684.9988",
                    lowPrice = "471513.3700",
                    closePrice = "471697.5100",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235563000,
                    startPrice = "471994.9888",
                    highPrice = "471994.9888",
                    lowPrice = "470450.0100",
                    closePrice = "471945.8288",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235411000,
                    startPrice = "470500.0000",
                    highPrice = "472404.4200",
                    lowPrice = "469639.0100",
                    closePrice = "472404.4200",
                    volume = "141",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235411000,
                    startPrice = "472404.4188",
                    highPrice = "472404.4400",
                    lowPrice = "469639.0100",
                    closePrice = "472404.4200",
                    volume = "43",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235411000,
                    startPrice = "472404.4400",
                    highPrice = "472404.4400",
                    lowPrice = "472404.4200",
                    closePrice = "472404.4200",
                    volume = "39",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235410000,
                    startPrice = "472404.4200",
                    highPrice = "472404.4400",
                    lowPrice = "469639.0100",
                    closePrice = "472404.4400",
                    volume = "199",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235409000,
                    startPrice = "472404.4388",
                    highPrice = "472404.4400",
                    lowPrice = "469639.0100",
                    closePrice = "472404.4400",
                    volume = "279",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235408000,
                    startPrice = "472404.4400",
                    highPrice = "472404.4400",
                    lowPrice = "469639.0100",
                    closePrice = "472404.4388",
                    volume = "48",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235408000,
                    startPrice = "472404.4400",
                    highPrice = "472404.4400",
                    lowPrice = "469639.0100",
                    closePrice = "472404.4400",
                    volume = "52",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235408000,
                    startPrice = "472404.4388",
                    highPrice = "472404.4400",
                    lowPrice = "469639.0100",
                    closePrice = "472404.4400",
                    volume = "36",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235408000,
                    startPrice = "472404.4388",
                    highPrice = "472404.4500",
                    lowPrice = "469639.0100",
                    closePrice = "472404.4500",
                    volume = "64",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235408000,
                    startPrice = "472404.4400",
                    highPrice = "472404.4600",
                    lowPrice = "469639.0100",
                    closePrice = "472404.4588",
                    volume = "31",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235408000,
                    startPrice = "472404.4600",
                    highPrice = "472404.4600",
                    lowPrice = "469639.0100",
                    closePrice = "472404.4400",
                    volume = "38",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235408000,
                    startPrice = "472404.4400",
                    highPrice = "472404.4600",
                    lowPrice = "469639.0100",
                    closePrice = "472404.4500",
                    volume = "31",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235407000,
                    startPrice = "472404.4488",
                    highPrice = "472404.4600",
                    lowPrice = "469639.0100",
                    closePrice = "472404.4588",
                    volume = "37",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235407000,
                    startPrice = "472404.4600",
                    highPrice = "472404.4600",
                    lowPrice = "469639.0100",
                    closePrice = "472404.4600",
                    volume = "37",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235407000,
                    startPrice = "472404.4600",
                    highPrice = "472404.4600",
                    lowPrice = "469639.0100",
                    closePrice = "472404.4600",
                    volume = "49",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235407000,
                    startPrice = "472404.4600",
                    highPrice = "472404.4600",
                    lowPrice = "469639.0100",
                    closePrice = "472404.4588",
                    volume = "38",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235407000,
                    startPrice = "472404.4600",
                    highPrice = "472404.4600",
                    lowPrice = "469639.0100",
                    closePrice = "469639.0100",
                    volume = "39",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235407000,
                    startPrice = "469639.0100",
                    highPrice = "472929.4400",
                    lowPrice = "469639.0100",
                    closePrice = "472929.4400",
                    volume = "34",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235407000,
                    startPrice = "472404.4588",
                    highPrice = "472929.4400",
                    lowPrice = "469639.0100",
                    closePrice = "472404.4600",
                    volume = "34",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235406000,
                    startPrice = "472929.4388",
                    highPrice = "472929.4400",
                    lowPrice = "469639.0100",
                    closePrice = "472929.4388",
                    volume = "38",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235406000,
                    startPrice = "472929.4400",
                    highPrice = "472929.4400",
                    lowPrice = "469639.0100",
                    closePrice = "472929.4400",
                    volume = "48",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235406000,
                    startPrice = "469639.0100",
                    highPrice = "472929.4400",
                    lowPrice = "469639.0100",
                    closePrice = "472929.4400",
                    volume = "49",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235406000,
                    startPrice = "472929.4388",
                    highPrice = "472929.4400",
                    lowPrice = "469639.0100",
                    closePrice = "472929.4388",
                    volume = "41",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235406000,
                    startPrice = "472929.4400",
                    highPrice = "472929.4400",
                    lowPrice = "469639.0100",
                    closePrice = "472929.4388",
                    volume = "41",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235406000,
                    startPrice = "469639.0100",
                    highPrice = "472929.4400",
                    lowPrice = "469639.0100",
                    closePrice = "472929.4400",
                    volume = "34",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235405000,
                    startPrice = "472929.4388",
                    highPrice = "472929.4400",
                    lowPrice = "469639.0100",
                    closePrice = "472929.4388",
                    volume = "36",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235405000,
                    startPrice = "472929.4400",
                    highPrice = "472929.4400",
                    lowPrice = "469639.0100",
                    closePrice = "472929.4388",
                    volume = "39",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235405000,
                    startPrice = "472929.4400",
                    highPrice = "472929.4400",
                    lowPrice = "469639.0100",
                    closePrice = "472929.4388",
                    volume = "43",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235405000,
                    startPrice = "472929.4400",
                    highPrice = "472929.4400",
                    lowPrice = "469639.0100",
                    closePrice = "472929.4400",
                    volume = "33",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235405000,
                    startPrice = "472929.4388",
                    highPrice = "472929.4400",
                    lowPrice = "469639.0100",
                    closePrice = "472929.4388",
                    volume = "38",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235405000,
                    startPrice = "472929.4400",
                    highPrice = "472929.4600",
                    lowPrice = "472929.4388",
                    closePrice = "472929.4388",
                    volume = "34",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235405000,
                    startPrice = "472929.4388",
                    highPrice = "472929.4600",
                    lowPrice = "469639.0100",
                    closePrice = "472929.4600",
                    volume = "31",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235405000,
                    startPrice = "472929.4600",
                    highPrice = "472929.4600",
                    lowPrice = "469639.0100",
                    closePrice = "472929.4600",
                    volume = "36",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235404000,
                    startPrice = "472929.4600",
                    highPrice = "472929.4600",
                    lowPrice = "469639.0100",
                    closePrice = "472929.4600",
                    volume = "39",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235404000,
                    startPrice = "472929.4600",
                    highPrice = "472929.4600",
                    lowPrice = "472929.4588",
                    closePrice = "472929.4600",
                    volume = "36",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235404000,
                    startPrice = "472929.4588",
                    highPrice = "474079.4288",
                    lowPrice = "469639.0100",
                    closePrice = "472929.4600",
                    volume = "53",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235404000,
                    startPrice = "472929.4600",
                    highPrice = "474129.4100",
                    lowPrice = "469639.0100",
                    closePrice = "469639.0100",
                    volume = "36",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235404000,
                    startPrice = "472929.4600",
                    highPrice = "474129.4100",
                    lowPrice = "469639.0100",
                    closePrice = "472929.4600",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235404000,
                    startPrice = "474129.4100",
                    highPrice = "474129.4100",
                    lowPrice = "472929.4600",
                    closePrice = "474129.4088",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235404000,
                    startPrice = "472929.4600",
                    highPrice = "474129.4100",
                    lowPrice = "472929.4600",
                    closePrice = "474129.4100",
                    volume = "39",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235403000,
                    startPrice = "474129.4100",
                    highPrice = "474129.4100",
                    lowPrice = "469639.0100",
                    closePrice = "474129.4100",
                    volume = "105",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235402000,
                    startPrice = "474129.4100",
                    highPrice = "474129.4300",
                    lowPrice = "469100.0000",
                    closePrice = "474129.4300",
                    volume = "291",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235401000,
                    startPrice = "468896.0200",
                    highPrice = "474129.4300",
                    lowPrice = "468896.0200",
                    closePrice = "474129.4300",
                    volume = "278",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235400000,
                    startPrice = "471518.2150",
                    highPrice = "474129.4300",
                    lowPrice = "471518.2150",
                    closePrice = "471518.2150",
                    volume = "44",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235300000,
                    startPrice = "471541.7150",
                    highPrice = "471827.5900",
                    lowPrice = "471415.3850",
                    closePrice = "471827.5900",
                    volume = "37",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235100000,
                    startPrice = "471827.5850",
                    highPrice = "473160.3450",
                    lowPrice = "471663.2800",
                    closePrice = "473160.3450",
                    volume = "30",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678233900000,
                    startPrice = "473273.6150",
                    highPrice = "473285.0000",
                    lowPrice = "471990.0000",
                    closePrice = "472382.5588",
                    volume = "30",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678233600000,
                    startPrice = "472215.0100",
                    highPrice = "472421.6588",
                    lowPrice = "471596.1000",
                    closePrice = "471822.0400",
                    volume = "30",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678233200000,
                    startPrice = "472045.0000",
                    highPrice = "472045.0000",
                    lowPrice = "470700.0100",
                    closePrice = "471172.8588",
                    volume = "30",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678233000000,
                    startPrice = "471172.8588",
                    highPrice = "471172.8588",
                    lowPrice = "470255.1200",
                    closePrice = "470856.0750",
                    volume = "30",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678232700000,
                    startPrice = "470717.4100",
                    highPrice = "471490.2288",
                    lowPrice = "470717.4100",
                    closePrice = "471186.2800",
                    volume = "30",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678232500000,
                    startPrice = "471050.0000",
                    highPrice = "471551.9888",
                    lowPrice = "470870.1200",
                    closePrice = "471551.4988",
                    volume = "31",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678232000000,
                    startPrice = "471346.9050",
                    highPrice = "471569.9988",
                    lowPrice = "470717.4100",
                    closePrice = "471231.6800",
                    volume = "30",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678232100000,
                    startPrice = "471234.2950",
                    highPrice = "471575.4788",
                    lowPrice = "470719.6800",
                    closePrice = "470963.1500",
                    volume = "30",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678232000000,
                    startPrice = "470978.8100",
                    highPrice = "471257.8488",
                    lowPrice = "470289.0000",
                    closePrice = "471073.2000",
                    volume = "30",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678164527000,
                    startPrice = "471270.7888",
                    highPrice = "471596.4288",
                    lowPrice = "470764.9300",
                    closePrice = "471250.0000",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678163899000,
                    startPrice = "471713.5500",
                    highPrice = "472293.0588",
                    lowPrice = "471339.0000",
                    closePrice = "472051.4200",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678163485000,
                    startPrice = "472028.7665",
                    highPrice = "473115.0000",
                    lowPrice = "471452.4500",
                    closePrice = "473009.9900",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678162756000,
                    startPrice = "473010.0000",
                    highPrice = "473038.8500",
                    lowPrice = "472297.3100",
                    closePrice = "472760.6200",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678162357000,
                    startPrice = "472864.8450",
                    highPrice = "473526.6188",
                    lowPrice = "472625.0000",
                    closePrice = "473222.1200",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678161657000,
                    startPrice = "472990.0100",
                    highPrice = "474207.0788",
                    lowPrice = "472570.0100",
                    closePrice = "473404.2750",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678161052000,
                    startPrice = "473826.0200",
                    highPrice = "474616.4800",
                    lowPrice = "473230.0100",
                    closePrice = "474532.8950",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678160489000,
                    startPrice = "474537.1200",
                    highPrice = "474699.0788",
                    lowPrice = "473755.0100",
                    closePrice = "474180.0000",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678159842000,
                    startPrice = "474484.5450",
                    highPrice = "475146.2988",
                    lowPrice = "474310.0000",
                    closePrice = "475057.6288",
                    volume = "31",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678159161000,
                    startPrice = "474701.4950",
                    highPrice = "476001.3350",
                    lowPrice = "474310.0100",
                    closePrice = "474499.1700",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678158566000,
                    startPrice = "474441.4200",
                    highPrice = "474441.4200",
                    lowPrice = "473542.3800",
                    closePrice = "473714.6650",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678158076000,
                    startPrice = "473714.6650",
                    highPrice = "474426.9500",
                    lowPrice = "473364.1300",
                    closePrice = "473800.8300",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678157576000,
                    startPrice = "474033.6650",
                    highPrice = "474765.2888",
                    lowPrice = "473796.2900",
                    closePrice = "474499.5500",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678157051000,
                    startPrice = "475132.0488",
                    highPrice = "475421.7288",
                    lowPrice = "474454.4300",
                    closePrice = "474460.2500",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678156646000,
                    startPrice = "474128.5700",
                    highPrice = "474897.0088",
                    lowPrice = "473878.3400",
                    closePrice = "474543.7250",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678156434000,
                    startPrice = "474485.9250",
                    highPrice = "474656.8888",
                    lowPrice = "473952.7800",
                    closePrice = "474656.8888",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678156053000,
                    startPrice = "474380.7750",
                    highPrice = "475137.0788",
                    lowPrice = "474172.4600",
                    closePrice = "474882.8300",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678155587000,
                    startPrice = "474989.6300",
                    highPrice = "475279.9488",
                    lowPrice = "474625.0000",
                    closePrice = "475279.9488",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678155031000,
                    startPrice = "475000.0000",
                    highPrice = "476217.2488",
                    lowPrice = "475000.0000",
                    closePrice = "476217.2488",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678154542000,
                    startPrice = "475502.6700",
                    highPrice = "476217.5288",
                    lowPrice = "475300.0000",
                    closePrice = "475977.5600",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678154192000,
                    startPrice = "476036.9988",
                    highPrice = "476261.1200",
                    lowPrice = "475250.0000",
                    closePrice = "475350.0100",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678153670000,
                    startPrice = "475998.3100",
                    highPrice = "477294.0300",
                    lowPrice = "475583.4500",
                    closePrice = "476846.1850",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678152952000,
                    startPrice = "476612.4600",
                    highPrice = "478014.4250",
                    lowPrice = "476292.8350",
                    closePrice = "477592.3100",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678152479000,
                    startPrice = "477838.3750",
                    highPrice = "478319.9988",
                    lowPrice = "476949.9000",
                    closePrice = "477010.3400",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678152044000,
                    startPrice = "477535.1288",
                    highPrice = "478049.9888",
                    lowPrice = "476190.2900",
                    closePrice = "476753.9888",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678151669000,
                    startPrice = "476753.9888",
                    highPrice = "476952.9088",
                    lowPrice = "476212.2850",
                    closePrice = "476670.9988",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678151297000,
                    startPrice = "476352.1450",
                    highPrice = "476590.4800",
                    lowPrice = "475288.3000",
                    closePrice = "476404.5950",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678150842000,
                    startPrice = "476404.5950",
                    highPrice = "477797.0950",
                    lowPrice = "476121.5400",
                    closePrice = "477797.0950",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678150499000,
                    startPrice = "477797.0950",
                    highPrice = "479099.9988",
                    lowPrice = "477797.0950",
                    closePrice = "478685.6800",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678150233000,
                    startPrice = "478700.6700",
                    highPrice = "479224.9500",
                    lowPrice = "478202.4500",
                    closePrice = "478264.0900",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149915000,
                    startPrice = "478376.5950",
                    highPrice = "478376.5950",
                    lowPrice = "477385.1500",
                    closePrice = "477854.9988",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149622000,
                    startPrice = "477854.9588",
                    highPrice = "478550.9100",
                    lowPrice = "477377.5100",
                    closePrice = "478383.9900",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149420000,
                    startPrice = "478394.8950",
                    highPrice = "478664.9988",
                    lowPrice = "477692.4500",
                    closePrice = "477692.4500",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149213000,
                    startPrice = "478344.3888",
                    highPrice = "479371.2500",
                    lowPrice = "477377.4800",
                    closePrice = "479371.2500",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149013000,
                    startPrice = "479371.2500",
                    highPrice = "480940.0000",
                    lowPrice = "478702.4500",
                    closePrice = "479683.2388",
                    volume = "81",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149011000,
                    startPrice = "479683.2388",
                    highPrice = "479683.2400",
                    lowPrice = "478887.0000",
                    closePrice = "479683.2400",
                    volume = "198",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149010000,
                    startPrice = "479683.2388",
                    highPrice = "479683.2400",
                    lowPrice = "478887.0000",
                    closePrice = "479683.2400",
                    volume = "214",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149010000,
                    startPrice = "479683.2400",
                    highPrice = "479683.2400",
                    lowPrice = "478887.0000",
                    closePrice = "479683.2388",
                    volume = "50",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149010000,
                    startPrice = "479683.2400",
                    highPrice = "479683.2400",
                    lowPrice = "478887.0000",
                    closePrice = "479683.2400",
                    volume = "91",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149009000,
                    startPrice = "479683.2400",
                    highPrice = "479683.2400",
                    lowPrice = "478831.0100",
                    closePrice = "479683.2388",
                    volume = "56",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149009000,
                    startPrice = "479683.2400",
                    highPrice = "479683.2400",
                    lowPrice = "478831.0100",
                    closePrice = "478887.0000",
                    volume = "47",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149009000,
                    startPrice = "479683.2400",
                    highPrice = "479683.2400",
                    lowPrice = "478887.0000",
                    closePrice = "479683.2400",
                    volume = "71",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149009000,
                    startPrice = "478831.0000",
                    highPrice = "479683.2400",
                    lowPrice = "478831.0000",
                    closePrice = "479683.2388",
                    volume = "49",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149009000,
                    startPrice = "479683.2400",
                    highPrice = "479683.2400",
                    lowPrice = "478831.0100",
                    closePrice = "479683.2400",
                    volume = "44",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149008000,
                    startPrice = "479683.2400",
                    highPrice = "479683.2400",
                    lowPrice = "478831.0000",
                    closePrice = "479683.2400",
                    volume = "43",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149008000,
                    startPrice = "479683.2400",
                    highPrice = "479683.2400",
                    lowPrice = "478831.0000",
                    closePrice = "479683.2400",
                    volume = "32",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149008000,
                    startPrice = "479683.2388",
                    highPrice = "479683.2400",
                    lowPrice = "478831.0000",
                    closePrice = "479683.2400",
                    volume = "32",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149008000,
                    startPrice = "479683.2400",
                    highPrice = "479683.2400",
                    lowPrice = "478831.0000",
                    closePrice = "479683.2388",
                    volume = "38",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149008000,
                    startPrice = "479683.2400",
                    highPrice = "479683.2400",
                    lowPrice = "478831.0000",
                    closePrice = "479683.2388",
                    volume = "34",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149008000,
                    startPrice = "478831.0000",
                    highPrice = "479683.2400",
                    lowPrice = "478792.0100",
                    closePrice = "479683.2400",
                    volume = "41",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149007000,
                    startPrice = "479683.2388",
                    highPrice = "479683.2400",
                    lowPrice = "478792.0100",
                    closePrice = "479683.2400",
                    volume = "38",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149007000,
                    startPrice = "479683.2388",
                    highPrice = "479683.2400",
                    lowPrice = "479683.2388",
                    closePrice = "479683.2400",
                    volume = "36",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149007000,
                    startPrice = "479683.2400",
                    highPrice = "479683.2400",
                    lowPrice = "478792.0100",
                    closePrice = "479683.2400",
                    volume = "37",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149007000,
                    startPrice = "479683.2388",
                    highPrice = "479683.2400",
                    lowPrice = "478792.0100",
                    closePrice = "479683.2400",
                    volume = "37",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149007000,
                    startPrice = "479683.2400",
                    highPrice = "479683.2400",
                    lowPrice = "478831.0000",
                    closePrice = "479683.2400",
                    volume = "34",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149007000,
                    startPrice = "479683.2388",
                    highPrice = "479683.2400",
                    lowPrice = "478792.0100",
                    closePrice = "479683.2400",
                    volume = "34",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149006000,
                    startPrice = "479683.2400",
                    highPrice = "479683.2400",
                    lowPrice = "478792.0100",
                    closePrice = "479683.2388",
                    volume = "37",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149006000,
                    startPrice = "479683.2400",
                    highPrice = "479683.2400",
                    lowPrice = "478792.0100",
                    closePrice = "479683.2388",
                    volume = "40",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149006000,
                    startPrice = "479683.2388",
                    highPrice = "479683.2400",
                    lowPrice = "478792.0100",
                    closePrice = "479683.2400",
                    volume = "49",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149006000,
                    startPrice = "479683.2400",
                    highPrice = "479683.2400",
                    lowPrice = "478792.0100",
                    closePrice = "479683.2388",
                    volume = "50",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149006000,
                    startPrice = "479683.2400",
                    highPrice = "479683.2400",
                    lowPrice = "479683.2388",
                    closePrice = "479683.2400",
                    volume = "46",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149005000,
                    startPrice = "479683.2388",
                    highPrice = "479683.2400",
                    lowPrice = "478792.0100",
                    closePrice = "479683.2388",
                    volume = "57",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149005000,
                    startPrice = "479683.2388",
                    highPrice = "479683.2400",
                    lowPrice = "478792.0000",
                    closePrice = "479683.2388",
                    volume = "44",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149005000,
                    startPrice = "479683.2400",
                    highPrice = "479683.2400",
                    lowPrice = "478762.0000",
                    closePrice = "479683.2388",
                    volume = "36",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149005000,
                    startPrice = "479683.2388",
                    highPrice = "481493.7700",
                    lowPrice = "478762.0000",
                    closePrice = "481493.7700",
                    volume = "36",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149005000,
                    startPrice = "479683.2388",
                    highPrice = "481493.7700",
                    lowPrice = "479683.2388",
                    closePrice = "481493.7688",
                    volume = "33",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149005000,
                    startPrice = "481493.7700",
                    highPrice = "481493.7700",
                    lowPrice = "478916.0000",
                    closePrice = "481493.7700",
                    volume = "34",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149004000,
                    startPrice = "481493.7700",
                    highPrice = "481493.7700",
                    lowPrice = "478916.0000",
                    closePrice = "481493.7700",
                    volume = "38",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149004000,
                    startPrice = "481493.7688",
                    highPrice = "481493.7700",
                    lowPrice = "478916.0000",
                    closePrice = "481493.7700",
                    volume = "36",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149004000,
                    startPrice = "481493.7700",
                    highPrice = "481493.7700",
                    lowPrice = "478916.0000",
                    closePrice = "481493.7700",
                    volume = "33",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149004000,
                    startPrice = "481493.7688",
                    highPrice = "481493.7700",
                    lowPrice = "478916.0000",
                    closePrice = "481493.7700",
                    volume = "37",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149004000,
                    startPrice = "481493.7700",
                    highPrice = "481493.7700",
                    lowPrice = "478916.0000",
                    closePrice = "481493.7688",
                    volume = "35",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149004000,
                    startPrice = "481493.7700",
                    highPrice = "481493.7700",
                    lowPrice = "478916.0000",
                    closePrice = "481493.7700",
                    volume = "44",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149004000,
                    startPrice = "481493.7700",
                    highPrice = "481493.7700",
                    lowPrice = "478916.0000",
                    closePrice = "481493.7688",
                    volume = "38",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149003000,
                    startPrice = "481493.7688",
                    highPrice = "481493.7700",
                    lowPrice = "478077.0000",
                    closePrice = "481493.7688",
                    volume = "37",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149003000,
                    startPrice = "481493.7688",
                    highPrice = "481493.7700",
                    lowPrice = "477839.0000",
                    closePrice = "481493.7700",
                    volume = "37",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149003000,
                    startPrice = "481493.7688",
                    highPrice = "481493.7700",
                    lowPrice = "477839.0000",
                    closePrice = "481493.7688",
                    volume = "40",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149002000,
                    startPrice = "481493.7700",
                    highPrice = "481493.7700",
                    lowPrice = "477681.0000",
                    closePrice = "481493.7700",
                    volume = "266",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149002000,
                    startPrice = "481493.7688",
                    highPrice = "481493.7700",
                    lowPrice = "477681.0000",
                    closePrice = "479733.2450",
                    volume = "47",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149002000,
                    startPrice = "481493.7700",
                    highPrice = "481493.7700",
                    lowPrice = "477811.0000",
                    closePrice = "481493.7700",
                    volume = "145",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678149001000,
                    startPrice = "481493.7700",
                    highPrice = "481824.4400",
                    lowPrice = "478017.0000",
                    closePrice = "481824.4400",
                    volume = "198",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678085843000,
                    startPrice = "481824.4400",
                    highPrice = "481824.4400",
                    lowPrice = "478017.0000",
                    closePrice = "479835.0200",
                    volume = "106",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678085206000,
                    startPrice = "479915.1300",
                    highPrice = "480757.5388",
                    lowPrice = "479835.0100",
                    closePrice = "480757.5388",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678084617000,
                    startPrice = "480585.8700",
                    highPrice = "480944.9988",
                    lowPrice = "479724.0700",
                    closePrice = "479724.0700",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678083993000,
                    startPrice = "479847.6550",
                    highPrice = "479989.0688",
                    lowPrice = "479372.5550",
                    closePrice = "479564.1388",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678083073000,
                    startPrice = "479282.7600",
                    highPrice = "479677.5388",
                    lowPrice = "478660.7600",
                    closePrice = "479072.9950",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678082475000,
                    startPrice = "479272.1788",
                    highPrice = "479289.9988",
                    lowPrice = "478663.7500",
                    closePrice = "478868.9800",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678081690000,
                    startPrice = "478798.4200",
                    highPrice = "479464.8588",
                    lowPrice = "478798.4200",
                    closePrice = "479464.8588",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678081079000,
                    startPrice = "479210.0450",
                    highPrice = "479632.5488",
                    lowPrice = "478500.0100",
                    closePrice = "478866.5588",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678080342000,
                    startPrice = "478470.0100",
                    highPrice = "478959.7888",
                    lowPrice = "478262.4600",
                    closePrice = "478262.4600",
                    volume = "33",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678079763000,
                    startPrice = "478620.3188",
                    highPrice = "478980.8388",
                    lowPrice = "478082.4500",
                    closePrice = "478980.8388",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678079252000,
                    startPrice = "478751.9500",
                    highPrice = "479121.4500",
                    lowPrice = "478215.0200",
                    closePrice = "478331.6150",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678078680000,
                    startPrice = "478508.2088",
                    highPrice = "478508.2088",
                    lowPrice = "477482.4600",
                    closePrice = "477557.4600",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678078125000,
                    startPrice = "477789.0488",
                    highPrice = "477880.9388",
                    lowPrice = "477335.0000",
                    closePrice = "477568.3950",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678077498000,
                    startPrice = "477799.0888",
                    highPrice = "478120.9388",
                    lowPrice = "477390.0100",
                    closePrice = "477811.3888",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678076961000,
                    startPrice = "477865.9388",
                    highPrice = "478084.9900",
                    lowPrice = "477280.0400",
                    closePrice = "477843.6900",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678076320000,
                    startPrice = "478204.1288",
                    highPrice = "478375.9388",
                    lowPrice = "477728.0700",
                    closePrice = "478375.9388",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678075853000,
                    startPrice = "478041.9100",
                    highPrice = "478214.2900",
                    lowPrice = "477432.2500",
                    closePrice = "477713.2000",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678075252000,
                    startPrice = "477573.9300",
                    highPrice = "478741.3700",
                    lowPrice = "477568.1900",
                    closePrice = "478549.7150",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678074928000,
                    startPrice = "478548.8250",
                    highPrice = "478750.9400",
                    lowPrice = "477978.7000",
                    closePrice = "478176.6450",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678074232000,
                    startPrice = "478190.4800",
                    highPrice = "479155.9388",
                    lowPrice = "478190.4800",
                    closePrice = "479081.2950",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678073981000,
                    startPrice = "478802.0100",
                    highPrice = "479014.5400",
                    lowPrice = "478540.1200",
                    closePrice = "478673.8300",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678073633000,
                    startPrice = "478673.8300",
                    highPrice = "478869.9988",
                    lowPrice = "478303.6200",
                    closePrice = "478728.0050",
                    volume = "31",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678073079000,
                    startPrice = "478985.4150",
                    highPrice = "479722.5388",
                    lowPrice = "478855.8800",
                    closePrice = "479306.3700",
                    volume = "31",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678072527000,
                    startPrice = "479518.1800",
                    highPrice = "479890.9288",
                    lowPrice = "479292.1800",
                    closePrice = "479432.4200",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678072031000,
                    startPrice = "479500.0000",
                    highPrice = "479500.0000",
                    lowPrice = "478737.6750",
                    closePrice = "478749.2450",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678071614000,
                    startPrice = "478929.9888",
                    highPrice = "478929.9888",
                    lowPrice = "478258.1600",
                    closePrice = "478379.2500",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678071047000,
                    startPrice = "478212.2200",
                    highPrice = "478555.0000",
                    lowPrice = "477461.3500",
                    closePrice = "477698.1250",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678070541000,
                    startPrice = "477498.6300",
                    highPrice = "477944.9888",
                    lowPrice = "477251.2500",
                    closePrice = "477682.5400",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678069892000,
                    startPrice = "477504.9900",
                    highPrice = "477725.0000",
                    lowPrice = "476535.0000",
                    closePrice = "476913.7800",
                    volume = "30",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                )
            )
            chartDataList = list
        } else if (request.chartDivision == "5MINUTE") {
            val list = mutableListOf(
                KfitBaseChart(
                    dateTime = 1678266000000,
                    startPrice = "181.9900",
                    highPrice = "182.1100",
                    lowPrice = "181.5500",
                    closePrice = "182.0000",
                    volume = "2602695",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678265700000,
                    startPrice = "182.0950",
                    highPrice = "182.3800",
                    lowPrice = "181.8900",
                    closePrice = "181.9900",
                    volume = "897788",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678265400000,
                    startPrice = "182.0299",
                    highPrice = "182.3900",
                    lowPrice = "181.8200",
                    closePrice = "182.0950",
                    volume = "856416",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678265100000,
                    startPrice = "181.4500",
                    highPrice = "182.1100",
                    lowPrice = "181.4103",
                    closePrice = "182.0299",
                    volume = "769636",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678264800000,
                    startPrice = "180.8350",
                    highPrice = "181.7300",
                    lowPrice = "180.7100",
                    closePrice = "181.4500",
                    volume = "830677",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678264500000,
                    startPrice = "180.5100",
                    highPrice = "180.9700",
                    lowPrice = "180.5000",
                    closePrice = "180.8350",
                    volume = "505000",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678264200000,
                    startPrice = "180.5300",
                    highPrice = "180.6500",
                    lowPrice = "180.1500",
                    closePrice = "180.5100",
                    volume = "708506",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678263900000,
                    startPrice = "180.6400",
                    highPrice = "181.0800",
                    lowPrice = "180.5100",
                    closePrice = "180.5300",
                    volume = "520966",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678263600000,
                    startPrice = "180.8000",
                    highPrice = "180.9199",
                    lowPrice = "180.5100",
                    closePrice = "180.6400",
                    volume = "562732",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678263300000,
                    startPrice = "181.1300",
                    highPrice = "181.2700",
                    lowPrice = "180.7500",
                    closePrice = "180.8000",
                    volume = "423154",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678263000000,
                    startPrice = "181.1200",
                    highPrice = "181.2400",
                    lowPrice = "180.8100",
                    closePrice = "181.1300",
                    volume = "594019",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678262700000,
                    startPrice = "181.9404",
                    highPrice = "182.1200",
                    lowPrice = "181.1100",
                    closePrice = "181.1200",
                    volume = "651079",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678262400000,
                    startPrice = "182.0216",
                    highPrice = "182.2500",
                    lowPrice = "181.7200",
                    closePrice = "181.9404",
                    volume = "381095",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678262100000,
                    startPrice = "181.9650",
                    highPrice = "182.1500",
                    lowPrice = "181.6400",
                    closePrice = "182.0216",
                    volume = "452464",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678261800000,
                    startPrice = "181.9600",
                    highPrice = "182.2000",
                    lowPrice = "181.9000",
                    closePrice = "181.9650",
                    volume = "361716",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678261500000,
                    startPrice = "182.3100",
                    highPrice = "182.5799",
                    lowPrice = "181.8100",
                    closePrice = "181.9600",
                    volume = "535766",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678261200000,
                    startPrice = "181.5400",
                    highPrice = "182.4722",
                    lowPrice = "181.4600",
                    closePrice = "182.3100",
                    volume = "627936",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678260900000,
                    startPrice = "182.0200",
                    highPrice = "182.0600",
                    lowPrice = "181.4300",
                    closePrice = "181.5400",
                    volume = "394679",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678260600000,
                    startPrice = "181.3891",
                    highPrice = "182.0700",
                    lowPrice = "181.3700",
                    closePrice = "182.0200",
                    volume = "435024",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678260300000,
                    startPrice = "181.5900",
                    highPrice = "181.8100",
                    lowPrice = "181.3312",
                    closePrice = "181.3891",
                    volume = "420990",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678260000000,
                    startPrice = "181.6700",
                    highPrice = "181.9300",
                    lowPrice = "181.4700",
                    closePrice = "181.5900",
                    volume = "521331",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678259700000,
                    startPrice = "181.7200",
                    highPrice = "182.1800",
                    lowPrice = "181.6700",
                    closePrice = "181.6700",
                    volume = "738098",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678259400000,
                    startPrice = "181.1400",
                    highPrice = "181.8000",
                    lowPrice = "180.8800",
                    closePrice = "181.7200",
                    volume = "644840",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678259100000,
                    startPrice = "180.6300",
                    highPrice = "181.3370",
                    lowPrice = "180.6100",
                    closePrice = "181.1400",
                    volume = "535316",
                    amount = "0",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678258800000,
                    startPrice = "181.9900",
                    highPrice = "182.1100",
                    lowPrice = "181.5500",
                    closePrice = "182.0000",
                    volume = "2602695",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678258500000,
                    startPrice = "182.0950",
                    highPrice = "182.3800",
                    lowPrice = "181.8900",
                    closePrice = "181.9900",
                    volume = "897788",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678258200000,
                    startPrice = "182.0299",
                    highPrice = "182.3900",
                    lowPrice = "181.8200",
                    closePrice = "182.0950",
                    volume = "856416",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678257900000,
                    startPrice = "181.4500",
                    highPrice = "182.1100",
                    lowPrice = "181.4103",
                    closePrice = "182.0299",
                    volume = "769636",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678257600000,
                    startPrice = "180.8350",
                    highPrice = "181.7300",
                    lowPrice = "180.7100",
                    closePrice = "181.4500",
                    volume = "830677",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678257300000,
                    startPrice = "180.5100",
                    highPrice = "180.9700",
                    lowPrice = "180.5000",
                    closePrice = "180.8350",
                    volume = "505000",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678257000000,
                    startPrice = "180.5300",
                    highPrice = "180.6500",
                    lowPrice = "180.1500",
                    closePrice = "180.5100",
                    volume = "708506",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678256700000,
                    startPrice = "180.6400",
                    highPrice = "181.0800",
                    lowPrice = "180.5100",
                    closePrice = "180.5300",
                    volume = "520966",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678256400000,
                    startPrice = "180.8000",
                    highPrice = "180.9199",
                    lowPrice = "180.5100",
                    closePrice = "180.6400",
                    volume = "562732",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678256100000,
                    startPrice = "181.1300",
                    highPrice = "181.2700",
                    lowPrice = "180.7500",
                    closePrice = "180.8000",
                    volume = "423154",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678255800000,
                    startPrice = "181.1200",
                    highPrice = "181.2400",
                    lowPrice = "180.8100",
                    closePrice = "181.1300",
                    volume = "594019",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678255500000,
                    startPrice = "181.9404",
                    highPrice = "182.1200",
                    lowPrice = "181.1100",
                    closePrice = "181.1200",
                    volume = "651079",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678255200000,
                    startPrice = "182.0216",
                    highPrice = "182.2500",
                    lowPrice = "181.7200",
                    closePrice = "181.9404",
                    volume = "381095",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678254900000,
                    startPrice = "181.9650",
                    highPrice = "182.1500",
                    lowPrice = "181.6400",
                    closePrice = "182.0216",
                    volume = "452464",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678254600000,
                    startPrice = "181.9600",
                    highPrice = "182.2000",
                    lowPrice = "181.9000",
                    closePrice = "181.9650",
                    volume = "361716",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678254300000,
                    startPrice = "182.3100",
                    highPrice = "182.5799",
                    lowPrice = "181.8100",
                    closePrice = "181.9600",
                    volume = "535766",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678254000000,
                    startPrice = "181.5400",
                    highPrice = "182.4722",
                    lowPrice = "181.4600",
                    closePrice = "182.3100",
                    volume = "627936",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678253700000,
                    startPrice = "182.0200",
                    highPrice = "182.0600",
                    lowPrice = "181.4300",
                    closePrice = "181.5400",
                    volume = "394679",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678253400000,
                    startPrice = "181.3891",
                    highPrice = "182.0700",
                    lowPrice = "181.3700",
                    closePrice = "182.0200",
                    volume = "435024",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678253100000,
                    startPrice = "181.5900",
                    highPrice = "181.8100",
                    lowPrice = "181.3312",
                    closePrice = "181.3891",
                    volume = "420990",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678252800000,
                    startPrice = "181.6700",
                    highPrice = "181.9300",
                    lowPrice = "181.4700",
                    closePrice = "181.5900",
                    volume = "521331",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678252500000,
                    startPrice = "181.7200",
                    highPrice = "182.1800",
                    lowPrice = "181.6700",
                    closePrice = "181.6700",
                    volume = "738098",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678252200000,
                    startPrice = "181.1400",
                    highPrice = "181.8000",
                    lowPrice = "180.8800",
                    closePrice = "181.7200",
                    volume = "644840",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678251900000,
                    startPrice = "180.6300",
                    highPrice = "181.3370",
                    lowPrice = "180.6100",
                    closePrice = "181.1400",
                    volume = "535316",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678251600000,
                    startPrice = "181.0300",
                    highPrice = "181.1600",
                    lowPrice = "180.5500",
                    closePrice = "180.6600",
                    volume = "405005",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678251300000,
                    startPrice = "180.5000",
                    highPrice = "181.0400",
                    lowPrice = "180.3400",
                    closePrice = "181.0300",
                    volume = "408958",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678251000000,
                    startPrice = "180.5300",
                    highPrice = "180.7897",
                    lowPrice = "180.4500",
                    closePrice = "180.4614",
                    volume = "304348",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678250700000,
                    startPrice = "180.4700",
                    highPrice = "180.6300",
                    lowPrice = "180.2200",
                    closePrice = "180.5300",
                    volume = "450144",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678250400000,
                    startPrice = "181.0810",
                    highPrice = "181.3600",
                    lowPrice = "180.4611",
                    closePrice = "180.4744",
                    volume = "484037",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678250100000,
                    startPrice = "180.4300",
                    highPrice = "181.1000",
                    lowPrice = "180.3100",
                    closePrice = "181.0810",
                    volume = "535998",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678249800000,
                    startPrice = "180.6600",
                    highPrice = "180.8200",
                    lowPrice = "180.2700",
                    closePrice = "180.4300",
                    volume = "374845",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678249500000,
                    startPrice = "180.7487",
                    highPrice = "180.9687",
                    lowPrice = "180.5100",
                    closePrice = "180.6600",
                    volume = "339005",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678249200000,
                    startPrice = "180.6100",
                    highPrice = "180.9400",
                    lowPrice = "180.4900",
                    closePrice = "180.7487",
                    volume = "377497",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678248900000,
                    startPrice = "180.4161",
                    highPrice = "180.9800",
                    lowPrice = "180.1500",
                    closePrice = "180.6100",
                    volume = "579764",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678248600000,
                    startPrice = "180.2100",
                    highPrice = "180.6300",
                    lowPrice = "180.0000",
                    closePrice = "180.4161",
                    volume = "796525",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678248300000,
                    startPrice = "180.7000",
                    highPrice = "181.1300",
                    lowPrice = "180.1600",
                    closePrice = "180.2100",
                    volume = "704790",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678248000000,
                    startPrice = "180.7700",
                    highPrice = "180.7700",
                    lowPrice = "180.3200",
                    closePrice = "180.7000",
                    volume = "547631",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678247700000,
                    startPrice = "181.3199",
                    highPrice = "181.4000",
                    lowPrice = "180.4700",
                    closePrice = "180.7700",
                    volume = "572226",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678247400000,
                    startPrice = "181.0800",
                    highPrice = "181.3452",
                    lowPrice = "180.8000",
                    closePrice = "181.3199",
                    volume = "504116",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678247100000,
                    startPrice = "181.3500",
                    highPrice = "181.5800",
                    lowPrice = "181.0500",
                    closePrice = "181.0800",
                    volume = "305584",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678246800000,
                    startPrice = "181.2300",
                    highPrice = "181.5276",
                    lowPrice = "181.1200",
                    closePrice = "181.3500",
                    volume = "367838",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678246500000,
                    startPrice = "181.2549",
                    highPrice = "181.3100",
                    lowPrice = "180.8000",
                    closePrice = "181.2300",
                    volume = "585637",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678246200000,
                    startPrice = "181.8600",
                    highPrice = "182.0500",
                    lowPrice = "181.0900",
                    closePrice = "181.2600",
                    volume = "450234",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678245900000,
                    startPrice = "181.7200",
                    highPrice = "181.9687",
                    lowPrice = "181.5900",
                    closePrice = "181.8600",
                    volume = "259590",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678245600000,
                    startPrice = "182.0200",
                    highPrice = "182.2200",
                    lowPrice = "181.4462",
                    closePrice = "181.7200",
                    volume = "404490",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678245300000,
                    startPrice = "181.7600",
                    highPrice = "182.2400",
                    lowPrice = "181.7500",
                    closePrice = "182.0200",
                    volume = "384780",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678245000000,
                    startPrice = "181.8600",
                    highPrice = "182.0300",
                    lowPrice = "181.5200",
                    closePrice = "181.8100",
                    volume = "431372",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678244700000,
                    startPrice = "181.9700",
                    highPrice = "182.5400",
                    lowPrice = "181.7509",
                    closePrice = "181.8600",
                    volume = "624960",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678244400000,
                    startPrice = "182.0100",
                    highPrice = "182.1600",
                    lowPrice = "181.7000",
                    closePrice = "181.9700",
                    volume = "464421",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678244100000,
                    startPrice = "182.1097",
                    highPrice = "182.3500",
                    lowPrice = "181.8900",
                    closePrice = "182.0100",
                    volume = "568717",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678243800000,
                    startPrice = "181.6000",
                    highPrice = "182.2128",
                    lowPrice = "181.3500",
                    closePrice = "182.1097",
                    volume = "586951",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678243500000,
                    startPrice = "181.3300",
                    highPrice = "181.7800",
                    lowPrice = "181.2400",
                    closePrice = "181.6000",
                    volume = "595819",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678243200000,
                    startPrice = "181.1000",
                    highPrice = "181.3500",
                    lowPrice = "180.7700",
                    closePrice = "181.3300",
                    volume = "811390",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678242900000,
                    startPrice = "181.8401",
                    highPrice = "181.8898",
                    lowPrice = "181.1000",
                    closePrice = "181.1000",
                    volume = "673396",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678242600000,
                    startPrice = "182.0460",
                    highPrice = "182.4130",
                    lowPrice = "181.6500",
                    closePrice = "181.8401",
                    volume = "516821",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678242300000,
                    startPrice = "181.9300",
                    highPrice = "182.2500",
                    lowPrice = "181.7600",
                    closePrice = "182.0460",
                    volume = "543977",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678242000000,
                    startPrice = "182.7300",
                    highPrice = "182.9000",
                    lowPrice = "181.8600",
                    closePrice = "181.9300",
                    volume = "666335",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678241700000,
                    startPrice = "183.0799",
                    highPrice = "183.3000",
                    lowPrice = "182.4700",
                    closePrice = "182.7200",
                    volume = "651633",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678241400000,
                    startPrice = "182.2900",
                    highPrice = "183.3000",
                    lowPrice = "182.2900",
                    closePrice = "183.0799",
                    volume = "947869",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678241100000,
                    startPrice = "182.4100",
                    highPrice = "182.7600",
                    lowPrice = "182.0614",
                    closePrice = "182.2900",
                    volume = "653997",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678240800000,
                    startPrice = "182.0050",
                    highPrice = "182.6799",
                    lowPrice = "181.7300",
                    closePrice = "182.4100",
                    volume = "865732",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678240500000,
                    startPrice = "180.9000",
                    highPrice = "182.1500",
                    lowPrice = "180.8050",
                    closePrice = "181.9795",
                    volume = "793421",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678240200000,
                    startPrice = "181.5550",
                    highPrice = "181.9999",
                    lowPrice = "180.7600",
                    closePrice = "180.9100",
                    volume = "757777",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678239900000,
                    startPrice = "181.7750",
                    highPrice = "181.8700",
                    lowPrice = "180.9200",
                    closePrice = "181.5550",
                    volume = "751947",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678239600000,
                    startPrice = "182.2294",
                    highPrice = "182.2600",
                    lowPrice = "181.5300",
                    closePrice = "181.7750",
                    volume = "894814",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678239300000,
                    startPrice = "181.9400",
                    highPrice = "182.6000",
                    lowPrice = "181.3900",
                    closePrice = "182.2294",
                    volume = "1128425",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678239000000,
                    startPrice = "182.0204",
                    highPrice = "182.6900",
                    lowPrice = "181.7000",
                    closePrice = "181.9400",
                    volume = "1177082",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678238700000,
                    startPrice = "181.3773",
                    highPrice = "182.2670",
                    lowPrice = "180.2600",
                    closePrice = "182.0204",
                    volume = "1371849",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678238400000,
                    startPrice = "180.9700",
                    highPrice = "181.9000",
                    lowPrice = "180.7210",
                    closePrice = "181.3800",
                    volume = "980427",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678238100000,
                    startPrice = "181.4300",
                    highPrice = "182.2000",
                    lowPrice = "180.9100",
                    closePrice = "180.9700",
                    volume = "1352601",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678237800000,
                    startPrice = "180.4900",
                    highPrice = "181.6850",
                    lowPrice = "180.0701",
                    closePrice = "181.4300",
                    volume = "1501075",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678237500000,
                    startPrice = "181.4800",
                    highPrice = "181.4900",
                    lowPrice = "180.0000",
                    closePrice = "180.4900",
                    volume = "2197621",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678237200000,
                    startPrice = "181.3200",
                    highPrice = "182.2000",
                    lowPrice = "180.6611",
                    closePrice = "181.4800",
                    volume = "1793443",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678236900000,
                    startPrice = "181.4400",
                    highPrice = "182.3400",
                    lowPrice = "181.0100",
                    closePrice = "181.3200",
                    volume = "1608927",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678236600000,
                    startPrice = "182.4850",
                    highPrice = "182.8690",
                    lowPrice = "181.2900",
                    closePrice = "181.4400",
                    volume = "2067753",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678236300000,
                    startPrice = "183.1000",
                    highPrice = "184.0400",
                    lowPrice = "182.3600",
                    closePrice = "182.4850",
                    volume = "1673256",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678236000000,
                    startPrice = "183.6400",
                    highPrice = "183.6400",
                    lowPrice = "182.4886",
                    closePrice = "183.1000",
                    volume = "1922112",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235700000,
                    startPrice = "184.8400",
                    highPrice = "186.5000",
                    lowPrice = "183.5200",
                    closePrice = "183.6400",
                    volume = "2811862",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235400000,
                    startPrice = "185.0400",
                    highPrice = "185.3000",
                    lowPrice = "184.5600",
                    closePrice = "185.0400",
                    volume = "68383",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678235100000,
                    startPrice = "185.0500",
                    highPrice = "185.2000",
                    lowPrice = "184.8000",
                    closePrice = "184.8000",
                    volume = "38084",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678234800000,
                    startPrice = "184.7400",
                    highPrice = "185.0985",
                    lowPrice = "184.7200",
                    closePrice = "185.0000",
                    volume = "37356",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678234500000,
                    startPrice = "185.1700",
                    highPrice = "185.2800",
                    lowPrice = "184.7600",
                    closePrice = "184.8000",
                    volume = "46708",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678234200000,
                    startPrice = "185.4400",
                    highPrice = "185.4800",
                    lowPrice = "185.0000",
                    closePrice = "185.1500",
                    volume = "28188",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678233900000,
                    startPrice = "185.2500",
                    highPrice = "185.5000",
                    lowPrice = "185.2500",
                    closePrice = "185.4800",
                    volume = "13998",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678233600000,
                    startPrice = "185.3600",
                    highPrice = "185.4300",
                    lowPrice = "185.1800",
                    closePrice = "185.3500",
                    volume = "11869",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678233300000,
                    startPrice = "185.6000",
                    highPrice = "185.6200",
                    lowPrice = "185.2500",
                    closePrice = "185.3600",
                    volume = "15806",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678233000000,
                    startPrice = "185.8226",
                    highPrice = "186.0200",
                    lowPrice = "185.3000",
                    closePrice = "185.5600",
                    volume = "35636",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678232700000,
                    startPrice = "185.5450",
                    highPrice = "186.1400",
                    lowPrice = "185.4000",
                    closePrice = "185.9100",
                    volume = "45033",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678232400000,
                    startPrice = "185.6500",
                    highPrice = "185.9000",
                    lowPrice = "185.2000",
                    closePrice = "185.5300",
                    volume = "33061",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678232100000,
                    startPrice = "185.0700",
                    highPrice = "193.7350",
                    lowPrice = "184.9000",
                    closePrice = "185.6000",
                    volume = "38918",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678231800000,
                    startPrice = "185.1200",
                    highPrice = "185.5400",
                    lowPrice = "184.9100",
                    closePrice = "185.0250",
                    volume = "24472",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678231500000,
                    startPrice = "185.0100",
                    highPrice = "185.5800",
                    lowPrice = "184.7700",
                    closePrice = "185.1850",
                    volume = "48369",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678231200000,
                    startPrice = "185.4300",
                    highPrice = "185.4600",
                    lowPrice = "184.6900",
                    closePrice = "185.0700",
                    volume = "35716",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678230900000,
                    startPrice = "185.3700",
                    highPrice = "185.5900",
                    lowPrice = "185.0800",
                    closePrice = "185.4385",
                    volume = "26181",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678230600000,
                    startPrice = "185.0500",
                    highPrice = "185.6000",
                    lowPrice = "184.8000",
                    closePrice = "185.3700",
                    volume = "35626",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678230300000,
                    startPrice = "186.7000",
                    highPrice = "187.2500",
                    lowPrice = "184.3615",
                    closePrice = "185.0350",
                    volume = "147518",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678230000000,
                    startPrice = "184.9000",
                    highPrice = "187.2000",
                    lowPrice = "184.3605",
                    closePrice = "186.5500",
                    volume = "52259",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678229700000,
                    startPrice = "184.8100",
                    highPrice = "185.3900",
                    lowPrice = "184.8000",
                    closePrice = "184.8800",
                    volume = "17200",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678229400000,
                    startPrice = "184.5100",
                    highPrice = "184.9600",
                    lowPrice = "184.3700",
                    closePrice = "184.8300",
                    volume = "27810",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678229100000,
                    startPrice = "185.0600",
                    highPrice = "185.0700",
                    lowPrice = "184.5100",
                    closePrice = "184.5100",
                    volume = "40038",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678228800000,
                    startPrice = "185.3000",
                    highPrice = "185.5000",
                    lowPrice = "185.0000",
                    closePrice = "185.0000",
                    volume = "25037",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678228500000,
                    startPrice = "185.3800",
                    highPrice = "185.4400",
                    lowPrice = "185.1100",
                    closePrice = "185.3000",
                    volume = "15133",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678228200000,
                    startPrice = "185.6100",
                    highPrice = "185.8700",
                    lowPrice = "185.2500",
                    closePrice = "185.3800",
                    volume = "16577",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678227900000,
                    startPrice = "186.3700",
                    highPrice = "186.3700",
                    lowPrice = "185.5300",
                    closePrice = "185.5700",
                    volume = "20480",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678227600000,
                    startPrice = "186.5400",
                    highPrice = "186.5400",
                    lowPrice = "186.2400",
                    closePrice = "186.3500",
                    volume = "11571",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678227300000,
                    startPrice = "186.6500",
                    highPrice = "186.9000",
                    lowPrice = "186.4000",
                    closePrice = "186.4600",
                    volume = "11769",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678227000000,
                    startPrice = "187.1400",
                    highPrice = "187.1700",
                    lowPrice = "186.4800",
                    closePrice = "186.5500",
                    volume = "12575",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678226700000,
                    startPrice = "186.8200",
                    highPrice = "187.2300",
                    lowPrice = "186.4900",
                    closePrice = "187.1800",
                    volume = "19115",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678226400000,
                    startPrice = "186.6800",
                    highPrice = "186.8800",
                    lowPrice = "186.3300",
                    closePrice = "186.8800",
                    volume = "6862",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678226100000,
                    startPrice = "186.6500",
                    highPrice = "186.7700",
                    lowPrice = "186.5500",
                    closePrice = "186.6700",
                    volume = "4812",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678225800000,
                    startPrice = "186.6800",
                    highPrice = "186.8100",
                    lowPrice = "186.6000",
                    closePrice = "186.6500",
                    volume = "3499",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678225500000,
                    startPrice = "186.5000",
                    highPrice = "186.7600",
                    lowPrice = "186.4800",
                    closePrice = "186.6700",
                    volume = "2823",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678225200000,
                    startPrice = "186.5100",
                    highPrice = "186.5300",
                    lowPrice = "186.3500",
                    closePrice = "186.5200",
                    volume = "6184",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678224900000,
                    startPrice = "187.0900",
                    highPrice = "187.0900",
                    lowPrice = "186.4400",
                    closePrice = "186.4700",
                    volume = "6274",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678224600000,
                    startPrice = "187.1100",
                    highPrice = "187.3800",
                    lowPrice = "186.8500",
                    closePrice = "187.1300",
                    volume = "6309",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678224300000,
                    startPrice = "187.0200",
                    highPrice = "187.1500",
                    lowPrice = "187.0000",
                    closePrice = "187.1100",
                    volume = "3348",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678224000000,
                    startPrice = "186.9900",
                    highPrice = "187.3000",
                    lowPrice = "186.9600",
                    closePrice = "187.0200",
                    volume = "6100",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678223700000,
                    startPrice = "187.3200",
                    highPrice = "187.4700",
                    lowPrice = "186.8300",
                    closePrice = "186.9900",
                    volume = "13409",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678223400000,
                    startPrice = "186.3800",
                    highPrice = "187.3000",
                    lowPrice = "186.3400",
                    closePrice = "187.3000",
                    volume = "39223",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678223100000,
                    startPrice = "186.1500",
                    highPrice = "186.4500",
                    lowPrice = "186.1300",
                    closePrice = "186.3800",
                    volume = "5088",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678222800000,
                    startPrice = "186.1200",
                    highPrice = "186.8000",
                    lowPrice = "185.8100",
                    closePrice = "186.1500",
                    volume = "16265",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678222500000,
                    startPrice = "185.6900",
                    highPrice = "186.1500",
                    lowPrice = "185.6400",
                    closePrice = "186.1300",
                    volume = "8236",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678222200000,
                    startPrice = "185.6500",
                    highPrice = "185.8000",
                    lowPrice = "185.6000",
                    closePrice = "185.7000",
                    volume = "3036",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678221900000,
                    startPrice = "185.5300",
                    highPrice = "185.7000",
                    lowPrice = "185.4000",
                    closePrice = "185.6100",
                    volume = "3528",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678221600000,
                    startPrice = "185.7200",
                    highPrice = "185.7600",
                    lowPrice = "185.2700",
                    closePrice = "185.5200",
                    volume = "4977",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678221300000,
                    startPrice = "185.3500",
                    highPrice = "185.8400",
                    lowPrice = "185.3300",
                    closePrice = "185.7400",
                    volume = "12255",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678221000000,
                    startPrice = "185.1000",
                    highPrice = "185.3500",
                    lowPrice = "185.0500",
                    closePrice = "185.3500",
                    volume = "6258",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678220700000,
                    startPrice = "185.1100",
                    highPrice = "185.2000",
                    lowPrice = "185.0000",
                    closePrice = "185.1000",
                    volume = "5817",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678220400000,
                    startPrice = "185.2000",
                    highPrice = "185.2100",
                    lowPrice = "185.0800",
                    closePrice = "185.1100",
                    volume = "7833",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678220100000,
                    startPrice = "185.2800",
                    highPrice = "185.3100",
                    lowPrice = "185.1900",
                    closePrice = "185.2200",
                    volume = "1651",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678219800000,
                    startPrice = "185.2500",
                    highPrice = "185.3000",
                    lowPrice = "185.0800",
                    closePrice = "185.2500",
                    volume = "3500",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678219500000,
                    startPrice = "185.3800",
                    highPrice = "185.3800",
                    lowPrice = "185.0000",
                    closePrice = "185.2500",
                    volume = "8771",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678219200000,
                    startPrice = "185.2000",
                    highPrice = "185.6200",
                    lowPrice = "185.1500",
                    closePrice = "185.2900",
                    volume = "6163",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678218900000,
                    startPrice = "185.6300",
                    highPrice = "185.6500",
                    lowPrice = "185.0000",
                    closePrice = "185.2000",
                    volume = "21834",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678218600000,
                    startPrice = "185.8800",
                    highPrice = "185.8800",
                    lowPrice = "185.5500",
                    closePrice = "185.6300",
                    volume = "7158",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678218300000,
                    startPrice = "186.1000",
                    highPrice = "186.1700",
                    lowPrice = "185.8500",
                    closePrice = "185.8500",
                    volume = "7363",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678218000000,
                    startPrice = "185.9500",
                    highPrice = "186.1900",
                    lowPrice = "185.8000",
                    closePrice = "186.0400",
                    volume = "9784",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678217700000,
                    startPrice = "186.1900",
                    highPrice = "186.2000",
                    lowPrice = "185.6700",
                    closePrice = "185.9500",
                    volume = "8479",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678217400000,
                    startPrice = "185.6300",
                    highPrice = "186.2100",
                    lowPrice = "185.6300",
                    closePrice = "186.1500",
                    volume = "16521",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678217100000,
                    startPrice = "186.1900",
                    highPrice = "186.2100",
                    lowPrice = "185.5400",
                    closePrice = "185.6200",
                    volume = "25377",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678216800000,
                    startPrice = "186.7500",
                    highPrice = "186.8900",
                    lowPrice = "186.1300",
                    closePrice = "186.2000",
                    volume = "12573",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678216500000,
                    startPrice = "187.5000",
                    highPrice = "187.5500",
                    lowPrice = "186.3900",
                    closePrice = "186.7000",
                    volume = "24242",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678216200000,
                    startPrice = "187.6300",
                    highPrice = "187.9000",
                    lowPrice = "187.2700",
                    closePrice = "187.5900",
                    volume = "16418",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678215900000,
                    startPrice = "187.1500",
                    highPrice = "188.0900",
                    lowPrice = "187.0700",
                    closePrice = "187.7100",
                    volume = "24610",
                    amount = "0",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678171500000,
                    startPrice = "189.3800",
                    highPrice = "189.6300",
                    lowPrice = "189.1700",
                    closePrice = "189.2450",
                    volume = "448665",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678171200000,
                    startPrice = "189.0100",
                    highPrice = "189.5873",
                    lowPrice = "188.8500",
                    closePrice = "189.3800",
                    volume = "512577",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678170900000,
                    startPrice = "188.6475",
                    highPrice = "189.2500",
                    lowPrice = "188.3000",
                    closePrice = "189.0100",
                    volume = "638169",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678170600000,
                    startPrice = "189.3300",
                    highPrice = "189.3500",
                    lowPrice = "188.5500",
                    closePrice = "188.6475",
                    volume = "530416",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678170300000,
                    startPrice = "189.9701",
                    highPrice = "189.9799",
                    lowPrice = "189.0900",
                    closePrice = "189.3300",
                    volume = "513852",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678170000000,
                    startPrice = "189.5700",
                    highPrice = "190.1900",
                    lowPrice = "189.5700",
                    closePrice = "189.9701",
                    volume = "461458",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678169700000,
                    startPrice = "189.5173",
                    highPrice = "189.8600",
                    lowPrice = "189.4000",
                    closePrice = "189.5700",
                    volume = "478927",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678169400000,
                    startPrice = "189.2500",
                    highPrice = "189.6899",
                    lowPrice = "189.2259",
                    closePrice = "189.5173",
                    volume = "394336",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678169100000,
                    startPrice = "189.0000",
                    highPrice = "189.6800",
                    lowPrice = "188.9400",
                    closePrice = "189.2500",
                    volume = "601319",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678168800000,
                    startPrice = "188.9599",
                    highPrice = "189.0700",
                    lowPrice = "188.4200",
                    closePrice = "189.0000",
                    volume = "565377",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678168500000,
                    startPrice = "189.3900",
                    highPrice = "189.4339",
                    lowPrice = "188.8700",
                    closePrice = "188.9599",
                    volume = "529568",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678168200000,
                    startPrice = "189.2500",
                    highPrice = "189.9800",
                    lowPrice = "189.2400",
                    closePrice = "189.3900",
                    volume = "517827",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678167900000,
                    startPrice = "189.6572",
                    highPrice = "189.7900",
                    lowPrice = "189.1522",
                    closePrice = "189.2500",
                    volume = "385347",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678167600000,
                    startPrice = "189.6350",
                    highPrice = "189.8200",
                    lowPrice = "189.5000",
                    closePrice = "189.6400",
                    volume = "331693",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678167300000,
                    startPrice = "189.6300",
                    highPrice = "189.9800",
                    lowPrice = "189.2800",
                    closePrice = "189.6296",
                    volume = "531066",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678167000000,
                    startPrice = "190.1400",
                    highPrice = "190.1550",
                    lowPrice = "189.5250",
                    closePrice = "189.6300",
                    volume = "713304",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678166700000,
                    startPrice = "191.4900",
                    highPrice = "191.5600",
                    lowPrice = "190.0700",
                    closePrice = "190.1400",
                    volume = "614614",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678166400000,
                    startPrice = "191.1800",
                    highPrice = "191.5400",
                    lowPrice = "191.0101",
                    closePrice = "191.5000",
                    volume = "344316",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678166100000,
                    startPrice = "190.7862",
                    highPrice = "191.4400",
                    lowPrice = "190.6750",
                    closePrice = "191.1700",
                    volume = "362859",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678165800000,
                    startPrice = "190.7800",
                    highPrice = "191.0700",
                    lowPrice = "190.7326",
                    closePrice = "190.8000",
                    volume = "259185",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678165500000,
                    startPrice = "190.8900",
                    highPrice = "191.2400",
                    lowPrice = "190.6700",
                    closePrice = "190.7800",
                    volume = "485587",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678165200000,
                    startPrice = "190.4950",
                    highPrice = "191.1199",
                    lowPrice = "190.4700",
                    closePrice = "190.8900",
                    volume = "506374",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678164900000,
                    startPrice = "190.5700",
                    highPrice = "190.8400",
                    lowPrice = "190.4600",
                    closePrice = "190.4950",
                    volume = "453060",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678164600000,
                    startPrice = "190.8700",
                    highPrice = "191.1000",
                    lowPrice = "190.4100",
                    closePrice = "190.5450",
                    volume = "494534",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678164300000,
                    startPrice = "191.4700",
                    highPrice = "191.5600",
                    lowPrice = "190.8000",
                    closePrice = "190.8700",
                    volume = "536760",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678164000000,
                    startPrice = "192.3150",
                    highPrice = "192.3150",
                    lowPrice = "191.4500",
                    closePrice = "191.4850",
                    volume = "602567",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678163700000,
                    startPrice = "192.3000",
                    highPrice = "192.8900",
                    lowPrice = "192.2500",
                    closePrice = "192.3150",
                    volume = "393629",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678163400000,
                    startPrice = "192.1116",
                    highPrice = "192.6800",
                    lowPrice = "191.9300",
                    closePrice = "192.3100",
                    volume = "452740",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678163100000,
                    startPrice = "192.4802",
                    highPrice = "192.5165",
                    lowPrice = "191.9700",
                    closePrice = "192.1116",
                    volume = "426954",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678162800000,
                    startPrice = "192.4200",
                    highPrice = "192.6000",
                    lowPrice = "192.0800",
                    closePrice = "192.4802",
                    volume = "411816",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678162500000,
                    startPrice = "192.8101",
                    highPrice = "192.9800",
                    lowPrice = "192.2800",
                    closePrice = "192.4200",
                    volume = "425204",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1678162200000,
                    startPrice = "192.9600",
                    highPrice = "192.9800",
                    lowPrice = "192.5700",
                    closePrice = "192.8101",
                    volume = "616544",
                    amount = "0",
                    sessionId = "0",
                    createdTime = null
                )
            )
            chartDataList = list
        } else if (request.chartDivision == "1MINUTE") {
            val list = mutableListOf(
                KfitBaseChart(
                    dateTime = 1680075720000,
                    startPrice = "0.1036",
                    highPrice = "0.1039",
                    lowPrice = "0.1036",
                    closePrice = "0.1039",
                    volume = "6480.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680075660000,
                    startPrice = "0.1038",
                    highPrice = "0.1039",
                    lowPrice = "0.1036",
                    closePrice = "0.1039",
                    volume = "6878.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680075600000,
                    startPrice = "0.1037",
                    highPrice = "0.1038",
                    lowPrice = "0.1036",
                    closePrice = "0.1038",
                    volume = "13512.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680075540000,
                    startPrice = "0.1037",
                    highPrice = "0.1038",
                    lowPrice = "0.1036",
                    closePrice = "0.1037",
                    volume = "7130.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680075480000,
                    startPrice = "0.1039",
                    highPrice = "0.1039",
                    lowPrice = "0.1038",
                    closePrice = "0.1038",
                    volume = "1172.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680075420000,
                    startPrice = "0.1039",
                    highPrice = "0.1039",
                    lowPrice = "0.1039",
                    closePrice = "0.1039",
                    volume = "3962.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680075360000,
                    startPrice = "0.1036",
                    highPrice = "0.1039",
                    lowPrice = "0.1033",
                    closePrice = "0.1039",
                    volume = "422.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680075300000,
                    startPrice = "0.1036",
                    highPrice = "0.1039",
                    lowPrice = "0.1035",
                    closePrice = "0.1036",
                    volume = "65592.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680075240000,
                    startPrice = "0.1036",
                    highPrice = "0.1036",
                    lowPrice = "0.1032",
                    closePrice = "0.1036",
                    volume = "11512.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680075180000,
                    startPrice = "0.1036",
                    highPrice = "0.1036",
                    lowPrice = "0.1033",
                    closePrice = "0.1036",
                    volume = "3090.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680075120000,
                    startPrice = "0.1036",
                    highPrice = "0.1036",
                    lowPrice = "0.1035",
                    closePrice = "0.1036",
                    volume = "4746.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680075060000,
                    startPrice = "0.1034",
                    highPrice = "0.1036",
                    lowPrice = "0.1034",
                    closePrice = "0.1036",
                    volume = "15222.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680075000000,
                    startPrice = "0.1035",
                    highPrice = "0.1035",
                    lowPrice = "0.1032",
                    closePrice = "0.1035",
                    volume = "6224.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680074940000,
                    startPrice = "0.1031",
                    highPrice = "0.1036",
                    lowPrice = "0.1030",
                    closePrice = "0.1035",
                    volume = "64532.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680074880000,
                    startPrice = "0.1036",
                    highPrice = "0.1036",
                    lowPrice = "0.1031",
                    closePrice = "0.1031",
                    volume = "151144.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680074820000,
                    startPrice = "0.1031",
                    highPrice = "0.1036",
                    lowPrice = "0.1031",
                    closePrice = "0.1036",
                    volume = "13892.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680074760000,
                    startPrice = "0.1035",
                    highPrice = "0.1036",
                    lowPrice = "0.1030",
                    closePrice = "0.1031",
                    volume = "8666.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680074700000,
                    startPrice = "0.1036",
                    highPrice = "0.1036",
                    lowPrice = "0.1031",
                    closePrice = "0.1036",
                    volume = "26278.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680074640000,
                    startPrice = "0.1036",
                    highPrice = "0.1036",
                    lowPrice = "0.1033",
                    closePrice = "0.1036",
                    volume = "5616.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680074580000,
                    startPrice = "0.1036",
                    highPrice = "0.1036",
                    lowPrice = "0.1032",
                    closePrice = "0.1036",
                    volume = "22784.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680074520000,
                    startPrice = "0.1039",
                    highPrice = "0.1039",
                    lowPrice = "0.1033",
                    closePrice = "0.1036",
                    volume = "30770.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680074460000,
                    startPrice = "0.1034",
                    highPrice = "0.1039",
                    lowPrice = "0.1034",
                    closePrice = "0.1034",
                    volume = "2208.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680074400000,
                    startPrice = "0.1036",
                    highPrice = "0.1036",
                    lowPrice = "0.1035",
                    closePrice = "0.1036",
                    volume = "54410.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680074340000,
                    startPrice = "0.1035",
                    highPrice = "0.1036",
                    lowPrice = "0.1035",
                    closePrice = "0.1036",
                    volume = "21200.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680074280000,
                    startPrice = "0.1036",
                    highPrice = "0.1036",
                    lowPrice = "0.1034",
                    closePrice = "0.1034",
                    volume = "9966.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680074220000,
                    startPrice = "0.1035",
                    highPrice = "0.1035",
                    lowPrice = "0.1033",
                    closePrice = "0.1034",
                    volume = "6504.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680074160000,
                    startPrice = "0.1035",
                    highPrice = "0.1035",
                    lowPrice = "0.1034",
                    closePrice = "0.1034",
                    volume = "11430.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680074100000,
                    startPrice = "0.1035",
                    highPrice = "0.1036",
                    lowPrice = "0.1035",
                    closePrice = "0.1036",
                    volume = "20932.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680074040000,
                    startPrice = "0.1032",
                    highPrice = "0.1036",
                    lowPrice = "0.1032",
                    closePrice = "0.1035",
                    volume = "18458.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680073980000,
                    startPrice = "0.1037",
                    highPrice = "0.1037",
                    lowPrice = "0.1032",
                    closePrice = "0.1032",
                    volume = "76464.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680073920000,
                    startPrice = "0.1030",
                    highPrice = "0.1038",
                    lowPrice = "0.1030",
                    closePrice = "0.1037",
                    volume = "11400.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680073860000,
                    startPrice = "0.1036",
                    highPrice = "0.1040",
                    lowPrice = "0.1029",
                    closePrice = "0.1038",
                    volume = "258108.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680073800000,
                    startPrice = "0.1037",
                    highPrice = "0.1040",
                    lowPrice = "0.1037",
                    closePrice = "0.1040",
                    volume = "15208.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680073740000,
                    startPrice = "0.1040",
                    highPrice = "0.1040",
                    lowPrice = "0.1037",
                    closePrice = "0.1040",
                    volume = "194968.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680073680000,
                    startPrice = "0.1040",
                    highPrice = "0.1040",
                    lowPrice = "0.1037",
                    closePrice = "0.1040",
                    volume = "762.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680073620000,
                    startPrice = "0.1039",
                    highPrice = "0.1040",
                    lowPrice = "0.1037",
                    closePrice = "0.1039",
                    volume = "17670.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680073560000,
                    startPrice = "0.1040",
                    highPrice = "0.1040",
                    lowPrice = "0.1037",
                    closePrice = "0.1040",
                    volume = "7940.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680073500000,
                    startPrice = "0.1039",
                    highPrice = "0.1040",
                    lowPrice = "0.1039",
                    closePrice = "0.1040",
                    volume = "44998.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680073440000,
                    startPrice = "0.1039",
                    highPrice = "0.1040",
                    lowPrice = "0.1036",
                    closePrice = "0.1039",
                    volume = "17612.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680073380000,
                    startPrice = "0.1039",
                    highPrice = "0.1039",
                    lowPrice = "0.1036",
                    closePrice = "0.1039",
                    volume = "7166.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680073320000,
                    startPrice = "0.1039",
                    highPrice = "0.1039",
                    lowPrice = "0.1039",
                    closePrice = "0.1039",
                    volume = "8960.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680073260000,
                    startPrice = "0.1042",
                    highPrice = "0.1042",
                    lowPrice = "0.1034",
                    closePrice = "0.1036",
                    volume = "119076.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null

                ),
                KfitBaseChart(
                    dateTime = 1680073200000,
                    startPrice = "0.1035",
                    highPrice = "0.1040",
                    lowPrice = "0.1033",
                    closePrice = "0.1040",
                    volume = "2468536.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680073140000,
                    startPrice = "0.1038",
                    highPrice = "0.1039",
                    lowPrice = "0.1035",
                    closePrice = "0.1036",
                    volume = "157448.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680073080000,
                    startPrice = "0.1034",
                    highPrice = "0.1040",
                    lowPrice = "0.1031",
                    closePrice = "0.1038",
                    volume = "232616.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680073020000,
                    startPrice = "0.1032",
                    highPrice = "0.1035",
                    lowPrice = "0.1031",
                    closePrice = "0.1035",
                    volume = "259170.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680072960000,
                    startPrice = "0.1036",
                    highPrice = "0.1037",
                    lowPrice = "0.1033",
                    closePrice = "0.1033",
                    volume = "423662.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680072900000,
                    startPrice = "0.1038",
                    highPrice = "0.1045",
                    lowPrice = "0.1033",
                    closePrice = "0.1037",
                    volume = "939806.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680072840000,
                    startPrice = "0.1041",
                    highPrice = "0.1041",
                    lowPrice = "0.1038",
                    closePrice = "0.1038",
                    volume = "987158.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680072780000,
                    startPrice = "0.1042",
                    highPrice = "0.1043",
                    lowPrice = "0.1040",
                    closePrice = "0.1041",
                    volume = "335466.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680072720000,
                    startPrice = "0.1044",
                    highPrice = "0.1044",
                    lowPrice = "0.1042",
                    closePrice = "0.1042",
                    volume = "454532.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680072660000,
                    startPrice = "0.1043",
                    highPrice = "0.1044",
                    lowPrice = "0.1043",
                    closePrice = "0.1044",
                    volume = "569510.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680072600000,
                    startPrice = "0.1044",
                    highPrice = "0.1044",
                    lowPrice = "0.1043",
                    closePrice = "0.1043",
                    volume = "616996.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680072540000,
                    startPrice = "0.1045",
                    highPrice = "0.1045",
                    lowPrice = "0.1043",
                    closePrice = "0.1044",
                    volume = "295774.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680072480000,
                    startPrice = "0.1045",
                    highPrice = "0.1046",
                    lowPrice = "0.1045",
                    closePrice = "0.1045",
                    volume = "813494.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680072420000,
                    startPrice = "0.1046",
                    highPrice = "0.1046",
                    lowPrice = "0.1045",
                    closePrice = "0.1046",
                    volume = "419950.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680072360000,
                    startPrice = "0.1051",
                    highPrice = "0.1052",
                    lowPrice = "0.1045",
                    closePrice = "0.1046",
                    volume = "646342.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680072300000,
                    startPrice = "0.1042",
                    highPrice = "0.1052",
                    lowPrice = "0.1041",
                    closePrice = "0.1051",
                    volume = "789702.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680072240000,
                    startPrice = "0.1048",
                    highPrice = "0.1048",
                    lowPrice = "0.1040",
                    closePrice = "0.1042",
                    volume = "1041988.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680072180000,
                    startPrice = "0.1044",
                    highPrice = "0.1048",
                    lowPrice = "0.1043",
                    closePrice = "0.1048",
                    volume = "200830.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680072120000,
                    startPrice = "0.1045",
                    highPrice = "0.1045",
                    lowPrice = "0.1041",
                    closePrice = "0.1043",
                    volume = "483808.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680072060000,
                    startPrice = "0.1047",
                    highPrice = "0.1047",
                    lowPrice = "0.1044",
                    closePrice = "0.1045",
                    volume = "92680.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680072000000,
                    startPrice = "0.1044",
                    highPrice = "0.1052",
                    lowPrice = "0.1044",
                    closePrice = "0.1047",
                    volume = "189142.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680071940000,
                    startPrice = "0.1044",
                    highPrice = "0.1045",
                    lowPrice = "0.1043",
                    closePrice = "0.1044",
                    volume = "377470.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680071880000,
                    startPrice = "0.1041",
                    highPrice = "0.1054",
                    lowPrice = "0.1040",
                    closePrice = "0.1044",
                    volume = "942376.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680071820000,
                    startPrice = "0.1042",
                    highPrice = "0.1045",
                    lowPrice = "0.1041",
                    closePrice = "0.1041",
                    volume = "526172.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680071760000,
                    startPrice = "0.1042",
                    highPrice = "0.1043",
                    lowPrice = "0.1041",
                    closePrice = "0.1043",
                    volume = "379266.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680071700000,
                    startPrice = "0.1042",
                    highPrice = "0.1042",
                    lowPrice = "0.1040",
                    closePrice = "0.1042",
                    volume = "667206.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680071640000,
                    startPrice = "0.1042",
                    highPrice = "0.1042",
                    lowPrice = "0.1041",
                    closePrice = "0.1042",
                    volume = "635964.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680071580000,
                    startPrice = "0.1042",
                    highPrice = "0.1043",
                    lowPrice = "0.1041",
                    closePrice = "0.1041",
                    volume = "345636.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680071520000,
                    startPrice = "0.1047",
                    highPrice = "0.1047",
                    lowPrice = "0.1041",
                    closePrice = "0.1042",
                    volume = "414790.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680071460000,
                    startPrice = "0.1053",
                    highPrice = "0.1053",
                    lowPrice = "0.1040",
                    closePrice = "0.1044",
                    volume = "789208.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680071400000,
                    startPrice = "0.1053",
                    highPrice = "0.1053",
                    lowPrice = "0.1052",
                    closePrice = "0.1053",
                    volume = "562678.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680071340000,
                    startPrice = "0.1052",
                    highPrice = "0.1053",
                    lowPrice = "0.1050",
                    closePrice = "0.1053",
                    volume = "782248.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680071280000,
                    startPrice = "0.1049",
                    highPrice = "0.1054",
                    lowPrice = "0.1049",
                    closePrice = "0.1052",
                    volume = "615502.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680071220000,
                    startPrice = "0.1049",
                    highPrice = "0.1050",
                    lowPrice = "0.1048",
                    closePrice = "0.1050",
                    volume = "360050.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680071160000,
                    startPrice = "0.1048",
                    highPrice = "0.1050",
                    lowPrice = "0.1045",
                    closePrice = "0.1049",
                    volume = "144204.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680071100000,
                    startPrice = "0.1048",
                    highPrice = "0.1055",
                    lowPrice = "0.1042",
                    closePrice = "0.1048",
                    volume = "810028.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680071040000,
                    startPrice = "0.1048",
                    highPrice = "0.1050",
                    lowPrice = "0.1047",
                    closePrice = "0.1048",
                    volume = "253928.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680070980000,
                    startPrice = "0.1050",
                    highPrice = "0.1053",
                    lowPrice = "0.1049",
                    closePrice = "0.1049",
                    volume = "470464.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680070920000,
                    startPrice = "0.1053",
                    highPrice = "0.1053",
                    lowPrice = "0.1050",
                    closePrice = "0.1051",
                    volume = "331138.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680070860000,
                    startPrice = "0.1056",
                    highPrice = "0.1057",
                    lowPrice = "0.1053",
                    closePrice = "0.1053",
                    volume = "607080.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680070800000,
                    startPrice = "0.1050",
                    highPrice = "0.1058",
                    lowPrice = "0.1049",
                    closePrice = "0.1056",
                    volume = "1154058.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680070740000,
                    startPrice = "0.1049",
                    highPrice = "0.1050",
                    lowPrice = "0.1048",
                    closePrice = "0.1050",
                    volume = "143794.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680070680000,
                    startPrice = "0.1047",
                    highPrice = "0.1049",
                    lowPrice = "0.1046",
                    closePrice = "0.1049",
                    volume = "363090.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680070620000,
                    startPrice = "0.1042",
                    highPrice = "0.1048",
                    lowPrice = "0.1041",
                    closePrice = "0.1047",
                    volume = "354266.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680070560000,
                    startPrice = "0.1045",
                    highPrice = "0.1045",
                    lowPrice = "0.1040",
                    closePrice = "0.1042",
                    volume = "450258.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680070500000,
                    startPrice = "0.1044",
                    highPrice = "0.1045",
                    lowPrice = "0.1044",
                    closePrice = "0.1044",
                    volume = "279182.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680070440000,
                    startPrice = "0.1045",
                    highPrice = "0.1045",
                    lowPrice = "0.1044",
                    closePrice = "0.1044",
                    volume = "52132.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680070380000,
                    startPrice = "0.1040",
                    highPrice = "0.1049",
                    lowPrice = "0.1040",
                    closePrice = "0.1045",
                    volume = "717034.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680070320000,
                    startPrice = "0.1040",
                    highPrice = "0.1041",
                    lowPrice = "0.1040",
                    closePrice = "0.1040",
                    volume = "296714.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680070260000,
                    startPrice = "0.1041",
                    highPrice = "0.1041",
                    lowPrice = "0.1040",
                    closePrice = "0.1040",
                    volume = "213294.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680070200000,
                    startPrice = "0.1042",
                    highPrice = "0.1042",
                    lowPrice = "0.1040",
                    closePrice = "0.1041",
                    volume = "436288.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680070140000,
                    startPrice = "0.1043",
                    highPrice = "0.1043",
                    lowPrice = "0.1041",
                    closePrice = "0.1042",
                    volume = "166878.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680070080000,
                    startPrice = "0.1044",
                    highPrice = "0.1044",
                    lowPrice = "0.1041",
                    closePrice = "0.1042",
                    volume = "653584.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680070020000,
                    startPrice = "0.1045",
                    highPrice = "0.1045",
                    lowPrice = "0.1044",
                    closePrice = "0.1044",
                    volume = "273548.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680069960000,
                    startPrice = "0.1044",
                    highPrice = "0.1045",
                    lowPrice = "0.1043",
                    closePrice = "0.1045",
                    volume = "395252.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680069900000,
                    startPrice = "0.1044",
                    highPrice = "0.1045",
                    lowPrice = "0.1043",
                    closePrice = "0.1044",
                    volume = "582366.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680069840000,
                    startPrice = "0.1047",
                    highPrice = "0.1048",
                    lowPrice = "0.1043",
                    closePrice = "0.1044",
                    volume = "442146.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680069780000,
                    startPrice = "0.1047",
                    highPrice = "0.1048",
                    lowPrice = "0.1047",
                    closePrice = "0.1047",
                    volume = "305994.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680069720000,
                    startPrice = "0.1048",
                    highPrice = "0.1048",
                    lowPrice = "0.1047",
                    closePrice = "0.1047",
                    volume = "666108.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680069660000,
                    startPrice = "0.1048",
                    highPrice = "0.1050",
                    lowPrice = "0.1047",
                    closePrice = "0.1047",
                    volume = "290524.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680069600000,
                    startPrice = "0.1046",
                    highPrice = "0.1048",
                    lowPrice = "0.1046",
                    closePrice = "0.1048",
                    volume = "458078.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680069540000,
                    startPrice = "0.1047",
                    highPrice = "0.1047",
                    lowPrice = "0.1046",
                    closePrice = "0.1046",
                    volume = "164976.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680069480000,
                    startPrice = "0.1050",
                    highPrice = "0.1051",
                    lowPrice = "0.1043",
                    closePrice = "0.1046",
                    volume = "404372.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680069420000,
                    startPrice = "0.1051",
                    highPrice = "0.1052",
                    lowPrice = "0.1050",
                    closePrice = "0.1051",
                    volume = "230904.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680069360000,
                    startPrice = "0.1053",
                    highPrice = "0.1054",
                    lowPrice = "0.1051",
                    closePrice = "0.1051",
                    volume = "574134.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680069300000,
                    startPrice = "0.1054",
                    highPrice = "0.1054",
                    lowPrice = "0.1051",
                    closePrice = "0.1053",
                    volume = "393916.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680069240000,
                    startPrice = "0.1049",
                    highPrice = "0.1053",
                    lowPrice = "0.1049",
                    closePrice = "0.1052",
                    volume = "443636.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680069180000,
                    startPrice = "0.1044",
                    highPrice = "0.1054",
                    lowPrice = "0.1044",
                    closePrice = "0.1049",
                    volume = "442074.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680069120000,
                    startPrice = "0.1050",
                    highPrice = "0.1051",
                    lowPrice = "0.1041",
                    closePrice = "0.1044",
                    volume = "521296.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680069060000,
                    startPrice = "0.1055",
                    highPrice = "0.1055",
                    lowPrice = "0.1050",
                    closePrice = "0.1050",
                    volume = "1071932.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680069000000,
                    startPrice = "0.1054",
                    highPrice = "0.1055",
                    lowPrice = "0.1053",
                    closePrice = "0.1055",
                    volume = "144152.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680068940000,
                    startPrice = "0.1052",
                    highPrice = "0.1055",
                    lowPrice = "0.1052",
                    closePrice = "0.1054",
                    volume = "601254.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680068880000,
                    startPrice = "0.1051",
                    highPrice = "0.1052",
                    lowPrice = "0.1050",
                    closePrice = "0.1052",
                    volume = "430744.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680068820000,
                    startPrice = "0.1051",
                    highPrice = "0.1051",
                    lowPrice = "0.1050",
                    closePrice = "0.1050",
                    volume = "1165436.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680068760000,
                    startPrice = "0.1051",
                    highPrice = "0.1051",
                    lowPrice = "0.1050",
                    closePrice = "0.1050",
                    volume = "335934.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680068700000,
                    startPrice = "0.1051",
                    highPrice = "0.1052",
                    lowPrice = "0.1050",
                    closePrice = "0.1051",
                    volume = "534704.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680068640000,
                    startPrice = "0.1053",
                    highPrice = "0.1053",
                    lowPrice = "0.1050",
                    closePrice = "0.1051",
                    volume = "283650.000000",
                    amount = "",
                    sessionId = "0",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680068580000,
                    startPrice = "0.1053",
                    highPrice = "0.1055",
                    lowPrice = "0.1052",
                    closePrice = "0.1053",
                    volume = "453896.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680068520000,
                    startPrice = "0.1049",
                    highPrice = "0.1053",
                    lowPrice = "0.1048",
                    closePrice = "0.1053",
                    volume = "269084.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680068460000,
                    startPrice = "0.1047",
                    highPrice = "0.1051",
                    lowPrice = "0.1047",
                    closePrice = "0.1049",
                    volume = "330378.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680068400000,
                    startPrice = "0.1053",
                    highPrice = "0.1053",
                    lowPrice = "0.1047",
                    closePrice = "0.1047",
                    volume = "1129548.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680068340000,
                    startPrice = "0.1050",
                    highPrice = "0.1053",
                    lowPrice = "0.1050",
                    closePrice = "0.1053",
                    volume = "259720.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680068280000,
                    startPrice = "0.1048",
                    highPrice = "0.1050",
                    lowPrice = "0.1048",
                    closePrice = "0.1050",
                    volume = "600326.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680068220000,
                    startPrice = "0.1049",
                    highPrice = "0.1049",
                    lowPrice = "0.1048",
                    closePrice = "0.1049",
                    volume = "828502.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680068160000,
                    startPrice = "0.1048",
                    highPrice = "0.1049",
                    lowPrice = "0.1047",
                    closePrice = "0.1049",
                    volume = "415784.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680068100000,
                    startPrice = "0.1048",
                    highPrice = "0.1048",
                    lowPrice = "0.1047",
                    closePrice = "0.1048",
                    volume = "513650.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680068040000,
                    startPrice = "0.1048",
                    highPrice = "0.1048",
                    lowPrice = "0.1047",
                    closePrice = "0.1048",
                    volume = "236794.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680067980000,
                    startPrice = "0.1048",
                    highPrice = "0.1048",
                    lowPrice = "0.1047",
                    closePrice = "0.1048",
                    volume = "139890.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680067920000,
                    startPrice = "0.1046",
                    highPrice = "0.1048",
                    lowPrice = "0.1046",
                    closePrice = "0.1048",
                    volume = "284012.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680067860000,
                    startPrice = "0.1041",
                    highPrice = "0.1049",
                    lowPrice = "0.1040",
                    closePrice = "0.1046",
                    volume = "424022.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680067800000,
                    startPrice = "0.1040",
                    highPrice = "0.1041",
                    lowPrice = "0.1040",
                    closePrice = "0.1041",
                    volume = "284688.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680067740000,
                    startPrice = "0.1041",
                    highPrice = "0.1041",
                    lowPrice = "0.1040",
                    closePrice = "0.1041",
                    volume = "498014.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680067680000,
                    startPrice = "0.1041",
                    highPrice = "0.1041",
                    lowPrice = "0.1040",
                    closePrice = "0.1040",
                    volume = "140600.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680067620000,
                    startPrice = "0.1040",
                    highPrice = "0.1041",
                    lowPrice = "0.1040",
                    closePrice = "0.1040",
                    volume = "147392.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680067560000,
                    startPrice = "0.1040",
                    highPrice = "0.1041",
                    lowPrice = "0.1040",
                    closePrice = "0.1040",
                    volume = "192210.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680067500000,
                    startPrice = "0.1041",
                    highPrice = "0.1041",
                    lowPrice = "0.1040",
                    closePrice = "0.1041",
                    volume = "394062.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680067440000,
                    startPrice = "0.1041",
                    highPrice = "0.1041",
                    lowPrice = "0.1040",
                    closePrice = "0.1040",
                    volume = "249398.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680067380000,
                    startPrice = "0.1040",
                    highPrice = "0.1041",
                    lowPrice = "0.1040",
                    closePrice = "0.1041",
                    volume = "371974.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680067320000,
                    startPrice = "0.1041",
                    highPrice = "0.1041",
                    lowPrice = "0.1040",
                    closePrice = "0.1040",
                    volume = "252322.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680067260000,
                    startPrice = "0.1041",
                    highPrice = "0.1042",
                    lowPrice = "0.1040",
                    closePrice = "0.1041",
                    volume = "301322.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680067200000,
                    startPrice = "0.1041",
                    highPrice = "0.1041",
                    lowPrice = "0.1040",
                    closePrice = "0.1041",
                    volume = "167778.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680067140000,
                    startPrice = "0.1042",
                    highPrice = "0.1042",
                    lowPrice = "0.1040",
                    closePrice = "0.1041",
                    volume = "531842.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680067080000,
                    startPrice = "0.1042",
                    highPrice = "0.1042",
                    lowPrice = "0.1041",
                    closePrice = "0.1042",
                    volume = "216692.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680067020000,
                    startPrice = "0.1045",
                    highPrice = "0.1045",
                    lowPrice = "0.1040",
                    closePrice = "0.1042",
                    volume = "335962.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680066960000,
                    startPrice = "0.1041",
                    highPrice = "0.1045",
                    lowPrice = "0.1040",
                    closePrice = "0.1045",
                    volume = "199812.000000",
                    amount = "",
                    sessionId = "1",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680066900000,
                    startPrice = "0.1045",
                    highPrice = "0.1046",
                    lowPrice = "0.1040",
                    closePrice = "0.1041",
                    volume = "275236.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680066840000,
                    startPrice = "0.1044",
                    highPrice = "0.1048",
                    lowPrice = "0.1044",
                    closePrice = "0.1045",
                    volume = "185328.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680066780000,
                    startPrice = "0.1043",
                    highPrice = "0.1045",
                    lowPrice = "0.1042",
                    closePrice = "0.1045",
                    volume = "158622.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680066720000,
                    startPrice = "0.1045",
                    highPrice = "0.1045",
                    lowPrice = "0.1040",
                    closePrice = "0.1043",
                    volume = "911336.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680066660000,
                    startPrice = "0.1044",
                    highPrice = "0.1045",
                    lowPrice = "0.1044",
                    closePrice = "0.1044",
                    volume = "234740.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680066600000,
                    startPrice = "0.1045",
                    highPrice = "0.1045",
                    lowPrice = "0.1044",
                    closePrice = "0.1044",
                    volume = "44444.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680066540000,
                    startPrice = "0.1045",
                    highPrice = "0.1045",
                    lowPrice = "0.1044",
                    closePrice = "0.1045",
                    volume = "469582.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680066480000,
                    startPrice = "0.1049",
                    highPrice = "0.1049",
                    lowPrice = "0.1044",
                    closePrice = "0.1044",
                    volume = "469364.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680066420000,
                    startPrice = "0.1049",
                    highPrice = "0.1049",
                    lowPrice = "0.1048",
                    closePrice = "0.1049",
                    volume = "123648.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680066360000,
                    startPrice = "0.1050",
                    highPrice = "0.1050",
                    lowPrice = "0.1048",
                    closePrice = "0.1049",
                    volume = "345936.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680066300000,
                    startPrice = "0.1048",
                    highPrice = "0.1049",
                    lowPrice = "0.1047",
                    closePrice = "0.1049",
                    volume = "315226.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680066240000,
                    startPrice = "0.1048",
                    highPrice = "0.1048",
                    lowPrice = "0.1047",
                    closePrice = "0.1048",
                    volume = "97546.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680066180000,
                    startPrice = "0.1046",
                    highPrice = "0.1048",
                    lowPrice = "0.1045",
                    closePrice = "0.1048",
                    volume = "354426.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680066120000,
                    startPrice = "0.1051",
                    highPrice = "0.1052",
                    lowPrice = "0.1045",
                    closePrice = "0.1046",
                    volume = "507014.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680066060000,
                    startPrice = "0.1051",
                    highPrice = "0.1053",
                    lowPrice = "0.1051",
                    closePrice = "0.1052",
                    volume = "209392.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680066000000,
                    startPrice = "0.1053",
                    highPrice = "0.1054",
                    lowPrice = "0.1050",
                    closePrice = "0.1052",
                    volume = "732844.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680065940000,
                    startPrice = "0.1051",
                    highPrice = "0.1054",
                    lowPrice = "0.1050",
                    closePrice = "0.1053",
                    volume = "848384.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680065880000,
                    startPrice = "0.1050",
                    highPrice = "0.1051",
                    lowPrice = "0.1050",
                    closePrice = "0.1051",
                    volume = "155104.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680065820000,
                    startPrice = "0.1053",
                    highPrice = "0.1053",
                    lowPrice = "0.1050",
                    closePrice = "0.1050",
                    volume = "169354.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680065760000,
                    startPrice = "0.1045",
                    highPrice = "0.1053",
                    lowPrice = "0.1045",
                    closePrice = "0.1053",
                    volume = "485646.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680065700000,
                    startPrice = "0.1048",
                    highPrice = "0.1048",
                    lowPrice = "0.1044",
                    closePrice = "0.1044",
                    volume = "364450.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680065640000,
                    startPrice = "0.1053",
                    highPrice = "0.1053",
                    lowPrice = "0.1047",
                    closePrice = "0.1048",
                    volume = "840772.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680065580000,
                    startPrice = "0.1051",
                    highPrice = "0.1053",
                    lowPrice = "0.1051",
                    closePrice = "0.1053",
                    volume = "301846.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680065520000,
                    startPrice = "0.1047",
                    highPrice = "0.1052",
                    lowPrice = "0.1046",
                    closePrice = "0.1050",
                    volume = "504942.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680065460000,
                    startPrice = "0.1047",
                    highPrice = "0.1047",
                    lowPrice = "0.1046",
                    closePrice = "0.1047",
                    volume = "136756.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680065400000,
                    startPrice = "0.1047",
                    highPrice = "0.1047",
                    lowPrice = "0.1046",
                    closePrice = "0.1047",
                    volume = "199002.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680065340000,
                    startPrice = "0.1047",
                    highPrice = "0.1047",
                    lowPrice = "0.1046",
                    closePrice = "0.1047",
                    volume = "540434.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680065280000,
                    startPrice = "0.1043",
                    highPrice = "0.1047",
                    lowPrice = "0.1043",
                    closePrice = "0.1047",
                    volume = "134898.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680065220000,
                    startPrice = "0.1047",
                    highPrice = "0.1047",
                    lowPrice = "0.1040",
                    closePrice = "0.1043",
                    volume = "625274.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680065160000,
                    startPrice = "0.1054",
                    highPrice = "0.1055",
                    lowPrice = "0.1046",
                    closePrice = "0.1047",
                    volume = "832770.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680065100000,
                    startPrice = "0.1057",
                    highPrice = "0.1057",
                    lowPrice = "0.1048",
                    closePrice = "0.1054",
                    volume = "981526.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680065040000,
                    startPrice = "0.1057",
                    highPrice = "0.1057",
                    lowPrice = "0.1056",
                    closePrice = "0.1056",
                    volume = "99812.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680064980000,
                    startPrice = "0.1054",
                    highPrice = "0.1058",
                    lowPrice = "0.1054",
                    closePrice = "0.1057",
                    volume = "437242.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680064920000,
                    startPrice = "0.1049",
                    highPrice = "0.1056",
                    lowPrice = "0.1048",
                    closePrice = "0.1053",
                    volume = "1593564.000000",
                    amount = "",
                    sessionId = "4",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680064860000,
                    startPrice = "0.1056",
                    highPrice = "0.1059",
                    lowPrice = "0.1048",
                    closePrice = "0.1050",
                    volume = "891746.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680064800000,
                    startPrice = "0.1055",
                    highPrice = "0.1060",
                    lowPrice = "0.1055",
                    closePrice = "0.1056",
                    volume = "2458398.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680064740000,
                    startPrice = "0.1055",
                    highPrice = "0.1055",
                    lowPrice = "0.1054",
                    closePrice = "0.1055",
                    volume = "2137664.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680064680000,
                    startPrice = "0.1049",
                    highPrice = "0.1055",
                    lowPrice = "0.1049",
                    closePrice = "0.1055",
                    volume = "2361352.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680064620000,
                    startPrice = "0.1049",
                    highPrice = "0.1049",
                    lowPrice = "0.1047",
                    closePrice = "0.1049",
                    volume = "764702.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680064560000,
                    startPrice = "0.1049",
                    highPrice = "0.1049",
                    lowPrice = "0.1048",
                    closePrice = "0.1048",
                    volume = "206618.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680064500000,
                    startPrice = "0.1048",
                    highPrice = "0.1049",
                    lowPrice = "0.1047",
                    closePrice = "0.1048",
                    volume = "450634.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680064440000,
                    startPrice = "0.1048",
                    highPrice = "0.1048",
                    lowPrice = "0.1047",
                    closePrice = "0.1048",
                    volume = "434648.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680064380000,
                    startPrice = "0.1045",
                    highPrice = "0.1048",
                    lowPrice = "0.1045",
                    closePrice = "0.1048",
                    volume = "811660.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680064320000,
                    startPrice = "0.1046",
                    highPrice = "0.1046",
                    lowPrice = "0.1045",
                    closePrice = "0.1045",
                    volume = "63978.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680064260000,
                    startPrice = "0.1046",
                    highPrice = "0.1046",
                    lowPrice = "0.1045",
                    closePrice = "0.1046",
                    volume = "271880.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680064200000,
                    startPrice = "0.1046",
                    highPrice = "0.1047",
                    lowPrice = "0.1045",
                    closePrice = "0.1046",
                    volume = "366974.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680064140000,
                    startPrice = "0.1048",
                    highPrice = "0.1048",
                    lowPrice = "0.1044",
                    closePrice = "0.1047",
                    volume = "475442.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680064080000,
                    startPrice = "0.1043",
                    highPrice = "0.1048",
                    lowPrice = "0.1039",
                    closePrice = "0.1048",
                    volume = "498314.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680064020000,
                    startPrice = "0.1045",
                    highPrice = "0.1046",
                    lowPrice = "0.1041",
                    closePrice = "0.1041",
                    volume = "296694.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680063960000,
                    startPrice = "0.1044",
                    highPrice = "0.1048",
                    lowPrice = "0.1037",
                    closePrice = "0.1045",
                    volume = "711234.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680063900000,
                    startPrice = "0.1036",
                    highPrice = "0.1049",
                    lowPrice = "0.1036",
                    closePrice = "0.1049",
                    volume = "707160.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680063840000,
                    startPrice = "0.1035",
                    highPrice = "0.1036",
                    lowPrice = "0.1035",
                    closePrice = "0.1036",
                    volume = "93014.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null
                ),
                KfitBaseChart(
                    dateTime = 1680063780000,
                    startPrice = "0.1035",
                    highPrice = "0.1036",
                    lowPrice = "0.1033",
                    closePrice = "0.1034",
                    volume = "82414.000000",
                    amount = "",
                    sessionId = "2",
                    createdTime = null
                )
            )
            chartDataList = list
        } else {
            val list = mutableListOf<KfitBaseChart>()

            val cal = Calendar.getInstance()
            cal.time = Date()
//            cal.add(Calendar.MONTH, 2)
//            cal.add(Calendar.DATE, -3)
//            cal.time.time

            for (i in 0 until 20 step 1) {
                val testlist = mutableListOf(
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 124000,
                        highPrice = 126500,
                        lowPrice = 123500,
                        closePrice = 126000,
                        volume = 2591426,
                        amount = 326519676000
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 122500,
                        highPrice = 124500,
                        lowPrice = 122000,
                        closePrice = 124000,
                        volume = 2694306,
                        amount = 334093944000
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 122500,
                        highPrice = 123500,
                        lowPrice = 122000,
                        closePrice = 122000,
                        volume = 2161686,
                        amount = 263725692000
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 120500,
                        highPrice = 120500,
                        lowPrice = 120500,
                        closePrice = 120500,
                        volume = 3266351,
                        amount = 398494822000
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 127500,
                        highPrice = 127500,
                        lowPrice = 124500,
                        closePrice = 124500,
                        volume = 2503143,
                        amount = 311641303500
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 127000,
                        highPrice = 128000,
                        lowPrice = 126000,
                        closePrice = 126500,
                        volume = 2113369,
                        amount = 267341178500
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 128000,
                        highPrice = 129500,
                        lowPrice = 127000,
                        closePrice = 129500,
                        volume = 1876803,
                        amount = 243045988500
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 128500,
                        highPrice = 129500,
                        lowPrice = 128000,
                        closePrice = 128500,
                        volume = 1973753,
                        amount = 253627260500
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 127000,
                        highPrice = 127500,
                        lowPrice = 125500,
                        closePrice = 127000,
                        volume = 2022191,
                        amount = 256818257000
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 122500,
                        highPrice = 124000,
                        lowPrice = 120500,
                        closePrice = 123000,
                        volume = 4529517,
                        amount = 557130591000
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 125500,
                        highPrice = 126000,
                        lowPrice = 122500,
                        closePrice = 122500,
                        volume = 5994634,
                        amount = 734342665000
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 127000,
                        highPrice = 127500,
                        lowPrice = 125000,
                        closePrice = 127500,
                        volume = 4183489,
                        amount = 533394847500
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 129500,
                        highPrice = 130000,
                        lowPrice = 128000,
                        closePrice = 128500,
                        volume = 2561169,
                        amount = 329110216500
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 127500,
                        highPrice = 129000,
                        lowPrice = 126500,
                        closePrice = 128500,
                        volume = 2555553,
                        amount = 328388560500
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 128500,
                        highPrice = 129000,
                        lowPrice = 125500,
                        closePrice = 126000,
                        volume = 2883678,
                        amount = 363343428000
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 127000,
                        highPrice = 129000,
                        lowPrice = 126000,
                        closePrice = 128500,
                        volume = 2815912,
                        amount = 361844692000
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 126000,
                        highPrice = 127000,
                        lowPrice = 124000,
                        closePrice = 127000,
                        volume = 2447209,
                        amount = 310795543000
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 126500,
                        highPrice = 126500,
                        lowPrice = 124000,
                        closePrice = 125000,
                        volume = 2330788,
                        amount = 291348500000
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 124500,
                        highPrice = 126000,
                        lowPrice = 123000,
                        closePrice = 125500,
                        volume = 4423140,
                        amount = 555104070000
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 121500,
                        highPrice = 123000,
                        lowPrice = 120500,
                        closePrice = 123000,
                        volume = 3135209,
                        amount = 385630707000
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 122000,
                        highPrice = 122500,
                        lowPrice = 119000,
                        closePrice = 119500,
                        volume = 3186493,
                        amount = 380785913500
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 130000,
                        highPrice = 130500,
                        lowPrice = 126500,
                        closePrice = 128000,
                        volume = 3950944,
                        amount = 505720832000
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 127500,
                        highPrice = 132500,
                        lowPrice = 127000,
                        closePrice = 130500,
                        volume = 3262746,
                        amount = 425788353000
                    ),
                    convertKfitBaseChart(
                        dateTime = Timestamp(cal.apply {
                            add(
                                Calendar.DATE,
                                -1
                            )
                        }.time.time).time,
                        startPrice = 137500,
                        highPrice = 136000,
                        lowPrice = 139000,
                        closePrice = 136000,
                        volume = 2061454,
                        amount = 280357744000
                    )
                )

                list.addAll(testlist)
            }

            chartDataList = list
        }
        var nextKey = "1"

        if (request.chartDivision == "WEEK") {
            if (request.nextKey == listOf("20170724")) {
                return KfitBaseChartResponseEntity(
                    page = KfitPageEntity(hasMore = false, nextKey = listOf()),
                    list = chartDataList,
                    data = null
                )
            }
            return KfitBaseChartResponseEntity(
                page = KfitPageEntity(hasMore = true, nextKey = listOf("20170724")),
                list = chartDataList,
                data = null
            )
        }

        return KfitBaseChartResponseEntity(
            page = KfitPageEntity(hasMore = false, nextKey = listOf()),
            list = chartDataList,
            data = KfitBaseChartData(
                preMarketStartTime = "",
                preMarketEndTime = "",
                marketStartTime = "093000",
                marketEndTime = "160000",
                afterMarketStartTime = "",
                afterMarketEndTime = "",
                dayMarketStartTime = "",
                dayMarketEndTime = "",
            )
        )
    }

    suspend fun getTradeData(request: KfitTradeDataRequestEntity): KfitTradeDataResponseEntity {
//        delay(200)

        val list = mutableListOf<KfitTradingListItemEntity>()

        val cal = Calendar.getInstance()
        val pattern = SimpleDateFormat("yyyyMMdd")
        cal.time = Date()
//        for (i in 0 until 71 step 1) {
//            val tradeDataResponseList = mutableListOf(
//                KfitTradingListItemEntity(
//                    orderedDate = pattern.format(cal.apply{ add(Calendar.DATE, -1)}.time).toString(), //주문일자
//                    sellQuantity = "0", // 소수점매도수량
//                    buyQuantity = "2", // 소수점매수수량
//                    sellAveragePrice = "0", // 소수점매도평균가격
//                    buyAveragePrice = "20" // 소수점매수평균가격
//                ),
//                KfitTradingListItemEntity(
//                    orderedDate = pattern.format(cal.apply{ add(Calendar.DATE, -1)}.time).toString(), //주문일자
//                    sellQuantity = "3", // 소수점매도수량
//                    buyQuantity = "0", // 소수점매수수량
//                    sellAveragePrice = "30", // 소수점매도평균가격
//                    buyAveragePrice = "0" // 소수점매수평균가격
//                ),
//                KfitTradingListItemEntity(
//                    orderedDate = pattern.format(cal.apply{ add(Calendar.DATE, -1)}.time).toString(), //주문일자
//                    sellQuantity = "5", // 소수점매도수량
//                    buyQuantity = "6", // 소수점매수수량
//                    sellAveragePrice = "50", // 소수점매도평균가격
//                    buyAveragePrice = "60" // 소수점매수평균가격
//                ),
//                KfitTradingListItemEntity(
//                    orderedDate = pattern.format(cal.apply{ add(Calendar.DATE, -1)}.time).toString(), //주문일자
//                    sellQuantity = "7", // 소수점매도수량
//                    buyQuantity = "0", // 소수점매수수량
//                    sellAveragePrice = "70", // 소수점매도평균가격
//                    buyAveragePrice = "0" // 소수점매수평균가격
//                ),
//                KfitTradingListItemEntity(
//                    orderedDate = pattern.format(cal.apply{ add(Calendar.DATE, -1)}.time).toString(), //주문일자
//                    sellQuantity = "9", // 소수점매도수량
//                    buyQuantity = "10", // 소수점매수수량
//                    sellAveragePrice = "90", // 소수점매도평균가격
//                    buyAveragePrice = "100" // 소수점매수평균가격
//                ),
//                KfitTradingListItemEntity(
//                    orderedDate = pattern.format(cal.apply{ add(Calendar.DATE, -1)}.time).toString(), //주문일자
//                    sellQuantity = "0", // 소수점매도수량
//                    buyQuantity = "12", // 소수점매수수량
//                    sellAveragePrice = "0", // 소수점매도평균가격
//                    buyAveragePrice = "120" // 소수점매수평균가격
//                ),
//                KfitTradingListItemEntity(
//                    orderedDate = pattern.format(cal.apply{ add(Calendar.DATE, -1)}.time).toString(), //주문일자
//                    sellQuantity = "13", // 소수점매도수량
//                    buyQuantity = "14", // 소수점매수수량
//                    sellAveragePrice = "130", // 소수점매도평균가격
//                    buyAveragePrice = "140" // 소수점매수평균가격
//                )
//            )
//            list.addAll(tradeDataResponseList)
//        }

//        for (i in 0 until 5 step 1) {
        val tradeDataResponseList = mutableListOf(
            KfitTradingListItemEntity(
                orderedDate = "20220317", //주문일자
                sellQuantity = "0.1234", // 소수점매도수량
                buyQuantity = "0.23", // 소수점매수수량
                sellAveragePrice = "234240", // 소수점매도평균가격
                buyAveragePrice = "85500" // 소수점매수평균가격
            ),
            KfitTradingListItemEntity(
                orderedDate = "20220315", //주문일자
                sellQuantity = "34", // 소수점매도수량
                buyQuantity = "2223", // 소수점매수수량
                sellAveragePrice = "23420", // 소수점매도평균가격
                buyAveragePrice = "85500" // 소수점매수평균가격
            ),
            KfitTradingListItemEntity(
                orderedDate = "20220311", //주문일자
                sellQuantity = "0.1234", // 소수점매도수량
                buyQuantity = "0.233", // 소수점매수수량
                sellAveragePrice = "223430", // 소수점매도평균가격
                buyAveragePrice = "85500" // 소수점매수평균가격
            )
//            ,
//            KfitTradingListItemEntity(
//                orderedDate = "20211108", //주문일자
//                sellQuantity = "0", // 소수점매도수량
//                buyQuantity = "18", // 소수점매수수량
//                sellAveragePrice = "0", // 소수점매도평균가격
//                buyAveragePrice = "123000" // 소수점매수평균가격
//            ),
//            KfitTradingListItemEntity(
//                orderedDate = "20211111", //주문일자
//                sellQuantity = "20", // 소수점매도수량
//                buyQuantity = "0", // 소수점매수수량
//                sellAveragePrice = "110000", // 소수점매도평균가격
//                buyAveragePrice = "0" // 소수점매수평균가격
//            ),
//            KfitTradingListItemEntity(
//                orderedDate = "20211115", //주문일자
//                sellQuantity = "9", // 소수점매도수량
//                buyQuantity = "0", // 소수점매수수량
//                sellAveragePrice = "165000", // 소수점매도평균가격
//                buyAveragePrice = "0" // 소수점매수평균가격
//            )
        )
        list.addAll(tradeDataResponseList)
//        }

        return KfitTradeDataResponseEntity(tradingList = list)
    }

    suspend fun getForeignRightData(request: KfitForeignRightChartRequestEntity): KfitForeignRightChartResponseEntity {
        delay(200)
        val data_list = mutableListOf<KfitRightDateTimeEntity>()

//        data_list.add(
//            KfitRightDateTimeEntity(
//                rightDateTime = 1636297200000,
//                right = "Cash Dividend",
//                dividendAmount = "0.205000",
//                dividendCurrency = "USD"
//            )
//        )

//        data_list.add(
//            KfitRightDateTimeEntity(
//                rightDateTime = 1628434800000,
//                right = "Stock split",
//                dividendAmount = "0.2405000",
//                dividendCurrency = "USD"
//            )
//        )

        data_list.add(
            KfitRightDateTimeEntity(
                rightDateTime = 1628434800000,
                right = "Cash Dividend",
                dividendAmount = "1234567890",
                dividendCurrency = "USD"
            )
        )

//        data_list.add(KfitRightDateTimeEntity(rightDateTime = System.currentTimeMillis()))
//        right_list.add(KfitRightEntity(right = "유상증자"))
//        dividend_list.add(KfitDividendEntity(dividendAmount = "8200", dividendCurrency = "8200"))
//
//        data_list.add(KfitRightDateTimeEntity(rightDateTime = System.currentTimeMillis()))
//        right_list.add(KfitRightEntity(right = "주식분할"))
//        dividend_list.add(KfitDividendEntity(dividendAmount = "8200", dividendCurrency = "8200"))
//
//        data_list.add(KfitRightDateTimeEntity(rightDateTime = System.currentTimeMillis()))
//        right_list.add(KfitRightEntity(right = "현금배당"))
//        dividend_list.add(KfitDividendEntity(dividendAmount = "8200", dividendCurrency = "8200"))
//
//        data_list.add(KfitRightDateTimeEntity(rightDateTime = System.currentTimeMillis()))
//        right_list.add(KfitRightEntity(right = "주식배당"))
//        dividend_list.add(KfitDividendEntity(dividendAmount = "8200", dividendCurrency = "8200"))

//        val kfitForeignRightChartResponseEntity = KfitForeignRightChartResponseEntity(
//            page = KfitPageEntity(hasMore = true, nextKey = listOf("1", "2", "3")),
//            dataList = data_list,
//            rightList = right_list,
//            dividendList = dividend_list
//        )

        val kfitForeignRightChartResponseEntity = KfitForeignRightChartResponseEntity(
            page = KfitPageEntity(hasMore = true, nextKey = listOf("99999999")),
            dataList = data_list
        )
        return kfitForeignRightChartResponseEntity
    }

    suspend fun getDomesticFinancialData(request: KfitDomesticFinancialChartRequestEntity): KfitDomesticFinancialChartResponseEntity {
        delay(200)
        val datalist = mutableListOf<KfitDomesticFinancialChart>()
        datalist.add(
            KfitDomesticFinancialChart(
                dateTime = 1636297200000,
                financialDate = "202012",
                businessProfit = "29000346785",
                businessProfitEarning = "+0.001",
                businessProfitRateYoy = "-0.013",
                businessProfitRateQoq = "-0.003",
                netProfit = "42512512309",
                netProfitEarning = "0.0101",
                netProfitRateYoy = "-0.0013",
                netProfitRateQoq = "0.003",
                investOpinion = "73000",
                goalStockPrice = "73000"
            )
        )

        datalist.add(
            KfitDomesticFinancialChart(
                dateTime = 202109011722000,
                financialDate = "202001",
                businessProfit = "2341523",
                businessProfitEarning = "0",
                businessProfitRateYoy = "21.22234234",
                businessProfitRateQoq = "-54.33234234",
                netProfit = "5331249",
                netProfitEarning = "",
                netProfitRateYoy = "21.22234234",
                netProfitRateQoq = "-54.33234234",
                investOpinion = "73000",
                goalStockPrice = "73000"
            )
        )

        datalist.add(
            KfitDomesticFinancialChart(
                dateTime = 202106101722000,
                financialDate = "202001",
                businessProfit = "34",
                businessProfitEarning = "-23.22234234",
                businessProfitRateYoy = "21.22234324",
                businessProfitRateQoq = "-54.33234234",
                netProfit = "-23409",
                netProfitEarning = "23.22234234",
                netProfitRateYoy = "21.22234234",
                netProfitRateQoq = "-54.33234234",
                investOpinion = "73000",
                goalStockPrice = "73000"
            )
        )

        val kfitDomesticFinancialChartResponseEntity = KfitDomesticFinancialChartResponseEntity(
            page = KfitPageEntity(hasMore = true, nextKey = listOf("1", "2", "3")),
            dataList = datalist
        )
        return kfitDomesticFinancialChartResponseEntity
    }

    fun getDomesticMrkIndcChart(request: KfitDomesticMrkIndcChartRequestEntity): KfitDomesticMrkIndcChartResponseEntity {
        val datalist = mutableListOf<KfitDomesticMrkIndcChart>()

        if (request.indicatorDivision == "FOREIGN") {
            val responseList = mutableListOf(
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1625065200000,
                    foreignRate = 49.688362,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1624978800000,
                    foreignRate = 49.801460,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1624892400000,
                    foreignRate = 49.568565,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1624806000000,
                    foreignRate = 49.607632,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1624546800000,
                    foreignRate = 49.559658,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1624460400000,
                    foreignRate = 49.529842,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1624374000000,
                    foreignRate = 49.494972,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1624287600000,
                    foreignRate = 49.418777,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1624201200000,
                    foreignRate = 49.361183,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1623942000000,
                    foreignRate = 49.453667,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1623855600000,
                    foreignRate = 49.576557,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1623769200000,
                    foreignRate = 49.612103,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1623682800000,
                    foreignRate = 49.562988,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1623596400000,
                    foreignRate = 49.513466,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1623337200000,
                    foreignRate = 49.514477,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1623250800000,
                    foreignRate = 49.307804,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1623164400000,
                    foreignRate = 49.284294,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1623078000000,
                    foreignRate = 49.470200,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1622991600000,
                    foreignRate = 49.400135,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1622732400000,
                    foreignRate = 49.395180,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1622646000000,
                    foreignRate = 49.345692,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1622559600000,
                    foreignRate = 49.260990,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1622473200000,
                    foreignRate = 49.267818,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1622386800000,
                    foreignRate = 49.250652,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1622127600000,
                    foreignRate = 49.177010,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1622041200000,
                    foreignRate = 49.231781,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1621954800000,
                    foreignRate = 49.112160,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1621868400000,
                    foreignRate = 49.036949,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1621782000000,
                    foreignRate = 48.955662,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1621522800000,
                    foreignRate = 48.943840,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1621436400000,
                    foreignRate = 48.949150,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1621263600000,
                    foreignRate = 48.945930,
                    foreignNet = 0,
                    institutionNet = 0,
                    individualNet = 0
                )
            )
            datalist.addAll(responseList)
        } else if (request.indicatorDivision == "INVESTOR") {
            val responseList = mutableListOf(
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1625065200000,
                    foreignRate = 49.688362,
                    foreignNet = 2314,
                    institutionNet = 65430,
                    individualNet = 98890
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1624978800000,
                    foreignRate = 49.801460,
                    foreignNet = 54250,
                    institutionNet = 3450,
                    individualNet = 6780
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1624892400000,
                    foreignRate = 49.568565,
                    foreignNet = 12340,
                    institutionNet = 653420,
                    individualNet = 6780
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1624806000000,
                    foreignRate = 49.607632,
                    foreignNet = 12340,
                    institutionNet = 2450,
                    individualNet = 65780
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1624546800000,
                    foreignRate = 49.559658,
                    foreignNet = -2423,
                    institutionNet = 3450,
                    individualNet = -45670
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1624460400000,
                    foreignRate = 49.529842,
                    foreignNet = 214240,
                    institutionNet = 3450,
                    individualNet = 5670
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1624374000000,
                    foreignRate = 49.494972,
                    foreignNet = 12440,
                    institutionNet = 3450,
                    individualNet = 5670
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1624287600000,
                    foreignRate = 49.418777,
                    foreignNet = -324340,
                    institutionNet = -3460,
                    individualNet = 5670
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1624201200000,
                    foreignRate = 49.361183,
                    foreignNet = 34340,
                    institutionNet = 3460,
                    individualNet = -5670
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1623942000000,
                    foreignRate = 49.453667,
                    foreignNet = 343430,
                    institutionNet = 360,
                    individualNet = 5670
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1623855600000,
                    foreignRate = 49.576557,
                    foreignNet = 434340,
                    institutionNet = -3640,
                    individualNet = 3450
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1623769200000,
                    foreignRate = 49.612103,
                    foreignNet = 34340,
                    institutionNet = 3620,
                    individualNet = -3450
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1623682800000,
                    foreignRate = 49.562988,
                    foreignNet = 2430,
                    institutionNet = 24350,
                    individualNet = 2450
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1623596400000,
                    foreignRate = 49.513466,
                    foreignNet = 2340,
                    institutionNet = -3450,
                    individualNet = -2350
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1623337200000,
                    foreignRate = 49.514477,
                    foreignNet = -2340,
                    institutionNet = 3450,
                    individualNet = 2340
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1623250800000,
                    foreignRate = 49.307804,
                    foreignNet = 430,
                    institutionNet = -673450,
                    individualNet = 2340
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1623164400000,
                    foreignRate = 49.284294,
                    foreignNet = 2340,
                    institutionNet = 0,
                    individualNet = 3460
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1623078000000,
                    foreignRate = 49.470200,
                    foreignNet = 234250,
                    institutionNet = 0,
                    individualNet = -3460
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1622991600000,
                    foreignRate = 49.400135,
                    foreignNet = 5450,
                    institutionNet = 0,
                    individualNet = -34460
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1622732400000,
                    foreignRate = 49.395180,
                    foreignNet = 6570,
                    institutionNet = 0,
                    individualNet = -233460
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1622646000000,
                    foreignRate = 49.345692,
                    foreignNet = -7680,
                    institutionNet = 0,
                    individualNet = -4570
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1622559600000,
                    foreignRate = 49.260990,
                    foreignNet = 4560,
                    institutionNet = 0,
                    individualNet = 4560
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1622473200000,
                    foreignRate = 49.267818,
                    foreignNet = 4560,
                    institutionNet = 0,
                    individualNet = 4570
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1622386800000,
                    foreignRate = 49.250652,
                    foreignNet = 23450,
                    institutionNet = 0,
                    individualNet = 4560
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1622127600000,
                    foreignRate = 49.177010,
                    foreignNet = 2340,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1622041200000,
                    foreignRate = 49.231781,
                    foreignNet = 2340,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1621954800000,
                    foreignRate = 49.112160,
                    foreignNet = 1340,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1621868400000,
                    foreignRate = 49.036949,
                    foreignNet = 3450,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1621782000000,
                    foreignRate = 48.955662,
                    foreignNet = 4530,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1621522800000,
                    foreignRate = 48.943840,
                    foreignNet = 650,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1621436400000,
                    foreignRate = 48.949150,
                    foreignNet = 650,
                    institutionNet = 0,
                    individualNet = 0
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1621263600000,
                    foreignRate = 48.945930,
                    foreignNet = 7770,
                    institutionNet = 0,
                    individualNet = 0
                )
            )
            datalist.addAll(responseList)
        } else if (request.indicatorDivision == "INVESTOR" && request.investorDivision == "ALL") {
            val responseList = mutableListOf(
//                convertKfitDomesticMrkIndcChart(dateTime=1625065200000, foreignRate=49.688362, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1624978800000, foreignRate=49.801460, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1624892400000, foreignRate=49.568565, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1624806000000, foreignRate=49.607632, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1624546800000, foreignRate=49.559658, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1624460400000, foreignRate=49.529842, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1624374000000, foreignRate=49.494972, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1624287600000, foreignRate=49.418777, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1624201200000, foreignRate=49.361183, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1623942000000, foreignRate=49.453667, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1623855600000, foreignRate=49.576557, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1623769200000, foreignRate=49.612103, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1623682800000, foreignRate=49.562988, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1623596400000, foreignRate=49.513466, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1623337200000, foreignRate=49.514477, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1623250800000, foreignRate=49.307804, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1623164400000, foreignRate=49.284294, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1623078000000, foreignRate=49.470200, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1622991600000, foreignRate=49.400135, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1622732400000, foreignRate=49.395180, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1622646000000, foreignRate=49.345692, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1622559600000, foreignRate=49.260990, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1622473200000, foreignRate=49.267818, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1622386800000, foreignRate=49.250652, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1622127600000, foreignRate=49.177010, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1622041200000, foreignRate=49.231781, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1621954800000, foreignRate=49.112160, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1621868400000, foreignRate=49.036949, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1621782000000, foreignRate=48.955662, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1621522800000, foreignRate=48.943840, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1621436400000, foreignRate=48.949150, foreignNet=0, institutionNet=0, individualNet=0),
//                convertKfitDomesticMrkIndcChart(dateTime=1621263600000, foreignRate=48.945930, foreignNet=0, institutionNet=0, individualNet=0)
//
//
//
//
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1621782000000,
                    foreignRate = 0.0,
                    foreignNet = -58691,
                    institutionNet = 486350,
                    individualNet = 486350
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1621522800000,
                    foreignRate = 0.0,
                    foreignNet = -121887,
                    institutionNet = 176109,
                    individualNet = 176109
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1621436400000,
                    foreignRate = 0.0,
                    foreignNet = 89948,
                    institutionNet = -284076,
                    individualNet = -284076
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1621263600000,
                    foreignRate = 0.0,
                    foreignNet = -54361,
                    institutionNet = -677997,
                    individualNet = -677997
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1621177200000,
                    foreignRate = 0.0,
                    foreignNet = -271377,
                    institutionNet = 200307,
                    individualNet = 200307
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1620918000000,
                    foreignRate = 0.0,
                    foreignNet = -363324,
                    institutionNet = 286712,
                    individualNet = 286712
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1620831600000,
                    foreignRate = 0.0,
                    foreignNet = -316597,
                    institutionNet = 452149,
                    individualNet = 452149
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1620745200000,
                    foreignRate = 0.0,
                    foreignNet = -1536356,
                    institutionNet = 1781694,
                    individualNet = 1781694
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1620658800000,
                    foreignRate = 0.0,
                    foreignNet = -3319850,
                    institutionNet = 5021640,
                    individualNet = 5021640
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1620572400000,
                    foreignRate = 0.0,
                    foreignNet = -344672,
                    institutionNet = 290144,
                    individualNet = 290144
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1620313200000,
                    foreignRate = 0.0,
                    foreignNet = 69949,
                    institutionNet = -193999,
                    individualNet = -193999
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1620226800000,
                    foreignRate = 0.0,
                    foreignNet = -852456,
                    institutionNet = 1004406,
                    individualNet = 1004406
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1620054000000,
                    foreignRate = 0.0,
                    foreignNet = -40097,
                    institutionNet = -20957,
                    individualNet = -20957
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1619967600000,
                    foreignRate = 0.0,
                    foreignNet = 862029,
                    institutionNet = -560000,
                    individualNet = -560000
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1619708400000,
                    foreignRate = 0.0,
                    foreignNet = -296331,
                    institutionNet = 522988,
                    individualNet = 522988
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1619622000000,
                    foreignRate = 0.0,
                    foreignNet = -347306,
                    institutionNet = 694328,
                    individualNet = 694328
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1619535600000,
                    foreignRate = 0.0,
                    foreignNet = -1091312,
                    institutionNet = 1419115,
                    individualNet = 1419115
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1619449200000,
                    foreignRate = 0.0,
                    foreignNet = 408705,
                    institutionNet = -684139,
                    individualNet = -684139
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1619362800000,
                    foreignRate = 0.0,
                    foreignNet = -535170,
                    institutionNet = 536525,
                    individualNet = 536525
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1619103600000,
                    foreignRate = 0.0,
                    foreignNet = -380778,
                    institutionNet = 790967,
                    individualNet = 790967
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1619017200000,
                    foreignRate = 0.0,
                    foreignNet = -202370,
                    institutionNet = 459821,
                    individualNet = 459821
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1618930800000,
                    foreignRate = 0.0,
                    foreignNet = -1794815,
                    institutionNet = 2620459,
                    individualNet = 2620459
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1618844400000,
                    foreignRate = 0.0,
                    foreignNet = -11916,
                    institutionNet = 5228,
                    individualNet = 5228
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1618758000000,
                    foreignRate = 0.0,
                    foreignNet = 131970,
                    institutionNet = 125004,
                    individualNet = 125004
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1618498800000,
                    foreignRate = 0.0,
                    foreignNet = 25720,
                    institutionNet = 325568,
                    individualNet = 325568
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1618412400000,
                    foreignRate = 0.0,
                    foreignNet = -127303,
                    institutionNet = 234160,
                    individualNet = 234160
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1618326000000,
                    foreignRate = 0.0,
                    foreignNet = -491755,
                    institutionNet = 753078,
                    individualNet = 753078
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1618239600000,
                    foreignRate = 0.0,
                    foreignNet = 278192,
                    institutionNet = -179565,
                    individualNet = -179565
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1618153200000,
                    foreignRate = 0.0,
                    foreignNet = -516003,
                    institutionNet = 880336,
                    individualNet = 880336
                ),
                convertKfitDomesticMrkIndcChart(
                    dateTime = 1617894000000,
                    foreignRate = 0.0,
                    foreignNet = -228550,
                    institutionNet = 435446,
                    individualNet = 435446
                )
            )
            datalist.addAll(responseList)
        }

        return KfitDomesticMrkIndcChartResponseEntity(
            page = KfitPageEntity(hasMore = true, nextKey = listOf("20200902")),
            dataList = datalist
        )
    }

    fun subscribeMessageFlow(): Flow<Any> = messageFlow

    fun subscribeConnectionStateFlow(): Flow<KfitConnectionStateEntity> = connectionStateFlow

    fun subscribeAveragePriceFlow(): Flow<KfitAverageBuyPriceStreamEntity> = averagePriceFlow

    fun subscribe(code: Int) {
        subscribe.add(code)
    }

    fun unSubscribe(code: Int) {
        subscribe.remove(code)
    }

    fun convertKfitBaseChart(
        dateTime: Long,
        startPrice: Long,
        highPrice: Long,
        lowPrice: Long,
        closePrice: Long,
        volume: Long,
        amount: Long
    ): KfitBaseChart {
        return KfitBaseChart(
            dateTime, (startPrice).toString(), (highPrice).toString(),
            (lowPrice).toString(), (closePrice).toString(), (volume).toString(), amount.toString(),
            "0", null
        )
    }

    fun convertKfitDomesticMrkIndcChart(
        dateTime: Long,
        foreignRate: Double,
        foreignNet: Long,
        institutionNet: Long,
        individualNet: Long
    ): KfitDomesticMrkIndcChart {
        return KfitDomesticMrkIndcChart(
            dateTime, foreignRate.toString(),
            foreignNet.toString(), institutionNet.toString(),
            individualNet.toString()
        )
    }
}