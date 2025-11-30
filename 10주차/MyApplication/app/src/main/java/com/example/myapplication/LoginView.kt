package com.example.myapplication.network

interface LoginView {
    fun onLoginSuccess(data: LoginResponseData)
    fun onLoginFailure(message: String)
}
