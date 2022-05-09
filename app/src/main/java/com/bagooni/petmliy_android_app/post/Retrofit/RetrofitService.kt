package com.bagooni.petmliy_android_app.post

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


class Post(
    val postId: Int, val email: String, val postImg: String, val postContent: String
)

data class PostContent(
    val postImg: String, val postContent: String
)


interface RetrofitService {

    //petmily 포스트 업로드
    @Multipart
    @POST("api/post/save")
    fun postUpload(
        @Header("email") email: String,
        @Part postImg: MultipartBody.Part,
        @Part postContent: MultipartBody.Part
    ): Call<Any>

    //ex.좋아요
    @POST("instagram/post/like/{post_id}/")
    fun postLike(
        @Path("post_id") post_id: Int
    ): Call<Any>

    //petmily 포스트 가져오기
    @GET("api/post/findAll")
    fun getPost(
    ): Call<ArrayList<Post>>
}