package com.bagooni.petmliy_android_app.walk.Db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bagooni.petmliy_android_app.Constants.TRACKING_DATABASE_NAME
import kotlinx.parcelize.Parcelize


@Entity(tableName = TRACKING_DATABASE_NAME)
@Parcelize
data class Tracking(
    @PrimaryKey
    var id: Int = 0,
    val year: Int = 0,
    val month: Int = 0,
    val day: Int = 0,
    var img: String? = null, //var img: Bitmap? = null 수정필요
    var avgSpeedInKMH: Float = 0f,
    var distanceInMeters: Int = 0,
    var timeInMillis: Long = 0L,
    var caloriesBurned: Int = 0
) : Parcelable