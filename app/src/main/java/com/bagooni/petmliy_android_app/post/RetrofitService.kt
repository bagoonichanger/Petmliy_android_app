package com.bagooni.petmliy_android_app.post

import retrofit2.Call
import retrofit2.http.GET

class Post(
    val content: String, val image: String, val owner_profile:OwnerProfile
)

class OwnerProfile(
    val username: String, val image: String
)

interface RetrofitService {
    @GET("instagram/post/list/all")
    fun getPosts(

    ): Call<ArrayList<Post>>

}