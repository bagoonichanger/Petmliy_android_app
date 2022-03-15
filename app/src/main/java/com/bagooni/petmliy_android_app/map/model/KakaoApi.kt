package com.bagooni.petmliy_android_app.map.model

import com.bagooni.petmliy_android_app.map.model.Response.PlaceDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoApi {
    @GET("/v2/local/search/keyword.json")
    fun getSearchPlaces(
        @Header("Authorization") key: String,
        @Query("query") query: String,
//        @Query("x") x: String,
//        @Query("y") y: String,
//        @Query("category_group_code") category: String
    ): Call<PlaceDto>
}