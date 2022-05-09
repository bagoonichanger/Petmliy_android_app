package com.bagooni.petmliy_android_app.walk.Fragment.Api

import com.bagooni.petmliy_android_app.walk.Fragment.Dto.sendTrackingDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface CustomWalkApi {
    @Multipart
    @POST("/api/walk/save")
    fun sendTrackingData(
        @Header("email") email: String,
        @Part postImg: MultipartBody.Part,
        @PartMap data: HashMap<String, RequestBody>
    ): Call<sendTrackingDto>
}