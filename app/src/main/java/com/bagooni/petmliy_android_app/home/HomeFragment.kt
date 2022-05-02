package com.bagooni.petmliy_android_app.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentHomeBinding
import com.bagooni.petmliy_android_app.databinding.FragmentMapBinding
import com.bagooni.petmliy_android_app.home.Fragment.AlbumActivity
import com.bagooni.petmliy_android_app.home.Fragment.BookMarkActivity
import com.bagooni.petmliy_android_app.home.Fragment.MyPageActivity

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mypageButton.setOnClickListener {
//            findNavController().navigate()
        }
        binding.albumButton.setOnClickListener {
//            findNavController().navigate()
        }
        binding.bookmarkButton.setOnClickListener {
//            findNavController().navigate()
        }
    }
}