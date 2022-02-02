package com.telechaplaincy.video_call

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TokenApiInterface {
    @GET("/?")

    fun fetchAllData(
        @Query("uid") uid: String,
        @Query("channelName") channelName: String,
        @Query("userType") userType: String
    ): Call<TokenModelClass>
}