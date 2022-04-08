package com.bagooni.petmliy_android_app.walk.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.ActivityTrackingBinding
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class TrackingActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityTrackingBinding
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityTrackingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        Glide.with(this).load(R.raw.dog).override(300, 300).into(binding.startImageView)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val seoul = LatLng(37.56,126.97)
        val markerOptions = MarkerOptions().also {
            it.position(seoul)
            it.title("서울")
        }
        mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul))
    }
}