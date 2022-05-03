package com.bagooni.petmliy_android_app.post

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

class InstaPost(
    val id: Int, val content: String, val image: String, val owner_profile: OwnerProfile
)

class OwnerProfile(
    val username: String, val image: String?
)

class UserInfo(
    val id: Int, val username: String, val profile: OwnerProfile
)

data class PostContent(
    val postImg: String, val postContent: String
)

interface RetrofitService {

    //ex.업로드
    @Multipart
    @POST("instagram/post/")
    fun uploadPost(
        @HeaderMap headers: Map<String, String>,  //토큰 헤더값으로 전달
        //@Part image: MultipartBody.Part,
        @Part("content") content: RequestBody
    )

    //petmily 포스트 업로드
    @POST("api/post/save")
    fun postUpload(
        @Body data : PostContent
    ): Call<PostContent>

    //ex.좋아요
    @POST("instagram/post/like/{post_id}/")
    fun postLike(
        @Path("post_id") post_id: Int
    ): Call<Any>

    //ex.포스트 가져오기
    @GET("instagram/post/list/all")
    fun getInstaPosts(
    ): Call<ArrayList<InstaPost>>

    @GET("posts/findAll")
    fun getPost(
    ): Call<ArrayList<InstaPost>>
}