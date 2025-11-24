package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.network.AuthService
import com.example.myapplication.network.LoginRequest
import com.example.myapplication.network.LoginResponseData
import com.example.myapplication.network.LoginView
import com.example.myapplication.network.TestView   // ★ TestView 추가

// LoginView + TestView 둘 다 구현
class LoginActivity : AppCompatActivity(), LoginView, TestView {

    private lateinit var binding: ActivityLoginBinding
    private val authService = AuthService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // "회원가입" 텍스트 클릭 시 회원가입 화면으로 이동
        binding.loginSignUpTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // 로그인 버튼 클릭 시 로그인 요청
        binding.loginSignInBtn.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val idPart = binding.loginIdEt.text.toString().trim()
        val domainPart = binding.loginDirectInputEt.text.toString().trim()
        val password = binding.loginPasswordEt.text.toString()

        if (idPart.isEmpty() || domainPart.isEmpty()) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val email = "$idPart@$domainPart"

        val request = LoginRequest(
            email = email,
            password = password
        )

        // 로그인 API 호출
        authService.login(this, request)
    }

    // LoginView 구현
    override fun onLoginSuccess(data: LoginResponseData) {
        Toast.makeText(this, "${data.name}님 환영합니다!", Toast.LENGTH_SHORT).show()

        // Access Token 및 유저 정보 저장
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        spf.edit()
            .putString("access_token", data.accessToken)
            .putInt("member_id", data.memberId)
            .putString("name", data.name)
            .putInt("jwt", data.memberId)
            .apply()

        // 로그인 성공 후, 바로 Test API 호출 (Access Token 사용)
        authService.testAccessToken(this, data.accessToken)
    }

    override fun onLoginFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // TestView 구현 (Access Token Test API 결과 처리)
    override fun onTestSuccess(message: String) {
        // Test API 성공 시
        Toast.makeText(this, "Test API 성공: $message", Toast.LENGTH_SHORT).show()
        startMainActivity()
        finish()    // 로그인 화면으로 뒤로가기 막기
    }

    override fun onTestFailure(message: String) {
        // Test API 실패 시
        Toast.makeText(this, "Test API 실패: $message", Toast.LENGTH_SHORT).show()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
