package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.myapplication.databinding.MainActivityBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private var song:Song = Song()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputDummySongs(applicationContext)

        binding.homeMiniPlayerContent.setOnClickListener {
            startActivity(Intent(this, SongActivity::class.java))

        }
        setupBottomNavigation()

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

        findViewById<BottomNavigationView>(R.id.home_bottom_navigation)
            .selectedItemId = R.id.menu_home
    }

    override fun onStart() {
        super.onStart()

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId",0)

        val songDB = SongDatabase.getInstance(this)!!

        song = if (songId == 0){
            songDB.songDao().getSong(1)
        } else{
            songDB.songDao().getSong(songId)
        }

        Log.d("song ID", song.id.toString())

        //setMiniPlayer(song)
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
private fun inputDummySongs(context: Context){
    val songDB= SongDatabase.getInstance(context.applicationContext)!!
    val songs=songDB.songDao().getSongs()

    if(songs.isNotEmpty()) return

    songDB.songDao().insert(
        Song(
            "Lilac",
            "아이유 (IU)",
            0,
            200,
            false,
            "music_lilac",
            R.drawable.img_album_exp2,
            false,
        )
    )

    songDB.songDao().insert(
        Song(
            "Flu",
            "아이유 (IU)",
            0,
            200,
            false,
            "music_flu",
            R.drawable.img_album_exp2,
            false,
        )
    )

    songDB.songDao().insert(
        Song(
            "Butter",
            "방탄소년단 (BTS)",
            0,
            190,
            false,
            "music_butter",
            R.drawable.img_album_exp3,
            false,
        )
    )

    songDB.songDao().insert(
        Song(
            "Next Level",
            "에스파 (AESPA)",
            0,
            210,
            false,
            "music_next",
            R.drawable.img_album_exp4,
            false,
        )
    )


    songDB.songDao().insert(
        Song(
            "Boy with Luv",
            "방탄소년단 (BTS)",
            0,
            230,
            false,
            "music_boy",
            R.drawable.img_album_exp5,
            false,
        )
    )


    songDB.songDao().insert(
        Song(
            "BBoom BBoom",
            "모모랜드 (MOMOLAND)",
            0,
            240,
            false,
            "music_bboom",
            R.drawable.img_album_exp5,
            false,
        )
    )

    val _songs = songDB.songDao().getSongs()
    Log.d("DB data", _songs.toString())
}