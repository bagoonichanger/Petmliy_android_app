package com.bagooni.petmliy_android_app.walk.Db

class TrackingRepository(private val trackingDAO: TrackingDAO) {
    suspend fun insertTracking(tracking: Tracking) = trackingDAO.insertTracking(tracking)

    suspend fun deleteTracking(tracking: Tracking) = trackingDAO.deleteTracking(tracking)

    fun getAllTrackingSortedByCalendar(year:Int, month:Int, day:Int) = trackingDAO.getAllTrackingSortedByCalendar(year,month,day)

    fun getAllTracking() = trackingDAO.getAllTracking()

//    fun getAllTrackingSortedByTimeInMillis() = trackingDAO.getAllTrackingSortedByTimeInMillis()
//
//    fun getAllTrackingSortedByCaloriesBurned() = trackingDAO.getAllTrackingSortedByCaloriesBurned()
//
//    fun getAllTrackingSortedByAvgSpeed() = trackingDAO.getAllTrackingSortedByAvgSpeed()
//
//    fun getAllTrackingSortedByDistance() = trackingDAO.getAllTrackingSortedByDistance()
}
