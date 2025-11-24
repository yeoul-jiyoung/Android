package com.example.myapplication.network

// 공통 응답 포맷
data class ApiResponse<T>(
    val status: Boolean,
    val code: String,
    val message: String,
    val data: T?
)
