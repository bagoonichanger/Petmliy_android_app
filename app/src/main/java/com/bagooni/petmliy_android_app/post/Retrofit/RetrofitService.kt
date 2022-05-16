package com.bagooni.petmliy_android_app.post

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

class Post(
    val postId: Long, val userImg: String, val email: String, val postImg: String, val postContent: String
)

interface RetrofitService {
    //포스트 업로드
    @Multipart
    @POST("api/post/save")
    fun postUpload(
        @Header("email") email: String,
        @Part userImg: MultipartBody.Part,
        @Part postImg: MultipartBody.Part,
        @Part postContent: MultipartBody.Part
    ): Call<Post>

    //포스트 가져오기
    @GET("api/post/findAll")
    fun getPost(
    ): Call<ArrayList<Post>>

}