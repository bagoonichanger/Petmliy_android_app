package com.bagooni.petmliy_android_app.post

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

class Post(
    val content: String, val author: String
)

class OwnerProfile(
    val username: String, val image: String
)

interface RetrofitService {

    @Multipart
    @POST("instagram/post/")
    fun uploadPost(
        @Part image: MultipartBody.Part,
        @Part("content") content: RequestBody
    )

    @GET("instagram/post/list/all")
    fun getPosts(

    ): Call<ArrayList<Post>>

    @GET("posts/findAll")
    fun getPost(

    ): Call<ArrayList<Post>>


}