package com.example.testapi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author hanjun.Kim
 * 통신을 위한 Retrofit Client
 * TODO 안드로이드 프런트엔드 앱과 데이터를 주고 받는 간접 통신 구조로 바뀌면 해당 파일 삭제
 */

object KfitChartRetrofitClient {
    val service: KfitChartDelegate = initService()
    // 서버 주소
    private const val BASE_URL = "https://test20.neoredbull.com"

    private fun initService(): KfitChartDelegate =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KfitChartDelegate::class.java)
    
    // API 요청에 인증토큰을 보내야하는 경우 예제
//    val okHttpClient = OkHttpClient.Builder()
//        .addInterceptor(HeaderInterceptor())
//        .build()
//
//    Retrofit.Builder()
//    .baseUrl(BASE_URL)
//    .client(okHttpClient)
//    .addConverterFactory(Json.asConverterFactory(MediaType.parse("application/json")!!))
//    .build()
//    .create(ChartService::class.java)
}

//// TODO API 통신 붙여서 테스트 해볼것.
////  데이터 통신 예제....
//val paramObject = JsonObject()
//val commonHeaderObject = JsonObject()
//
//commonHeaderObject.addProperty("th_tr_tcd", "S")
//commonHeaderObject.addProperty("th_if_tcd", "T")
//commonHeaderObject.addProperty("guid", "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456")
//commonHeaderObject.addProperty("th_qry_c", "200")
//commonHeaderObject.addProperty("th_tr_id", "BasicChart")
//
//val gson = Gson()
//val requestEntity = KfitBaseChartRequestEntity(
//    exchangeId = "001",
//    isAdjustedData = "0",
//    divisionUnit = "1",
//    stockId = "KR7005930003",
//    chartDivision = "DAY",
//    count = "200", // 앱단
//    nextKey = presentInfoKfit.nextKey ?: "" // 최초요청때는 99999999
//)
//
//paramObject.add("common_header", commonHeaderObject)
//paramObject.add("data", gson.toJsonTree(requestEntity))
//
////API request-response
//val job1 = async { repository.stockPresentInfo(requestEntity.stockId, requestEntity.exchangeId) }

//            val job2 = async {
//                com.example.testapi.KfitChartRetrofitClient.service.getBaseChart(paramObject)
//                    .enqueue(object: retrofit2.Callback<KfitBaseChartResponseEntity> {
//                        override fun onFailure(call: Call<KfitBaseChartResponseEntity>, t: Throwable) {
//                            log.info(t.message)
//                        }
//
//                        override fun onResponse(
//                            call: Call<KfitBaseChartResponseEntity>,
//                            response: Response<KfitBaseChartResponseEntity>
//                        ) {
//                            if (response.isSuccessful) {
//                                response.body()?.let {
//                                    updateOutputData(presentInfoKfit, it)
//                                }
//                            }
//                        }
//                    })
//            }
//
//            job2.await()