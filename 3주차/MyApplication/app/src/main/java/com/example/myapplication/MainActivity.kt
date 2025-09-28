package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val songTitle = binding.homeMiniPlayerTitle.toString()
        val songSinger = binding.homeMiniPlayerSinger.text.toString()

        binding.homeMiniPlayer.setOnClickListener {
            val intent = Intent(this, SongActivity::class.java)
            intent.putExtra("title", songTitle)
            intent.putExtra("singer", songSinger)
            startActivity(intent)
        }
        binding.homeMiniPlayerTitle.setOnClickListener {
            val intent = Intent(this, SongActivity::class.java)
            intent.putExtra("title",songTitle)
            intent.putExtra("singer",songSinger)
            startActivity(intent)
        }
        binding.homeMiniPlayerSinger.setOnClickListener {
            val intent = Intent(this, SongActivity::class.java)
            intent.putExtra("title",songTitle)
            intent.putExtra("singer",songSinger)
            startActivity(intent)
        }

        binding.homePannelVp.adapter = PannelVPAdapter(fragment = supportFragmentManager.findFragmentById(R.id.homeFragment)!!)
        binding.homePannelVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

    }
}
