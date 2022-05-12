package com.bagooni.petmliy_android_app.walk.Fragment.Api

import com.bagooni.petmliy_android_app.walk.Db.Tracking
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

    @DELETE("/api/walk/delete/{id}/{year}/{month}/{day}")
    fun deleteDate(
        @Header("email") email: String,
        @Path("id") id: Int,
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Path("day") day: Int
    ):Call<Void>

    @GET("/api/walk/findByDate/{year}/{month}/{day}")
    fun searchData(
        @Header("email") email: String,
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Path("day") day: Int
    ): Call<List<Tracking>>

    @GET("/api/walk/findAll")
    fun searchAllData(
        @Header("email") email: String,
    ): Call<List<Tracking>>
}