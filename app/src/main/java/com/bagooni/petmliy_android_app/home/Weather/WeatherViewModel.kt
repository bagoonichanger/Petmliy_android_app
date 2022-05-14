package com.bagooni.petmliy_android_app.home.Weather

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bagooni.petmliy_android_app.home.Weather.Interface.RemoteDataSource
import com.bagooni.petmliy_android_app.home.Weather.Interface.RemoteDataSourceImpl
import org.json.JSONObject
import retrofit2.Response

class WeatherViewModel : ViewModel(){

    private val TAG: String = WeatherViewModel::class.java.simpleName

    private val weatherRepository = WeatherRepository()
    val isSuccWeather = MutableLiveData<Boolean>()
    val responseWeather = MutableLiveData<WeatherModel>()

    fun getWeatherInfoView(jsonObject: JSONObject) {
        Log.d(TAG, "getWeatherInfoView() - jsonObject : " + jsonObject);

        weatherRepository.getWeatherInfo(jsonObject = jsonObject,
            onResponse = {
                if (it.isSuccessful) {
                    Log.d(TAG, "getWeatherInfoView() - Succ : " + it.body());
                    isSuccWeather.value = true
                    responseWeather.value = it.body()
                }
            },
            onFailure = {
                it.printStackTrace()
                Log.d(TAG, "getWeatherInfoView() - Fail : " + it.message);
            }
        )
    }
}

private class WeatherRepository {
    private val retrofitRemoteDataSource: RemoteDataSource = RemoteDataSourceImpl()
    fun getWeatherInfo(
        jsonObject: JSONObject,
        onResponse: (Response<WeatherModel>) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        retrofitRemoteDataSource.getWeatherInfo(jsonObject, onResponse, onFailure)
    }
}