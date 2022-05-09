package com.bagooni.petmliy_android_app.home.Weather.Interface

import com.bagooni.petmliy_android_app.home.Weather.WeatherModel
import org.json.JSONObject
import retrofit2.Response

interface RemoteDataSource {
    fun getWeatherInfo(
        jsonObject: JSONObject,
        onResponse: (Response<WeatherModel>) -> Unit,
        onFailure: (Throwable) -> Unit
    )
}
