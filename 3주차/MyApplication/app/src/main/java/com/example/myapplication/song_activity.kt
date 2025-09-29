package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.SongActivityBinding

class SongActivity : AppCompatActivity() {
    lateinit var binding: SongActivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SongActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("title") && intent.hasExtra("singer")) {
            binding.songActivityTitle.text = intent.getStringExtra("title")
            binding.songActivitySinger.text = intent.getStringExtra("singer")
        }

        binding.songActivityArrowDown.setOnClickListener {
            finish()
        }


    }

    private fun setPlayerStatus (isPlaying : Boolean){

        if(isPlaying){
            // 재생 상태일 때
            binding.songActivityPlayPlayerImgIv.visibility = View.GONE
            //binding.songPauseIv.visibility = View.VISIBLE
        } else {
            // 일시정지 상태일 때
            binding.songActivityPlayPlayerImgIv.visibility = View.VISIBLE
            //binding.songPauseIv.visibility = View.GONE

        }
    }

}