package com.bagooni.petmliy_android_app.map.model.Dto

import com.google.gson.annotations.SerializedName

data class LikePlaceDto(
    @SerializedName("id") var id: Int?,
    @SerializedName("name") var name: String?,
    @SerializedName("phone") var phone: String?,
    @SerializedName("address") var address: String?,
    @SerializedName("url") var url: String?,
    @SerializedName("categories") var categories: String?
)