package com.telechaplaincy.video_call

import retrofit2.Call
import retrofit2.http.Path
import retrofit2.http.GET

interface TokenApiInterface {
    @GET("index/{uid}")

    fun fetchAllData(@Path("uid") uid: String): Call<TokenModelClass>
}