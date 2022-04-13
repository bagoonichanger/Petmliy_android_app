package com.bagooni.petmliy_android_app.walk.Activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.PixelCopy
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.bagooni.petmliy_android_app.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar


class TrackingActivity : AppCompatActivity(), OnMapReadyCallback {
    private var view: View? = null
    private lateinit var mMap: GoogleMap

    private val walkTimeTextView: CountUpView by lazy {
        findViewById(R.id.walkTime)
    }
    private val startButton: AppCompatImageButton by lazy {
        findViewById(R.id.startButton)
    }
    private val pauseButton: AppCompatImageButton by lazy {
        findViewById(R.id.pauseButton)
    }
    private val stopButton: AppCompatImageButton by lazy {
        findViewById(R.id.stopButton)
    }
    private val disabledStopButton: AppCompatImageButton by lazy {
        findViewById(R.id.disabledStopButton)
    }
    private val captureButton: AppCompatImageButton by lazy {
        findViewById(R.id.captureButton)
    }
    private val disabledCaptureButton: AppCompatImageButton by lazy {
        findViewById(R.id.disabledCaptureButton)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)

        view = findViewById(R.id.trackingContainer)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.googleMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initViews()
        initButtons()
    }

    private fun initViews() {
        startButton.visibility = View.VISIBLE
        pauseButton.visibility = View.GONE
        walkTimeTextView.clearCountTime()
    }

    private fun initButtons() {
        startButton.setOnClickListener {
            startButton.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
            disabledStopButton.visibility = View.VISIBLE
            disabledCaptureButton.visibility = View.VISIBLE
            walkTimeTextView.startCountUp()
        }

        pauseButton.setOnClickListener {
            startButton.visibility = View.VISIBLE
            pauseButton.visibility = View.GONE
            disabledStopButton.visibility = View.GONE
            disabledCaptureButton.visibility = View.GONE
            walkTimeTextView.pauseCountUp()
        }

        stopButton.setOnClickListener {
            Log.d("cd", "b")
            walkTimeTextView.text = "00:00"
            walkTimeTextView.stopCountUp()
        }

        disabledStopButton.setOnClickListener {
            Toast.makeText(this, "산책을 멈추고 중지 해주세요", Toast.LENGTH_SHORT).show()
        }

        captureButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                showCaptureTrackingDialog()
            } else {
                requestWriteStoragePermission()
            }
        }
        disabledCaptureButton.setOnClickListener{
            Toast.makeText(this, "산책을 멈추고 스크린샷 해주세요", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCaptureTrackingDialog() {
        AlertDialog.Builder(this)
            .setMessage("산책 기록을 저장하시겠습니까?")
            .setPositiveButton("저장") { dialog, _ ->
                view?.let {
                    getBitmapFromView(it, this) { bitmap ->
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

    private fun requestWriteStoragePermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val writeExternalStoragePermissionGranted =
            requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED

        if (writeExternalStoragePermissionGranted) {
            showCaptureTrackingDialog()
        }
    }

    private fun captureTracking(bitmap: Bitmap) {
        val fileName = "${System.currentTimeMillis()}.png"
        val resolver = applicationContext.contentResolver
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
                        val load_intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                            type = "image/*"
                        }
                        startActivity(Intent.createChooser(load_intent, "공유하기"))
                    }

                })
                    .show()
            }

        }

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


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    companion object {
        private const val REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 101
    }
}