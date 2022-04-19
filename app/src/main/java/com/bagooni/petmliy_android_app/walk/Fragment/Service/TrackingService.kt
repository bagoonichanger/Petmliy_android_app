package com.bagooni.petmliy_android_app.walk.Fragment.Service

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bagooni.petmliy_android_app.MainActivity
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.walk.Fragment.Constants.ACTION_PAUSE_SERVICE
import com.bagooni.petmliy_android_app.walk.Fragment.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.bagooni.petmliy_android_app.walk.Fragment.Constants.ACTION_START_OR_RESUME_SERVICE
import com.bagooni.petmliy_android_app.walk.Fragment.Constants.ACTION_STOP_SERVICE
import com.bagooni.petmliy_android_app.walk.Fragment.Constants.FASTEST_LOCATION_INTERVAL
import com.bagooni.petmliy_android_app.walk.Fragment.Constants.LOCATION_UPDATE_INTERVAL
import com.bagooni.petmliy_android_app.walk.Fragment.Constants.NOTIFICATION_CHANNEL_ID
import com.bagooni.petmliy_android_app.walk.Fragment.Constants.NOTIFICATION_CHANNEL_NAME
import com.bagooni.petmliy_android_app.walk.Fragment.Constants.NOTIFICATION_ID
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>
//별칭

class TrackingService : LifecycleService() {
    var isFirstRun = true
    var isTracking = false
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            Log.d("1", "before")
            super.onLocationResult(result)
            if (isTracking) {
                Log.d("1", "check")
                for (location in result.locations) {
                    addPathPoint(location)
                    Log.d("Location", "Location : ${location.latitude}, ${location.longitude}")
                }
            }
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        val pathPoints = MutableLiveData<Polylines>()
    }

    private fun postInitValues() {
        isTracking = false
        pathPoints.postValue(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        postInitValues()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            val request = LocationRequest.create().apply {
                interval = LOCATION_UPDATE_INTERVAL
                fastestInterval = FASTEST_LOCATION_INTERVAL
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
                Log.d("1", "update")
                fusedLocationClient.requestLocationUpdates(
                    request, locationCallback, Looper.getMainLooper()
                )
        } else {
            Log.d("1", "dead2")
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        isTracking = true
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Log.d("1", "Resuming service...")
                    }
                    Log.d("1", "Started or resumed service")
                }
                ACTION_PAUSE_SERVICE -> {
                    Log.d("1", "Pause service")
                }
                ACTION_STOP_SERVICE -> {
                    Log.d("1", "Stop service")
                }

                else -> {}
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        addEmptyPolyline()
        updateLocationTracking(isTracking)
        Log.d("1", "$isTracking")

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager)

        val notificationBuilder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_premium_icon_paw)
                .setContentTitle("Petmily")
                .setContentText("00:00:00")
                .setContentIntent(getTrackingActivityPendingIntent())


        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getTrackingActivityPendingIntent() =
        PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        }, FLAG_UPDATE_CURRENT)

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}