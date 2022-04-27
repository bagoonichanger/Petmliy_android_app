package com.bagooni.petmliy_android_app.walk

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bagooni.petmliy_android_app.R

import com.google.android.material.floatingactionbutton.FloatingActionButton

class WalkFragment : Fragment(R.layout.fragment_walk){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<FloatingActionButton>(R.id.changeTrackingFragment).setOnClickListener {
            getPermissions()
            findNavController().navigate(R.id.action_walkFragment_to_trackingFragment)
        }
    }

    private fun getPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    ),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_AND_WRITE_EXTERNAL_STORAGE_PERMISSION
                )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_AND_WRITE_EXTERNAL_STORAGE_PERMISSION =
            100
    }
}


