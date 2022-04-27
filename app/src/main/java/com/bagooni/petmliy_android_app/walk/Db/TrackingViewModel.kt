package com.bagooni.petmliy_android_app.walk.Db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TrackingViewModel(application: Application) : AndroidViewModel(application) {
    class Factory(val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TrackingViewModel(application) as T
        }

    }
    private val trackingRepository : TrackingRepository

    init {
        val trackingDAO = TrackingDatabase.getDatabase(application)!!.getTrackingDao()
        trackingRepository = TrackingRepository(trackingDAO)
    }

    fun insertRun(tracking: Tracking) = viewModelScope.launch(Dispatchers.IO) {
        trackingRepository.insertTracking(tracking)
    }
}