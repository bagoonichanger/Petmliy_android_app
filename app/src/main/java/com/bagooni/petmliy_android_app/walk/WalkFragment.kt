package com.bagooni.petmliy_android_app.walk

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bagooni.petmliy_android_app.databinding.FragmentWalkBinding
import com.bagooni.petmliy_android_app.MainActivity
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentMapBinding
import com.bagooni.petmliy_android_app.walk.Activity.TrackingActivity
import com.bagooni.petmliy_android_app.walk.Activity.WriteActivity

@Suppress("UNREACHABLE_CODE")
class WalkFragment : Fragment() { // 시작
    private var _binding: FragmentWalkBinding? = null
    private val binding get() = _binding!!

    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentWalkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.changeActivity.setOnClickListener {
            startActivity(Intent(activity, TrackingActivity::class.java))
        }
        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            binding.selectDate.text = String.format("%d년 %d월 %d일", year, month+1, dayOfMonth)
        }
        binding.writeButton.setOnClickListener{
            startActivity(Intent(activity, WriteActivity::class.java))
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
}


