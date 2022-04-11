package com.bagooni.petmliy_android_app.walk

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bagooni.petmliy_android_app.databinding.FragmentWalkBinding

@Suppress("UNREACHABLE_CODE")
class WalkFragment : Fragment() { // 시작
    private var _binding: FragmentWalkBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWalkBinding.inflate(inflater,container,false)
        return binding.root

        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->

        }
    }
}


