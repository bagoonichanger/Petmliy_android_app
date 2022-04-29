package com.bagooni.petmliy_android_app.walk.Db

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "tracking_table")
@Parcelize
data class Tracking(
    val year: Int = 0,
    val month: Int = 0,
    val day: Int = 0,
    var img: Bitmap? = null,
    var avgSpeedInKMH: Float = 0f,
    var distanceInMeters: Int = 0,
    var timeInMillis: Long = 0L,
    var caloriesBurned: Int = 0
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}