package com.bagooni.petmliy_android_app.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bagooni.petmliy_android_app.databinding.ActivityBookMarkBinding

class BookMarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookMarkBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBookMarkBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.closeButton.setOnClickListener { finish() }
    }
}