package com.bagooni.petmliy_android_app.walk.Fragment.Dto

import com.google.gson.annotations.SerializedName

data class sendTrackingDto(
    @SerializedName("id") var id: Int?,
    @SerializedName("year") var year: String?,
    @SerializedName("month") var month: String?,
    @SerializedName("day") var day: String?,
    @SerializedName("timeInMillis") var timeInMillis: String?,
    @SerializedName("distanceInMeters") var distanceInMeters: String?,
    @SerializedName("avgSpeedInKMH") var avgSpeed: String?,
    @SerializedName("caloriesBurned") var caloriesBurned: String?,
    @SerializedName("img") var img: String?
)

