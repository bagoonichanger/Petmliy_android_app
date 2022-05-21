package com.bagooni.petmliy_android_app.map.model.Api

import com.bagooni.petmliy_android_app.map.model.Dto.LikePlaceDto
import retrofit2.Call
import retrofit2.http.*

interface CustomMapApi {
    @POST("/api/place/save")
    fun sendLikePlaces(
        @Header("email") email: String,
        @Body data: LikePlaceDto
    ): Call<LikePlaceDto>

    @DELETE("/api/place/delete/{placeId}")
    fun deletePlace(
        @Header("email") email: String,
        @Path("placeId") placeId: Int
    ):Call<Void>
}

