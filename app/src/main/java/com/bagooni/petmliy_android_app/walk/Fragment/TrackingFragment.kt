package com.bagooni.petmliy_android_app.walk.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.PixelCopy
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.walk.Db.Tracking
import com.bagooni.petmliy_android_app.walk.Db.TrackingViewModel
import com.bagooni.petmliy_android_app.Constants.ACTION_PAUSE_SERVICE
import com.bagooni.petmliy_android_app.Constants.ACTION_START_OR_RESUME_SERVICE
import com.bagooni.petmliy_android_app.Constants.ACTION_STOP_SERVICE
import com.bagooni.petmliy_android_app.Constants.MAP_ZOOM
import com.bagooni.petmliy_android_app.Constants.POLYLINE_COLOR
import com.bagooni.petmliy_android_app.Constants.POLYLINE_WIDTH
import com.bagooni.petmliy_android_app.walk.Fragment.Service.Polyline
import com.bagooni.petmliy_android_app.walk.Fragment.Service.TrackingService
import com.bagooni.petmliy_android_app.walk.Fragment.Service.TrackingUtility
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.*
import kotlin.math.round

class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val viewModel by lazy {
        ViewModelProvider(this, TrackingViewModel.Factory(requireActivity().application)).get(TrackingViewModel::class.java)
    }
    private lateinit var mapView: MapView
    private var map: GoogleMap? = null

    private var curTimeInMillis = 0L

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.googleMapView)
        mapView.onCreate(savedInstanceState)

        initViews(view)
        initButtons(view)

        mapView.getMapAsync {
            map = it
            addAllPolylines()
        }

        subscribeToObservers(view)
    }

    private fun subscribeToObservers(view: View) {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            this.isTracking = it
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })
        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            curTimeInMillis = it
            var checkDistanceInMeters = 0
            for (polyline in pathPoints) {
                checkDistanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val checkAvgSpeed =
                round((checkDistanceInMeters / 100f) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f
            view.findViewById<TextView>(R.id.velocity).text = checkAvgSpeed.toString()

            val checkCaloriesBurned = ((checkDistanceInMeters / 1000f) * 70f).toInt()
            view.findViewById<TextView>(R.id.calorie).text = checkCaloriesBurned.toString()

            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis, false)
            view.findViewById<TextView>(R.id.walkTime).text = formattedTime

            view.findViewById<TextView>(R.id.distance).text = (checkDistanceInMeters/ 1000f).toString()
        })
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty())
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
    }

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.builder()
        for (polyline in pathPoints) {
            for (pos in polyline) {
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(), mapView.width, mapView.height, (mapView.height * 0.05f).toInt()
            )
        )
    }

    private fun endTracking_saveToDb() {
        map?.snapshot { bitmap ->
            var distanceInMeters = 0
            for (polyline in pathPoints) {
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed =
                round((distanceInMeters / 100f) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f
            val year = Calendar.getInstance().get(Calendar.YEAR)
            val month = Calendar.getInstance().get(Calendar.MONTH)+1
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val caloriesBurned = ((distanceInMeters / 1000f) * 70f).toInt()

            val tracking = Tracking(
                year,
                month,
                day,
                bitmap,
                avgSpeed,
                distanceInMeters,
                curTimeInMillis,
                caloriesBurned
            )

            viewModel.insertTracking(tracking)
            view?.let {
                Snackbar.make(it, "산책이 저장되었습니다.", Snackbar.LENGTH_LONG).show()
            }
            stopTracking()
        }
    }

    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun initViews(view: View) {
        view.findViewById<AppCompatImageButton>(R.id.startButton).visibility = View.VISIBLE
        view.findViewById<AppCompatImageButton>(R.id.pauseButton).visibility = View.GONE
    }

    private fun initButtons(view: View) {
        view.findViewById<AppCompatImageButton>(R.id.startButton).setOnClickListener {
            if (!isTracking) {
                sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
                view.findViewById<AppCompatImageButton>(R.id.startButton).visibility = View.GONE
                view.findViewById<AppCompatImageButton>(R.id.pauseButton).visibility = View.VISIBLE
                view.findViewById<AppCompatImageButton>(R.id.disabledStopButton).visibility =
                    View.VISIBLE
                view.findViewById<AppCompatImageButton>(R.id.disabledCaptureButton).visibility =
                    View.VISIBLE
            }
//            isTracking = true
        }

        view.findViewById<AppCompatImageButton>(R.id.pauseButton).setOnClickListener {
            if (isTracking) {
                sendCommandToService(ACTION_PAUSE_SERVICE)
                view.findViewById<AppCompatImageButton>(R.id.startButton).visibility = View.VISIBLE
                view.findViewById<AppCompatImageButton>(R.id.pauseButton).visibility = View.GONE
                view.findViewById<AppCompatImageButton>(R.id.disabledStopButton).visibility =
                    View.GONE
                view.findViewById<AppCompatImageButton>(R.id.disabledCaptureButton).visibility =
                    View.GONE
            }
//            isTracking = false
        }

        view.findViewById<AppCompatImageButton>(R.id.stopButton).setOnClickListener {
            showSaveTrackingDialog()
        }

        view.findViewById<AppCompatImageButton>(R.id.disabledStopButton).setOnClickListener {
            Toast.makeText(requireContext(), "산책을 멈추고 중지 해주세요", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<AppCompatImageButton>(R.id.captureButton).setOnClickListener {
            showCaptureTrackingDialog()
        }
        view.findViewById<AppCompatImageButton>(R.id.disabledCaptureButton).setOnClickListener {
            Toast.makeText(requireContext(), "산책을 멈추고 스크린샷 해주세요", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<AppCompatImageButton>(R.id.cancel_tracking).setOnClickListener {
            showCancelTrackingDialog()
        }
    }

    private fun showCaptureTrackingDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("산책 기록을 캡쳐하시겠습니까?")
            .setPositiveButton("캡쳐") { dialog, _ ->
                view?.let {
                    getBitmapFromView(it, requireActivity()) { bitmap ->
                        captureTracking(bitmap)
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun getBitmapFromView(view: View, activity: Activity, callback: (Bitmap) -> Unit) {
        activity.window?.let { window ->
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val locationOfViewInWindow = IntArray(2)
            view.getLocationInWindow(locationOfViewInWindow)
            try {
                PixelCopy.request(
                    window,
                    Rect(
                        locationOfViewInWindow[0],
                        locationOfViewInWindow[1],
                        locationOfViewInWindow[0] + view.width,
                        locationOfViewInWindow[1] + view.height
                    ),
                    bitmap,
                    { copyResult ->
                        if (copyResult == PixelCopy.SUCCESS) {
                            callback(bitmap)
                        }
                        // possible to handle other result codes ...
                    },
                    Handler(Looper.getMainLooper())
                )
            } catch (e: IllegalArgumentException) {
                // PixelCopy may throw IllegalArgumentException, make sure to handle it
                e.printStackTrace()
            }
        }
    }

    private fun captureTracking(bitmap: Bitmap) {
        val fileName = "${System.currentTimeMillis()}.png"
        val resolver = requireContext().contentResolver
        val imageCollections =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val imageUri = resolver.insert(imageCollections, imageDetails)
        imageUri ?: return

        resolver.openOutputStream(imageUri).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.clear()
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(imageUri, imageDetails, null, null)
        }

        view?.let {
            Snackbar.make(it, "저장된 사진을 확인하시겠습니까?", Snackbar.LENGTH_SHORT).apply {

                setAction("확인하기", object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        val loadIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                            type = "image/*"
                        }
                        startActivity(Intent.createChooser(loadIntent, "공유하기"))
                    }

                })
                    .show()
            }

        }

    }

    private fun sendCommandToService(action: String) {
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    private fun showCancelTrackingDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("산책 기록을 취소하시겠습니까?")
            .setMessage("현재까지의 기록을 취소하고 모든 데이터를 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                stopTracking()
            }
            .setNegativeButton("아니오") { dialog, _ ->
                dialog.cancel()
            }
            .create()
            .show()
    }

    private fun showSaveTrackingDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("산책 기록을 저장하시겠습니까?")
            .setMessage("현재까지의 기록을 취소하고 모든 데이터를 삭제하시겠습니까?")
            .setPositiveButton("저장") { _, _ ->
                zoomToSeeWholeTrack()
                endTracking_saveToDb()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.cancel()
            }
            .create()
            .show()
    }

    private fun stopTracking() {
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_walkFragment)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}

