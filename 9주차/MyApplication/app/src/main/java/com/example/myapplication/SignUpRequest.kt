package com.example.myapplication.network

// 회원가입 Request Body
data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String
)

// 회원가입 Response Body 중 data 부분
data class SignUpResponseData(
    val memberId: Int
)
