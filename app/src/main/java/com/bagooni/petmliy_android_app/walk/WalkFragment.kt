package com.bagooni.petmliy_android_app.walk

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.walk.Db.Tracking
import com.bagooni.petmliy_android_app.walk.Db.TrackingViewModel
import com.bagooni.petmliy_android_app.walk.Fragment.Adapter.CalendarRecyclerAdapter
import com.bagooni.petmliy_android_app.walk.Fragment.Service.TrackingUtility
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class WalkFragment : Fragment(R.layout.fragment_walk) {
    private val recyclerAdapter = CalendarRecyclerAdapter(detailRecyclerView = {
        findNavController().navigate(WalkFragmentDirections.actionWalkFragmentToDetailTrackingFragment(tracking = it))
    })

    private val viewModel by lazy {
        ViewModelProvider(this, TrackingViewModel.Factory(requireActivity().application)).get(
            TrackingViewModel::class.java
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.calendarRecyclerView)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        initButtons(view)

        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        calendarView.setOnDateChangeListener { calendarView, year, month, day ->
            viewModel.trackingSortedByCalendar(year, month, day)
                .observe(viewLifecycleOwner, Observer {
                    recyclerAdapter.submitList(it)
                })
        }
        val calendar = Calendar.getInstance()
        viewModel.trackingSortedByCalendar(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).observe(viewLifecycleOwner, Observer {
            Log.d("check", "check")
            recyclerAdapter.submitList(it)
        })
    }

    private fun initButtons(view: View) {
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


