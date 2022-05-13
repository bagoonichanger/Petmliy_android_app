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
import com.bagooni.petmliy_android_app.Constants.ACTION_PAUSE_SERVICE
import com.bagooni.petmliy_android_app.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.bagooni.petmliy_android_app.Constants.ACTION_START_OR_RESUME_SERVICE
import com.bagooni.petmliy_android_app.Constants.ACTION_STOP_SERVICE
import com.bagooni.petmliy_android_app.Constants.FASTEST_LOCATION_INTERVAL
import com.bagooni.petmliy_android_app.Constants.LOCATION_UPDATE_INTERVAL
import com.bagooni.petmliy_android_app.Constants.NOTIFICATION_CHANNEL_ID
import com.bagooni.petmliy_android_app.Constants.NOTIFICATION_CHANNEL_NAME
import com.bagooni.petmliy_android_app.Constants.NOTIFICATION_ID
import com.bagooni.petmliy_android_app.Constants.TIMER_UPDATE_INTERVAL
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingService : LifecycleService() {
    companion object {
        val timeRunInMillis = MutableLiveData<Long>()// 뷰에 표시될 시간
        val timeRunInSeconds = MutableLiveData<Long>()  // 알림창에 표시될 시간
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
    }

    var isFirstRun = true
    var serviceKilled = false

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    lateinit var basenotificationBuilder: NotificationCompat.Builder
    lateinit var curNotificationBuilder: NotificationCompat.Builder

    private var isTimerEnabled = false // 타이머 실행 여부
    private var lapTime = 0L // 시작 후 측정한 시간
    private var totalTime = 0L // 정지 시 저장되는 시간
    private var timeStarted = 0L // 측정 시작된 시간
    private var lastSecondTimestamp = 0L // 1초 단위 체크를 위함

    private fun startTimer() {
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                lapTime = System.currentTimeMillis() - timeStarted
                timeRunInMillis.postValue(totalTime + lapTime)
                if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            totalTime += lapTime
        }
    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInMillis.postValue(0L)
        timeRunInSeconds.postValue(0L)
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf())

    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(it.latitude, it.longitude)
            pathPoints.value?.apply {
                if (lastOrNull() == null) {
                    add(mutableListOf(pos))
                } else {
                    last().add(pos)
                }
                pathPoints.postValue(this)
            }
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            for (location in locationResult.locations) {
                addPathPoint(location)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        initNotificationBuilder()
        postInitialValues()
        initLocationRequest()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        isTracking.observe(this, Observer {
            startLocationUpdates(it)
            updateNotificationTrackingState(it)
        })
        initLocationClient()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        Log.d("service", "Started service")
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        startTimer()
                        Log.d("service", "resumed service")
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                    Log.d("service", "Pause service")
                }
                ACTION_STOP_SERVICE -> {
                    Log.d("service", "Stop service")
                    killService()
                }

                else -> {}
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun pauseService() {
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    private fun killService() {
        serviceKilled = true
        isFirstRun = true
        pauseService()
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }

    private fun initNotificationBuilder() {
        basenotificationBuilder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_premium_icon_paw)
                .setContentTitle("Petmily")
                .setContentText("00:00")
                .setContentIntent(getTrackingActivityPendingIntent())

        curNotificationBuilder = basenotificationBuilder
    }

    private fun initLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = LOCATION_UPDATE_INTERVAL
            fastestInterval = FASTEST_LOCATION_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun initLocationClient() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { locationSettingsResponse ->
            Log.d("FusedLocationManager", "location client setting success")
        }

        task.addOnFailureListener { exception ->
            Log.d("FusedLocationManager", "location client setting failure")
        }
    }

    private fun startLocationUpdates(isTracking: Boolean) {
        Log.d("tracking", isTracking.toString())
        if (isTracking) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            stopLocationUpdates()
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun startForegroundService() {
        startTimer()
        isTracking.postValue(true)
//        startLocationUpdates()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        startForeground(NOTIFICATION_ID, basenotificationBuilder.build())

        timeRunInSeconds.observe(this, Observer {
            if (!serviceKilled) {
                val notification = curNotificationBuilder
                    .setContentText(TrackingUtility.getFormattedStopWatchTime(it * 1000L))
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }
        })
    }

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText = if (isTracking) "Pause" else "Resume"

        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(curNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }

        if (!serviceKilled) {
            curNotificationBuilder = basenotificationBuilder
                .addAction(R.drawable.ic_baseline_pause_24, notificationActionText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, curNotificationBuilder.build())
        }
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