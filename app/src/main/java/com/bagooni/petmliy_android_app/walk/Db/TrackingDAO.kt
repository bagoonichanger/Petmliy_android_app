package com.bagooni.petmliy_android_app.walk.Db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TrackingDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Primary Key가 겹치는 것이 있으면 덮어 쓴다
    suspend fun insertTracking(tracking: Tracking)

    @Delete
    suspend fun deleteTracking(tracking: Tracking)

    @Query("SELECT * FROM tracking_table ORDER BY timestamp DESC")
    fun getAllTrackingSortedByDate(): LiveData<List<Tracking>>

    @Query("SELECT * FROM tracking_table ORDER BY timeInMillis DESC")
    fun getAllTrackingSortedByTimeInMillis(): LiveData<List<Tracking>>

    @Query("SELECT * FROM tracking_table ORDER BY caloriesBurned DESC")
    fun getAllTrackingSortedByCaloriesBurned(): LiveData<List<Tracking>>

    @Query("SELECT * FROM tracking_table ORDER BY avgSpeedInKMH DESC")
    fun getAllTrackingSortedByAvgSpeed(): LiveData<List<Tracking>>

    @Query("SELECT * FROM tracking_table ORDER BY distanceInMeters DESC")
    fun getAllTrackingSortedByDistance(): LiveData<List<Tracking>>

    @Query("SELECT SUM(timeInMillis) FROM tracking_table")
    fun getTotalTimeInMillis() : LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) FROM tracking_table")
    fun getTotalCaloriesBurned() : LiveData<Int>

    @Query("SELECT SUM(distanceInMeters) FROM tracking_table")
    fun getTotalDistance() : LiveData<Int>

    @Query("SELECT SUM(avgSpeedInKMH) FROM tracking_table")
    fun getTotalAvgSpeed() : LiveData<Float>
}