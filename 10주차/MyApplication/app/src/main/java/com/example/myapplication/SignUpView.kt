package com.example.myapplication.network

interface SignUpView {
    fun onSignUpSuccess(memberId: Int)
    fun onSignUpFailure(message: String)
}
