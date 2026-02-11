package com.example.testapi

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

/**
 * @author hanjun.Kim
 */
//메인 부모 컨트롤로와 통신하기 위한 프로토콜 정의 >>
interface KfitChartDelegate {
    @Headers( "Content-Type: application/json" )
    @POST(".")
    fun getBaseChart(
        @Body body: JsonObject
    ): Call<TestKfitBaseChartResponseEntity>

    data class Common_header(
        val th_tr_tcd: String,
        val th_if_tcd: String,
        val guid: String,
        val th_qry_c: Int,
        val th_tr_id: String
    )
}