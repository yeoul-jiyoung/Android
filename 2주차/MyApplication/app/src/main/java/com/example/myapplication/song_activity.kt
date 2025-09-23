package com.example.myapplication

import android.os.Bundle
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
}