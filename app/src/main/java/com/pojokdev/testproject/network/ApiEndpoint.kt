package com.pojokdev.testproject.network

import com.pojokdev.testproject.data.ResponseLogin
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiEndpoint {

    @FormUrlEncoded
    @POST("ceklogin")
    fun cekNohp(

        @Field("nohp") nohp : String
    ):Call<ResponseLogin>
}
