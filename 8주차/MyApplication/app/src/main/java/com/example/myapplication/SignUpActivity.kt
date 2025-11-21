package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivitySignupBinding
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //"가입완료" 버튼 클릭 시 회원가입, finish() 를 통해 회원가입 액티비티 꺼짐
        binding.signUpSignUpBtn.setOnClickListener {
            signup()
        }
    }
    //이메일과 비밀번호 받아오기
    private fun getUser(): User {
        val email: String =
            binding.signUpIdEt.text.toString() + "@" + binding.signUpDirectInputEt.text.toString()
        val pwd: String = binding.signUpPasswordEt.text.toString()
        return User(email, pwd)
    }

    //이메일 or 비밀번호 공란 시 토스트 메시지
    private fun signup() {
        if (binding.signUpIdEt.text.toString()
                .isEmpty() || binding.signUpDirectInputEt.text.toString().isEmpty()
        ) {
            Toast.makeText(this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        //비밀번호 잘못 입력시 토스트 메시지
        if (binding.signUpPasswordEt.text.toString() != binding.signUpPasswordCheckEt.text.toString()) {
            Toast.makeText(this, "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        //정보를 DB에 저장
        val user = getUser()

        //DB 작업은 IO 스레드에서 실행
        lifecycleScope.launch(Dispatchers.IO){
            val db=SongDatabase.getInstance(this@SignUpActivity)!!

            db.userDao().insert(user)

            //log 확인용
            val users = db.userDao().getUsers()
            Log.d("SIGNUPACT",users.toString())

            //UI 관련 작업은 메인 스레드로 전환
            withContext(Dispatchers.Main){
                Toast.makeText(this@SignUpActivity,"회원가입이 완료되었습니다.",Toast.LENGTH_SHORT).show()
                //정상적으로 정보가 저장된 뒤에만 화면 종료
                finish()
            }
        }
    }
}
