package com.bagooni.petmliy_android_app.home.Fragment.Api

import retrofit2.http.GET
import retrofit2.http.Query

interface AnalysisApi {
    @GET("api/analysis/breed/dog")
    fun getDog(
        @Query("img") img: String
    )

    @GET("api/analysis/breed/cat")
    fun getCat(
        @Query("img") img: String
    )

    @GET("api/analysis/emotion")
    fun getEmotion(
        @Query("img") img: String
    )
}