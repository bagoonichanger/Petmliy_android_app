package com.bagooni.petmliy_android_app.post.Retrofit

import retrofit2.Call
import retrofit2.http.*

class Like(
    val postId: Long, val userImg: String
)

interface LikeRetrofitService {
    @POST("api/like/save")
    fun postLike(
        @Header("email") email: String,
        @Query("postId") postId: Long,
        @Query("userImg") userImg: String
    ): Call<Like>

    @DELETE("api/like/delete/{postId}")
    fun deleteLike(
        @Header("email") email: String,
        @Path("postId") postId: Long
    ): Call<Void>

    @GET("api/like/aboutMyLike/{postId}")
    fun aboutLike(
        @Header("email") email: String,
        @Path("postId") postId: Long
    ): Call<Int>

}