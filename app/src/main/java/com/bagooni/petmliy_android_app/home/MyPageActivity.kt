package com.bagooni.petmliy_android_app.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bagooni.petmliy_android_app.databinding.ActivityMyPageBinding

class MyPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMyPageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.closeButton.setOnClickListener{ finish() }
    }
}