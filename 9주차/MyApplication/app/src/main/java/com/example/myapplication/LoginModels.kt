package com.example.myapplication.network

// 로그인 Request Body
data class LoginRequest(
    val email: String,
    val password: String
)

// 로그인 Response Body 중 data 부분
data class LoginResponseData(
    val name: String,
    val memberId: Int,
    val accessToken: String
)
