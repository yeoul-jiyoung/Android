package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentLockerBinding
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {
    private var _binding: FragmentLockerBinding?=null
    private var SavedSongDatas=ArrayList<SavedSong>()
    private val binding get()=_binding!!
    private val information = arrayListOf("저장한곡", "음악파일", "저장앨범")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLockerBinding.inflate(inflater, container, false)

//        SavedSongDatas.apply{
//            add(SavedSong(title="Butter", singer = "방탄소년단(BTS)", R.drawable.img_album_exp))
//            add(SavedSong(title = "Lilac","아이유(IU)",R.drawable.img_album_exp2))
//            add(SavedSong("Next Level","에스파(AESPA)",R.drawable.img_album_exp3))
//            add(SavedSong("Boy with Luv","방탄소년단(BTS)",R.drawable.img_album_exp4))
//            add(SavedSong("BBoom BBoom","모모랜드(MOMOLAND",R.drawable.img_album_exp5))
//            add(SavedSong("Weekend","태연(Tea Yeon)",R.drawable.img_album_exp6))
//            add(SavedSong(title="Butter", singer = "방탄소년단(BTS)", R.drawable.img_album_exp))
//            add(SavedSong(title = "Lilac","아이유(IU)",R.drawable.img_album_exp2))
//            add(SavedSong("Next Level","에스파(AESPA)",R.drawable.img_album_exp3))
//            add(SavedSong("Boy with Luv","방탄소년단(BTS)",R.drawable.img_album_exp4))
//            add(SavedSong("BBoom BBoom","모모랜드(MOMOLAND",R.drawable.img_album_exp5))
//            add(SavedSong("Weekend","태연(Tea Yeon)",R.drawable.img_album_exp6))
//        }

//        val lockerRVAdapter= LockerRVAdapter(SavedSongDatas)
//        binding.lockerSongRv.adapter=lockerRVAdapter
//        binding.lockerSongRv.layoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)

        

        val lockerAdapter = LockerVPAdapter(this)
        binding.lockerContentVp.adapter = lockerAdapter

        TabLayoutMediator(binding.lockerContentTb, binding.lockerContentVp) { tab, position ->
            tab.text = information[position]
        }.attach()

        binding.lockerLoginTv.setOnClickListener {
            startActivity((Intent(activity, LoginActivity::class.java)))
        }

        return binding.root
    }

    override fun onStart(){
        super.onStart()
        initViews()
    }

    //LoginActivity에서 Jwt를 저장했던 "auth"라는 이름 가져옴.
    //activity 뒤에 물음표 적는 이유? : 프래그먼트에서 사용할 때 적는 방법.
    private fun getJwt():Int{
        val spf=activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getInt("jwt",0) //SharedPreference에서 가져온 값이 없다면 0을 반환.
    }

    private fun initViews(){
        val jwt:Int=getJwt()
        if (jwt==0) { //로그인을 해야하는 상황
            binding.lockerLoginTv.text = "로그인"
            binding.lockerLoginTv.setOnClickListener {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        }else{ //로그인을 한 상태
            binding.lockerLoginTv.text="로그아웃"
            binding.lockerLoginTv.setOnClickListener {
                //로그아웃 진행
                logout()
                //로그아웃 시 메인 액티비티로 이동
                startActivity(Intent(activity, MainActivity::class.java))
            }
        }
    }

    private fun logout(){
        val spf=activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val editor=spf!!.edit()
        editor.remove("jwt") //"jwt"라는 키값에 저장된 값 삭제
        editor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}