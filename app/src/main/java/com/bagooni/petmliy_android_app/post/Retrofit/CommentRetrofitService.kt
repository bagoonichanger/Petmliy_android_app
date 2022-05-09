package com.bagooni.petmliy_android_app.post.Retrofit

import com.bagooni.petmliy_android_app.post.RetrofitService
import retrofit2.Call
import retrofit2.http.GET


class Comment(
    val postId: Int, val username: String, val userImg: String, val comment: String
)

interface CommentRetrofitService {
    @GET("")
    fun getComment(
    ): Call<ArrayList<Comment>>
}