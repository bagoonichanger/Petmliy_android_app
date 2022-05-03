package com.bagooni.petmliy_android_app.home.Retrofit

import com.bagooni.petmliy_android_app.post.UserInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap

class OwnerProfile(
    val username: String, val image: String?
)

class UserInfo(
    val id: Int, val username: String, val profile: OwnerProfile
)

interface MypageRetrofitService {
    @GET("user/userInfo/")
    fun getUserInfo(
        @HeaderMap headers: Map<String, String>
    ): Call<UserInfo>
}