package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.myapplication.databinding.MainActivityBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fun openSong() {
            val title = binding.homeMiniPlayerTitle.text.toString()
            val singer = binding.homeMiniPlayerSinger.text.toString()
            startActivity(Intent(this, SongActivity::class.java).apply {
                putExtra("title", title)
                putExtra("singer", singer)
            })
        }
        binding.homeMiniPlayer.setOnClickListener { openSong() }
        binding.homeMiniPlayerTitle.setOnClickListener { openSong() }
        binding.homeMiniPlayerSinger.setOnClickListener { openSong() }

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, HomeFragment())
            .commitAllowingStateLoss()
        setupBottomNavigation()
        findViewById<BottomNavigationView>(R.id.home_bottom_navigation)
            .selectedItemId = R.id.menu_home
    }
    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.home_bottom_navigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, HomeFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.menu_look -> {   // 메뉴 xml에 실제로 이 id가 있어야 함
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, LookFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.menu_search -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, SearchFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.menu_locker -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, LockerFragment())
                        .commitAllowingStateLoss()
                    true
                }
                else -> false
            }
        }
    }
}