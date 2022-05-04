package com.bagooni.petmliy_android_app.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentHomeBinding


class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.mypageButton.setOnClickListener { ButtonListener() }
        binding.albumButton.setOnClickListener { ButtonListener()}
        binding.bookmarkButton.setOnClickListener { ButtonListener()}
                return binding.root
    }

    inner class ButtonListener: View.OnClickListener {
        override fun onClick(p0: View?) {
            when (p0?.id) {
                R.id.mypageButton -> {
                    findNavController().navigate(R.id.action_homeFragment_to_myPageFragment)
                }
                R.id.albumButton -> {
                    findNavController().navigate(R.id.action_homeFragment_to_albumFragment)
                }
                R.id.bookmarkButton -> {
                    findNavController().navigate(R.id.action_homeFragment_to_bookMarkFragment)
                }
            }
        }
    }
}