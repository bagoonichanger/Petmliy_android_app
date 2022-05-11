package com.bagooni.petmliy_android_app.home.Fragment.Api

import com.bagooni.petmliy_android_app.map.model.Dto.LikePlaceDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface BookmarkApi {
    @GET("/api/place/findByEmail")
    fun searchAllData(
        @Header("email") email: String,
    ): Call<List<LikePlaceDto>>
}