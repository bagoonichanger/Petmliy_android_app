package com.bagooni.petmliy_android_app.walk.Fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.PixelCopy
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.walk.Fragment.Constants.ACTION_START_OR_RESUME_SERVICE
import com.bagooni.petmliy_android_app.walk.Fragment.Service.TrackingService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

class TrackingFragment : Fragment(R.layout.fragment_tracking), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        mapView = view.findViewById(R.id.googleMapView)
        mapView.onCreate(savedInstanceState)

        initViews(view)
        initButtons(view)

        mapView.getMapAsync(this)


    }

    private fun initViews(view: View) {
        view.findViewById<AppCompatImageButton>(R.id.startButton).visibility = View.VISIBLE
        view.findViewById<AppCompatImageButton>(R.id.pauseButton).visibility = View.GONE
    }

    private fun initButtons(view: View) {
        view.findViewById<AppCompatImageButton>(R.id.startButton).setOnClickListener {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
            view.findViewById<AppCompatImageButton>(R.id.startButton).visibility = View.GONE
            view.findViewById<AppCompatImageButton>(R.id.pauseButton).visibility = View.VISIBLE
            view.findViewById<AppCompatImageButton>(R.id.disabledStopButton).visibility =
                View.VISIBLE
            view.findViewById<AppCompatImageButton>(R.id.disabledCaptureButton).visibility =
                View.VISIBLE
        }

        view.findViewById<AppCompatImageButton>(R.id.pauseButton).setOnClickListener {
            view.findViewById<AppCompatImageButton>(R.id.startButton).visibility = View.VISIBLE
            view.findViewById<AppCompatImageButton>(R.id.pauseButton).visibility = View.GONE
            view.findViewById<AppCompatImageButton>(R.id.disabledStopButton).visibility = View.GONE
            view.findViewById<AppCompatImageButton>(R.id.disabledCaptureButton).visibility =
                View.GONE
        }

        view.findViewById<AppCompatImageButton>(R.id.stopButton).setOnClickListener {

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
    }

    private fun showCaptureTrackingDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("산책 기록을 저장하시겠습니까?")
            .setPositiveButton("저장") { dialog, _ ->
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
//                setAction("공유", object : View.OnClickListener {
//                    override fun onClick(v: View?) {
//                        val sharing_intent = Intent(Intent.ACTION_SEND).apply {
//                            type = "image/png"
//                            putExtra(Intent.EXTRA_STREAM, imageUri)
//                        }
//                        startActivity(Intent.createChooser(sharing_intent, "공유하기"))
//                    }
//
//                })
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

    override fun onMapReady(googleMap: GoogleMap) {
        getPermissions()
        googleMap.cameraPosition
        googleMap.setMinZoomPreference(16.0f)
        googleMap.setMaxZoomPreference(18.0f)
        val home = LatLng(40.58674223379605, -73.82349947514406)
        googleMap.addMarker(
            MarkerOptions()
                .position(home)
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(home))
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
                    Manifest.permission.ACCESS_COARSE_LOCATION
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

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_AND_WRITE_EXTERNAL_STORAGE_PERMISSION =
            100
    }
}

