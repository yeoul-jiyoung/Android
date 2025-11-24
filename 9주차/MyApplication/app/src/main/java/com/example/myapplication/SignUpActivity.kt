package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivitySignupBinding
import com.example.myapplication.network.AuthService
import com.example.myapplication.network.SignUpRequest
import com.example.myapplication.network.SignUpView

class SignUpActivity : AppCompatActivity(), SignUpView {

    private lateinit var binding: ActivitySignupBinding
    private val authService = AuthService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //"가입완료" 버튼 클릭 시 회원가입 요청
        binding.signUpSignUpBtn.setOnClickListener {
            signup()
        }
    }

    private fun signup() {
        // 닉네임 / 이메일 / 비밀번호 입력값 가져오기
        val nickname = binding.signUpNicknameEt.text.toString().trim()
        val idPart = binding.signUpIdEt.text.toString().trim()
        val domainPart = binding.signUpDirectInputEt.text.toString().trim()
        val password = binding.signUpPasswordEt.text.toString()
        val passwordCheck = binding.signUpPasswordCheckEt.text.toString()

        // 닉네임 공란 체크
        if (nickname.isEmpty()) {
            Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 이메일 형식 체크
        if (idPart.isEmpty() || domainPart.isEmpty()) {
            Toast.makeText(this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 비밀번호 공란 체크
        if (password.isEmpty() || passwordCheck.isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 비밀번호 일치 체크
        if (password != passwordCheck) {
            Toast.makeText(this, "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 최종 이메일 문자열
        val email = "$idPart@$domainPart"

        // API 명세: name(닉네임), email, password 3개 전부 전송
        val request = SignUpRequest(
            name = nickname,
            email = email,
            password = password
        )

        // 회원가입 API 호출
        authService.signUp(this, request)
    }

    // SignUpView 구현
    override fun onSignUpSuccess(memberId: Int) {
        Toast.makeText(
            this,
            "회원가입이 완료되었습니다. (memberId: $memberId)",
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    override fun onSignUpFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
