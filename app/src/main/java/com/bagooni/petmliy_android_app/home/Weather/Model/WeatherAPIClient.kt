package com.bagooni.petmliy_android_app.home.Weather.Model

import com.bagooni.petmliy_android_app.home.Weather.CookiesIntercepter
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherAPIClient {
    fun getClient(url: String): Retrofit {
        val okHttpClient: OkHttpClient = OkHttpClient.Builder().addInterceptor(CookiesIntercepter())
            .addNetworkInterceptor(CookiesIntercepter()).build()

        val retrofit: Retrofit = Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit
    }
}