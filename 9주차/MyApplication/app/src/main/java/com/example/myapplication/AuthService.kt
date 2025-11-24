package com.example.myapplication.network

import android.util.Log
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthService {

    // 회원가입
    fun signUp(signUpView: SignUpView, request: SignUpRequest) {
        val call = NetworkManager.authApi.signUp(request)

        call.enqueue(object : Callback<ApiResponse<SignUpResponseData>> {
            override fun onResponse(
                call: Call<ApiResponse<SignUpResponseData>>,
                response: Response<ApiResponse<SignUpResponseData>>
            ) {
                // 1) HTTP 200~299
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("AuthService", "signUp response body = $body")

                    // 서버 응답 JSON 안의 status 값 확인
                    if (body?.status == true) {
                        val memberId = body.data?.memberId ?: -1
                        signUpView.onSignUpSuccess(memberId)
                    } else {
                        // status=false 인 경우 서버가 내려준 message 사용
                        val msg = body?.message ?: "회원가입에 실패했습니다.(status=false)"
                        signUpView.onSignUpFailure(msg)
                    }
                }
                // 2) HTTP 4xx / 5xx 에러
                else {
                    val code = response.code()
                    val errorBodyStr = response.errorBody()?.string()

                    Log.e("AuthService", "signUp error code=$code, body=$errorBodyStr")

                    val serverMsg: String? = try {
                        if (!errorBodyStr.isNullOrEmpty()) {
                            val errorRes =
                                Gson().fromJson(errorBodyStr, ApiResponse::class.java) as ApiResponse<*>
                            errorRes.message
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("AuthService", "signUp error parse failed: ${e.message}", e)
                        null
                    }

                    val userMsg = serverMsg ?: "회원가입에 실패했습니다. (HTTP $code)"
                    signUpView.onSignUpFailure(userMsg)
                }
            }

            override fun onFailure(
                call: Call<ApiResponse<SignUpResponseData>>,
                t: Throwable
            ) {
                Log.e("AuthService", "signUp onFailure", t)
                signUpView.onSignUpFailure("네트워크 오류가 발생했습니다.")
            }
        })
    }

    // 로그인
    fun login(loginView: LoginView, request: LoginRequest) {
        val call = NetworkManager.authApi.login(request)

        call.enqueue(object : Callback<ApiResponse<LoginResponseData>> {
            override fun onResponse(
                call: Call<ApiResponse<LoginResponseData>>,
                response: Response<ApiResponse<LoginResponseData>>
            ) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val body = response.body()!!
                    val data = body.data!!
                    loginView.onLoginSuccess(data)
                } else {
                    val message = response.body()?.message ?: "로그인에 실패했습니다."
                    loginView.onLoginFailure(message)
                }
            }

            override fun onFailure(
                call: Call<ApiResponse<LoginResponseData>>,
                t: Throwable
            ) {
                Log.e("AuthService", "login onFailure: ${t.message}", t)
                loginView.onLoginFailure("네트워크 오류가 발생했습니다.")
            }
        })
    }

    // Access Token Test API
    fun testAccessToken(testView: TestView, accessToken: String) {

        val headerValue = "Bearer $accessToken"

        val call = NetworkManager.authApi.testToken(headerValue)

        call.enqueue(object : Callback<ApiResponse<TestResponseData>> {
            override fun onResponse(
                call: Call<ApiResponse<TestResponseData>>,
                response: Response<ApiResponse<TestResponseData>>
            ) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val body = response.body()!!
                    val msg = body.data?.message ?: body.message
                    testView.onTestSuccess(msg)
                } else {
                    val msg = response.body()?.message ?: "Test API 호출에 실패했습니다."
                    testView.onTestFailure(msg)
                }
            }

            override fun onFailure(
                call: Call<ApiResponse<TestResponseData>>,
                t: Throwable
            ) {
                Log.e("AuthService", "testAccessToken onFailure: ${t.message}", t)
                testView.onTestFailure("네트워크 오류가 발생했습니다.")
            }
        })
    }
}
