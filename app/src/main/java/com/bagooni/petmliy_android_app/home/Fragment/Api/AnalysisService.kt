package com.bagooni.petmliy_android_app.home.Fragment.Api

import android.net.Uri
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import java.net.URI

class AnalysisResult(
    val type: String, val crop_position: CropPosition, val breed: Breed, val emotion: Emotion
)

class CropPosition(
    val leftX: Int, val leftY: Int, val rightX: Int, val rightY: Int
)

class Emotion(
    val angry: Float, val sad: Float, val happy: Float
)

class Breed(
    val top1: String, val top1_result: Float
)

interface AnalysisService {
    @Multipart
    @POST("api/analysis/emotion")
    fun getEmotion(
        @Part img: MultipartBody.Part
    ): Call<AnalysisResult>
}