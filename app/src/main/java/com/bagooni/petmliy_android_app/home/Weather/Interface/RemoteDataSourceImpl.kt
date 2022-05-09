package com.bagooni.petmliy_android_app.home.Weather.Interface

import com.bagooni.petmliy_android_app.home.Weather.Model.WeatherAPIClient
import com.bagooni.petmliy_android_app.home.Weather.WeatherModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoteDataSourceImpl : RemoteDataSource{
    override fun getWeatherInfo(
        jsonObject: JSONObject,
        onResponse: (Response<WeatherModel>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val APIService: WeatherAPIService = WeatherAPIClient.getClient(
            jsonObject.get("url").toString()
        ).create(WeatherAPIService::class.java)
        APIService.doGetJsonDataWeather(
            jsonObject.get("path").toString(),
            jsonObject.get("q").toString(),
            jsonObject.get("appid").toString()
        ).enqueue(object :
            Callback<WeatherModel> {
            override fun onResponse(call: Call<WeatherModel>, response: Response<WeatherModel>) {
                onResponse(response)
            }
            override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                onFailure(t)
            }
        }
        )
    }
}