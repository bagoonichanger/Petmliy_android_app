package com.bagooni.petmliy_android_app.home.Weather.Interface

import com.bagooni.petmliy_android_app.home.Weather.WeatherModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherAPIService {
    @GET("data/2.5/{path}")
    fun doGetJsonDataWeather(
        @Path("path") path: String,
        @Query("q") q: String,
        @Query("appid") appid: String,
    ): Call<WeatherModel>

}