package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity(){
    lateinit var binding:ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginSignUpTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        //로그인 버튼을 눌렀을 때 login() 함수 실행
        binding.loginSignInBtn.setOnClickListener {
            login()
        }
    }

    private fun login() {
        if (binding.loginIdEt.text.toString()
                .isEmpty() || binding.loginDirectInputEt.text.toString().isEmpty()
        ) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.loginPasswordEt.text.toString().isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val email: String =
            binding.loginIdEt.text.toString() + "@" + binding.loginDirectInputEt.text.toString()
        val pwd: String = binding.loginPasswordEt.text.toString()

        //DB 작업은 IO 스러드에서 실행
        lifecycleScope.launch(Dispatchers.IO) {
            val songDB = SongDatabase.getInstance(this@LoginActivity)!!
            val user = songDB.userDao().getUser(email, pwd)

            //UI 변경은 메인 스레드 에서 실행
            withContext(Dispatchers.Main) {
                if (user != null) { //user 정보가 null 이 아니면 (정보가 DB에 존재하면)
                    Log.d("LOGIN_ACT/GET_USER", "userId: ${user.id}, $user")
                    Toast.makeText(this@LoginActivity,"로그인에 성공하였습니다.",Toast.LENGTH_SHORT).show()
                    saveJwt(user.id)
                    startMainActivity()
                    finish() // 로그인 화면으로 뒤로가기 막기
                } else { //user 정보가 null 이면 (정보가 DB에 없으면)
                    Toast.makeText(
                        this@LoginActivity,
                        "회원 정보가 존재하지 않습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
                private fun saveJwt(jwt: Int) {
                    val spf = getSharedPreferences("auth", MODE_PRIVATE)
                    val editor = spf.edit()

                    //jwt를 키 값으로 저장
                    editor.putInt("jwt", jwt)
                    editor.apply()
                }

                //MainActivity로 이동하는 함수
                private fun startMainActivity() {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }