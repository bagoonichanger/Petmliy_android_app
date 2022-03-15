package com.bagooni.petmliy_android_app.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bagooni.petmliy_android_app.databinding.ActivityWeatherBinding

class WeatherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.closeButton.setOnClickListener{ finish() }
    }
}