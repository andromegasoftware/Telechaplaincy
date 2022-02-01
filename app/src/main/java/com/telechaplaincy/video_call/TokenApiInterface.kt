package com.telechaplaincy.video_call

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface TokenApiInterface {
    @GET("index/{uid}/{channelName}/{userType}")

    fun fetchAllData(
        @Path("uid") uid: String,
        @Path("channelName") channelName: String,
        @Path("userType") userType: String
    ): Call<TokenModelClass>
}