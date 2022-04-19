package com.bagooni.petmliy_android_app.walk

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bagooni.petmliy_android_app.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


class WalkFragment : Fragment(R.layout.fragment_walk){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<FloatingActionButton>(R.id.changeTrackingFragment).setOnClickListener {
            findNavController().navigate(R.id.action_walkFragment_to_trackingFragment)
        }
    }
}


