package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityLoginBinding
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

    private fun login(){
        if(binding.loginIdEt.text.toString().isEmpty()||binding.loginDirectInputEt.text.toString().isEmpty()){
            Toast.makeText(this,"이메일을 입력해주세요.",Toast.LENGTH_SHORT).show()
            return
        }
        if(binding.loginPasswordEt.text.toString().isEmpty()){
            Toast.makeText(this,"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show()
            return
        }

        val email: String=binding.loginIdEt.text.toString()+"@"+binding.loginDirectInputEt.text.toString()
        val pwd: String=binding.loginPasswordEt.text.toString()

        val songDB=SongDatabase.getInstance(this)!!
        val user=songDB.userDao().getUser(email,pwd)

        user?.let{
            Log.d("LOGIN_ACT/GET_USER","userId: ${user.id}, $user")
            //유저의 아이디 값은 아래에서 만든 함수의 인자값
            saveJwt(user.id)

            //로그인 시 MainActivity로 이동
            startMainActivity()
        }
        Toast.makeText(this,"회원 정보가 존개하지 않습니다.", Toast.LENGTH_SHORT).show()
    }
    private fun saveJwt(jwt:Int){
        val spf=getSharedPreferences("auth", MODE_PRIVATE)
        val editor=spf.edit()

        //jwt를 키 값으로 저장
        editor.putInt("jwt",jwt)
        editor.apply()
    }

    //MainActivity로 이동하는 함수
    private fun startMainActivity(){
        val intent=Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}