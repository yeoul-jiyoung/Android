package com.example.myapplication.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {

    // 회원가입
    @POST("/signup")
    fun signUp(
        @Body body: SignUpRequest
    ): Call<ApiResponse<SignUpResponseData>>

    // 로그인
    @POST("/login")
    fun login(
        @Body body: LoginRequest
    ): Call<ApiResponse<LoginResponseData>>

    // Access Token 테스트용 API
    @GET("/test")
    fun testToken(
        @Header("Authorization") authorization: String
    ): Call<ApiResponse<TestResponseData>>
}