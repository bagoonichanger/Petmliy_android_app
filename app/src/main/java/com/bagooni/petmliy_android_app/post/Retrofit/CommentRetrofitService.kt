package com.bagooni.petmliy_android_app.post.Retrofit

import com.bagooni.petmliy_android_app.post.Comment.CommentFragmentArgs
import com.bagooni.petmliy_android_app.post.RetrofitService
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.*


class Comment(
    val postId: Long, val email: String, val commentContent: String
)

class postComment(
    @SerializedName("postId") val postId: Long,
    @SerializedName("commentContent") val commentContent: String
)

interface CommentRetrofitService {
    @GET("api/comment/findByPostId/{postId}")
    fun getComment(
        @Path("postId") postId: Long
    ): Call<ArrayList<Comment>>

    @POST("api/comment/save")
    fun postComment(
        @Header("email") email: String,
        @Body postComment: postComment
    ): Call<ArrayList<postComment>>
}