package com.bagooni.petmliy_android_app.walk

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.walk.Db.TrackingViewModel
import com.bagooni.petmliy_android_app.walk.Fragment.Adapter.CalendarRecyclerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashSet
import kotlin.concurrent.thread


class WalkFragment : Fragment(R.layout.fragment_walk) {
    var setDate = mutableListOf<CalendarDay>()

    private val recyclerAdapter = CalendarRecyclerAdapter(detailRecyclerView = {
        findNavController().navigate(
            WalkFragmentDirections.actionWalkFragmentToDetailTrackingFragment(
                tracking = it
            )
        )
    })

    private val viewModel by lazy {
        ViewModelProvider(this, TrackingViewModel.Factory(requireActivity().application)).get(
            TrackingViewModel::class.java
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy/M/d")
        val formatted = current.format(formatter)
        view.findViewById<TextView>(R.id.calendarTitle).text = formatted

        val recyclerView = view.findViewById<RecyclerView>(R.id.calendarRecyclerView)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        initButtons(view)

        var calendarView = view.findViewById<MaterialCalendarView>(R.id.calendarView)
//        val drawable = context?.resources?.getDrawable(R.drawable.home_album_button_background)
//        calendarView.addDecorator(object : DayViewDecorator{
//            override fun shouldDecorate(day: CalendarDay?): Boolean {
//                return day?.equals(CalendarDay.today())!!
//            }
//
//            override fun decorate(view: DayViewFacade?) {
//                if (drawable != null) {
//                    view?.setBackgroundDrawable(drawable)
//                }
//            }
//
//        })
        calendarView.setSelectedDate(CalendarDay.today())
        calendarView.setOnDateChangedListener { widget, date, selected ->
            view.findViewById<TextView>(R.id.calendarTitle).text = "${date.year}/${date.month}/${date.day}"
            viewModel.trackingSortedByCalendar(date.year, date.month, date.day)
                .observe(viewLifecycleOwner, Observer {
                    Log.d("check", "${date.year}/${date.month}/${date.day}")
                    recyclerAdapter.submitList(it)
                })

        }
        val calendar = Calendar.getInstance()
        viewModel.trackingSortedByCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH)
        ).observe(viewLifecycleOwner, Observer {
            recyclerAdapter.submitList(it)
        })

        val r = Runnable {

            val iterator = viewModel.allTracking().iterator()
            while (iterator.hasNext()){
                var item = iterator.next()
                var year = item.year; var month = item.month;  var day = item.day

                setDate.add(CalendarDay.from(year,month,day))
            }
            activity?.runOnUiThread{
                calendarView.addDecorator(object : DayViewDecorator{
                    override fun shouldDecorate(day: CalendarDay?): Boolean {
                        return setDate.contains(day)
                    }

                    override fun decorate(view: DayViewFacade?) {
                        view?.addSpan(DotSpan(5f,Color.RED))
                    }

                })
            }

        }
        val thread = Thread(r)
        thread.start()

//        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
//        calendarView.setOnDateChangeListener { calendarView, year, month, day ->
//            view.findViewById<TextView>(R.id.calendarTitle).text = "${year}/${month+1}/${day}"
//
//            viewModel.trackingSortedByCalendar(year, month, day)
//                .observe(viewLifecycleOwner, Observer {
//                    recyclerAdapter.submitList(it)
//                })
//        }
//        val calendar = Calendar.getInstance()
//        viewModel.trackingSortedByCalendar(
//            calendar.get(Calendar.YEAR),
//            calendar.get(Calendar.MONTH),
//            calendar.get(Calendar.DAY_OF_MONTH)
//        ).observe(viewLifecycleOwner, Observer {
//            Log.d("check", "check")
//            recyclerAdapter.submitList(it)
//        })
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


