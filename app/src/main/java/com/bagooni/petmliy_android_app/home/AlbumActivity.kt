package com.bagooni.petmliy_android_app.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bagooni.petmliy_android_app.databinding.ActivityAlbumBinding

class AlbumActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlbumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAlbumBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.closeButton.setOnClickListener { finish() }
    }
}