package com.example.lab10_login

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface StudentAPI {

    @FormUrlEncoded
    @POST("login")
    fun loginStudent(
        @Field("std_id") std_id: String,
        @Field("std_password") std_password: String
    ): Call<LoginClass> // ✅ ตรวจสอบว่า LoginClass ถูกต้อง

    @GET("search/{std_id}")
    fun searchStudent(
        @Path("std_id") std_id: String
    ): Call<ProfileClass> // ✅ ตรวจสอบว่า ProfileClass ถูกต้อง



    @FormUrlEncoded
    @POST("insertAccount")
    fun registerStudent(
        @Field("std_id") std_id: String,
        @Field("std_name") std_name: String,
        @Field("std_password") std_password: String,
        @Field("std_gender") std_gender: String
    ): Call<LoginClass>

    companion object {
        private const val BASE_URL = "http://10.0.2.2:3000/" // ✅ ใช้กับ Emulator

        fun create(): StudentAPI {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(StudentAPI::class.java)
        }
    }
}
