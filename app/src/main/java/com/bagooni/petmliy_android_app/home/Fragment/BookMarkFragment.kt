package com.bagooni.petmliy_android_app.home.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentBookMarkBinding

class BookMarkFragment : Fragment() {
    private var _binding: FragmentBookMarkBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookMarkBinding.inflate(inflater,container,false)
        binding.closeButton.setOnClickListener {
            findNavController().navigate(R.id.action_bookMarkFragment_to_homeFragment)
        }
        return binding.root
    }

}