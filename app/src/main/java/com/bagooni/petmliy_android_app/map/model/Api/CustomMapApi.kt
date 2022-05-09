package com.bagooni.petmliy_android_app.map.model.Api

import com.bagooni.petmliy_android_app.map.model.Dto.LikePlaceDto
import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface CustomMapApi {
    @POST("/api/place/save")
    fun sendLikePlaces(
        @Header("email") email: String,
        @Body data : LikePlaceDto
    ): Call<LikePlaceDto>
}

