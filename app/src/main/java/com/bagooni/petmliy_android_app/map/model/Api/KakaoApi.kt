package com.bagooni.petmliy_android_app.map.model.Api

import com.bagooni.petmliy_android_app.map.model.Dto.PlaceDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoApi {
    @GET("/v2/local/search/keyword.json")
    fun getSearchPlaces(
        @Header("Authorization") key: String,
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
//        @Query("category_group_code") category: String
    ): Call<PlaceDto>
}