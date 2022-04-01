package com.bagooni.petmliy_android_app.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import com.bagooni.petmliy_android_app.databinding.ActivityAlbumBinding

class AlbumActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlbumBinding
    private val OPEN_GALLERY = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAlbumBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.closeButton.setOnClickListener { finish() }

        val imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            }
        binding.selectPicture.setOnClickListener{
            imagePickerLauncher.launch(
                Intent(Intent.ACTION_PICK).apply {
                    this.type = MediaStore.Images.Media.CONTENT_TYPE
                }
            )
        }
    }

}