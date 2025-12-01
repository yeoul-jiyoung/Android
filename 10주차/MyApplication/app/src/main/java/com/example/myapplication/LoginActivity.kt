package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.network.AuthService
import com.example.myapplication.network.LoginRequest
import com.example.myapplication.network.LoginResponseData
import com.example.myapplication.network.LoginView
import com.example.myapplication.network.TestView
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

// 이메일 로그인 + Test API + 카카오 로그인 모두 처리
class LoginActivity : AppCompatActivity(), LoginView, TestView {

    private lateinit var binding: ActivityLoginBinding
    private val authService = AuthService()

    companion object {
        private const val TAG = "KAKAO_LOGIN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 회원가입 텍스트 클릭 시 회원가입 화면으로 이동
        binding.loginSignUpTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // 이메일/비밀번호 로그인 버튼
        binding.loginSignInBtn.setOnClickListener {
            login()
        }

        // 카카오 로그인 아이콘 클릭
        binding.loginKakakoLoginIv.setOnClickListener {
            kakaoLogin()
        }
    }

    // 이메일/비밀번호 로그인
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

    // 이메일 로그인 성공 시
    override fun onLoginSuccess(data: LoginResponseData) {
        Toast.makeText(this, "${data.name}님 환영합니다!", Toast.LENGTH_SHORT).show()

        // Access Token 및 유저 정보 저장
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        spf.edit()
            .putString("access_token", data.accessToken)
            .putInt("member_id", data.memberId)
            .putString("name", data.name)
            .putInt("jwt", data.memberId)
            .putString("login_type", "email")
            .apply()

        // 로그인 성공 후, Test API 호출 (Access Token 사용)
        authService.testAccessToken(this, data.accessToken)
    }

    override fun onLoginFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Test API 결과 처리
    override fun onTestSuccess(message: String) {
        Toast.makeText(this, "Test API 성공: $message", Toast.LENGTH_SHORT).show()
        startMainActivity()
        finish()    // 로그인 화면으로 뒤로가기 막기
    }

    override fun onTestFailure(message: String) {
        Toast.makeText(this, "Test API 실패: $message", Toast.LENGTH_SHORT).show()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    // 카카오 로그인
    private fun kakaoLogin() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오 로그인 실패", error)
                Toast.makeText(this, "카카오 로그인 실패: ${error.message}", Toast.LENGTH_SHORT).show()
            } else if (token != null) {
                Log.i(TAG, "카카오 로그인 성공, accessToken = ${token.accessToken}")
                Toast.makeText(this, "카카오 로그인 성공!", Toast.LENGTH_SHORT).show()
                // 로그인 성공 후 사용자 정보 요청 + 우리 앱 로그인 처리
                loadKakaoUserInfo()
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }

    // 카카오 사용자 정보(프로필/닉네임/메일) 요청 + SharedPreferences 저장
    private fun loadKakaoUserInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
                Toast.makeText(this, "사용자 정보 요청 실패", Toast.LENGTH_SHORT).show()
                return@me
            }

            if (user != null) {
                val rawNickname = user.kakaoAccount?.profile?.nickname
                val nickname = rawNickname ?: "(닉네임 없음)"
                val email = user.kakaoAccount?.email ?: "(이메일 없음)"
                val profileUrl = user.kakaoAccount?.profile?.thumbnailImageUrl
                val kakaoId = user.id ?: -1L

                Log.i(
                    TAG, "사용자 정보 요청 성공\n" +
                            "id = $kakaoId\n" +
                            "nickname = $nickname\n" +
                            "email = $email\n" +
                            "profile = $profileUrl"
                )



                // 로그인된 상태로 인식하도록 SharedPreferences 설정
                val pseudoJwt = (kakaoId % Int.MAX_VALUE).toInt()  // Long → Int 변환

                val spf = getSharedPreferences("auth", MODE_PRIVATE)
                spf.edit()
                    .putBoolean("is_kakao_login", true)
                    .putLong("kakao_id", kakaoId)
                    .putString("kakao_nickname", nickname)
                    .putString("kakao_email", email)
                    .putString("name", nickname)
                    .putInt("jwt", pseudoJwt)
                    .putString("login_type", "kakao")
                    .apply()

                // 이메일 로그인과 동일하게 메인 화면으로 이동
                startMainActivity()
                finish()
            }
        }
    }

    // 카카오 로그아웃
    private fun kakaoLogout() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                Toast.makeText(this, "로그아웃 실패: ${error.message}", Toast.LENGTH_SHORT).show()
            } else {
                Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()

                // SharedPreferences 정리 (선택)
                val spf = getSharedPreferences("auth", MODE_PRIVATE)
                spf.edit()
                    .remove("is_kakao_login")
                    .remove("kakao_id")
                    .remove("kakao_nickname")
                    .remove("kakao_email")
                    .remove("jwt")
                    .apply()
            }
        }
    }
}