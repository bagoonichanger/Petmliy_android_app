package com.bagooni.petmliy_android_app.walk

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentMapBinding
import com.bagooni.petmliy_android_app.databinding.FragmentWalkBinding
import com.bagooni.petmliy_android_app.walk.Activity.TrackingActivity

class WalkFragment : Fragment() { // 시작
    private var _binding: FragmentWalkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentWalkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.changeActivity.setOnClickListener {
            startActivity(Intent(context, TrackingActivity::class.java))
        }
    }
}

