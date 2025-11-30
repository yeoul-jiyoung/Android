package com.example.myapplication.network

interface TestView {
    fun onTestSuccess(message: String)
    fun onTestFailure(message: String)
}
