package com.bagooni.petmliy_android_app.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentHomeBinding

class HomeFragment : Fragment(){
    private var _binding: FragmentHomeBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        binding.mypageButton.setOnClickListener(ButtonListener())
        binding.weatherButton.setOnClickListener(ButtonListener())
        binding.albumButton.setOnClickListener(ButtonListener())
        binding.bookmarkButton.setOnClickListener (ButtonListener())
        return binding.root
    }

    inner class ButtonListener: View.OnClickListener {
        override fun onClick(p0: View?) {
            var intent = Intent()
            when (p0?.id) {
                R.id.mypageButton -> {
                    intent = Intent(context, MyPageActivity::class.java)
                    startActivity(intent)
                }
                R.id.weatherButton -> {
                    intent = Intent(context, WeatherActivity::class.java)
                    startActivity(intent)
                }
                R.id.albumButton -> {
                    intent = Intent(context, AlbumActivity::class.java)
                    startActivity(intent)
                }
                R.id.bookmarkButton -> {
                    intent = Intent(context, BookMarkActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}