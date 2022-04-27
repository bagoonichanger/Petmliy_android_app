package com.bagooni.petmliy_android_app.walk.Db


class TrackingRepository(private val trackingDAO: TrackingDAO) {
    suspend fun insertTracking(tracking: Tracking) = trackingDAO.insertTracking(tracking)

    suspend fun deleteTracking(tracking: Tracking) = trackingDAO.deleteTracking(tracking)

    fun getAllTrackingSortedByDate() = trackingDAO.getAllTrackingSortedByDate()

    fun getAllTrackingSortedByTimeInMillis() = trackingDAO.getAllTrackingSortedByTimeInMillis()

    fun getAllTrackingSortedByCaloriesBurned() = trackingDAO.getAllTrackingSortedByCaloriesBurned()

    fun getAllTrackingSortedByAvgSpeed() = trackingDAO.getAllTrackingSortedByAvgSpeed()

    fun getAllTrackingSortedByDistance() = trackingDAO.getAllTrackingSortedByDistance()

    fun getTotalTimeInMillis() = trackingDAO.getTotalTimeInMillis()

    fun getTotalCaloriesBurned() = trackingDAO.getTotalCaloriesBurned()

    fun getTotalDistance() = trackingDAO.getTotalDistance()

    fun getTotalAvgSpeed() = trackingDAO.getTotalAvgSpeed()
}
