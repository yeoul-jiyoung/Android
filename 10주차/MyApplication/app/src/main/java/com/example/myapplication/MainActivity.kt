package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
//import androidx.compose.ui.semantics.text
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.myapplication.databinding.MainActivityBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    // 미니플레이어용 상태
    private lateinit var songDB: SongDatabase
    private val songs = arrayListOf<Song>()
    private var nowPos = 0
    private var miniTimer: MiniTimer? = null
    private var isMiniPlaying: Boolean = false

    //Firebase 참조 변수 추가
//    private val database= Firebase.database
//    private val songRef=database.getReference("songs")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //loadSongsFromFirebase()

        // DB/플레이리스트 준비
        inputDummySongs(applicationContext)
        inputDummyAlbums(applicationContext)
        songDB = SongDatabase.getInstance(this)!!
        songs.clear()
        songs.addAll(songDB.songDao().getSongs())

        // 컨테이너 기본 프래그먼트
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, HomeFragment())
            .commitAllowingStateLoss()
        findViewById<BottomNavigationView>(R.id.home_bottom_navigation)
            .selectedItemId = R.id.menu_home

        // 미니플레이어 → SongActivity로 진입
        binding.homeMiniPlayerContent.setOnClickListener { openSongActivity() }
        binding.homeMiniPlayerTitle.setOnClickListener { openSongActivity() }
        binding.homeMiniPlayerSinger.setOnClickListener { openSongActivity() }

        // 미니플레이어 컨트롤
        binding.homeMiniPlayerPlay.setOnClickListener { toggleMiniPlay() }
        binding.homeMiniPlayerPrevious.setOnClickListener { moveMiniSong(-1) }
        binding.homeMiniPlayerNext.setOnClickListener { moveMiniSong(+1) }
        binding.homeMiniPlayerGoList.setOnClickListener { openSongActivity() }

        setupBottomNavigation()
    }


    // Firebase 데이터 로딩 함수 구현
//    private fun loadSongsFromFirebase() {
//        // Firebase에서 "songs" 경로의 데이터를 한 번 가져옴
//        songRef.get().addOnSuccessListener { dataSnapshot ->
//            songs.clear() // 기존 목록을 비움
//            for (snapshot in dataSnapshot.children) {
//                // Firebase 데이터를 Song 객체로 변환하여 리스트에 추가
//                snapshot.getValue(Song::class.java)?.let { song ->
//                    snapshot.key?.toIntOrNull()?.let { song.id = it }
//                    songs.add(song)
//                }
//            }
//            Log.d("MainActivity", "Firebase loaded, songs.size=${songs.size}")
//            songs.forEach {
//                Log.d("MainActivity", "song id=${it.id}, title=${it.title}, singer=${it.singer}")
//            }

            // 데이터 로딩이 완료된 후, UI 초기화 작업
            // onStart의 로직을 그대로 가져오는 함수
//            setupMiniPlayerOnStart()
//
//        }.addOnFailureListener {
//                e ->
//            Log.e("MainActivity", "Firebase load failed", e)
//            // 데이터 로딩 실패 시 처리 토스트 메세지 출력
//            Toast.makeText(this, "데이터 로딩에 실패했습니다.", Toast.LENGTH_SHORT).show()
//        }
//    }


    // onStart의 로직을 새 함수로 분리
    private fun setupMiniPlayerOnStart() {
        // onStart에 있던 코드를 그대로 여기로 옮김
        if (songs.isEmpty()) return

        // SongActivity에서 저장해 둔 song_json(현재 곡 전체 정보) 기준으로 복원
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songJson = spf.getString("song_json", null)

        if (songJson != null) {
            val savedSong = Gson().fromJson(songJson, Song::class.java)

            // SongActivity에서 마지막으로 재생하던 곡 id 기준으로 nowPos 맞추기
            nowPos = getPlayingSongPosition(savedSong.id).coerceIn(0, songs.lastIndex)

            // songs 리스트에 저장된 곡 정보도 최신 상태로 맞춤
            songs[nowPos].title = savedSong.title
            songs[nowPos].singer = savedSong.singer
            songs[nowPos].coverImg = savedSong.coverImg
            songs[nowPos].playTime = savedSong.playTime
            songs[nowPos].second = savedSong.second
            songs[nowPos].isPlaying = savedSong.isPlaying

            isMiniPlaying = savedSong.isPlaying

            bindMiniUI(songs[nowPos], songs[nowPos].second)
            startMiniTimer(songs[nowPos].second, isMiniPlaying)
            setMiniPlayIcon(isMiniPlaying)
            return
        }

        // song_json이 없는 경우에만 기존 PlaybackStore / songId 로직 사용
        val st = PlaybackStore.read(this)
        if (st != null) {
            //SongActivity/미니 플레이어에서 저장된 위치로부터 복원
            nowPos = getPlayingSongPosition(st.songId).coerceIn(0, songs.lastIndex)
            val sec = (st.posMs / 1000).coerceAtLeast(0)
            songs[nowPos].second = sec
            isMiniPlaying = st.isPlaying
        } else {
            // 기존 복원 로직 유지(이전에 재생되던 곡이 없으면)
            val songId = spf.getInt("songId", 0)
            nowPos = getPlayingSongPosition(songId).coerceIn(0, songs.lastIndex)
            isMiniPlaying = false
        }

        bindMiniUI(songs[nowPos], songs[nowPos].second)
        startMiniTimer(songs[nowPos].second, isMiniPlaying)
        setMiniPlayIcon(isMiniPlaying)
    }

    override fun onStart() {
        super.onStart()

        if (songs.isEmpty()) return
        if (songs.isNotEmpty()) {
            setupMiniPlayerOnStart()
        }

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songJson = spf.getString("song_json", null)

        val st = PlaybackStore.read(this)
        if (st != null) {
            //SongActivity/미니 플레이어에서 저장된 위치로부터 복원
            nowPos = getPlayingSongPosition(st.songId).coerceIn(0, songs.lastIndex)
            val sec = (st.posMs / 1000).coerceAtLeast(0)
            songs[nowPos].second = sec
            isMiniPlaying = st.isPlaying
            bindMiniUI(songs[nowPos], sec)
            startMiniTimer(sec, isMiniPlaying)
            setMiniPlayIcon(isMiniPlaying)
        } else {
            // 기존 복원 로직 유지(이전에 재생되던 곡이 없으면)
            val spf = getSharedPreferences("song", MODE_PRIVATE)
            val songId = spf.getInt("songId", 0)
            nowPos = getPlayingSongPosition(songId).coerceIn(0, songs.lastIndex)
            isMiniPlaying = false
        }

        bindMiniUI(songs[nowPos], songs[nowPos].second)
        startMiniTimer(songs[nowPos].second, isMiniPlaying)
        setMiniPlayIcon(isMiniPlaying)
    }

    override fun onPause() {
        super.onPause()
        if (songs.isEmpty()) return

        // 진행상황 저장
        val playTime = songs[nowPos].playTime
        val percent = binding.miniPlayerSongProgress.progress
        if (playTime > 0) {
            val sec = (percent * playTime) / 100
            songs[nowPos].second = sec
        }
        songs[nowPos].isPlaying = false
        isMiniPlaying = false
        setMiniPlayIcon(false)

        // 타이머 정리
        miniTimer?.interrupt()
        miniTimer = null

        // 현재 곡 id 저장
        getSharedPreferences("song", MODE_PRIVATE)
            .edit()
            .putInt("songId", songs[nowPos].id)
            .apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        miniTimer?.interrupt()
        miniTimer = null
    }

    // Mini Player 동작
    private fun toggleMiniPlay() {
        if (songs.isEmpty()) return
        isMiniPlaying = !isMiniPlaying
        setMiniPlayIcon(isMiniPlaying)

        // Timer 존재 보장
        if (miniTimer == null) {
            startMiniTimer(songs[nowPos].second, isMiniPlaying)
        } else {
            miniTimer?.isPlaying = isMiniPlaying
        }

        // 즉시 저장
        val elapsedMs = (songs[nowPos].second * 1000) + (0)
        PlaybackStore.save(this,
            PlaybackState(songs[nowPos].id, elapsedMs, isMiniPlaying))
    }

    private fun moveMiniSong(direct: Int) {
        if (songs.isEmpty()) return
        val next = nowPos + direct
        if (next !in 0..songs.lastIndex) {
            Toast.makeText(
                this,
                if (direct < 0) "first song" else "last song",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        nowPos = next

        miniTimer?.interrupt()
        miniTimer = null

        // 새로운 곡 바인딩(위치는 0초부터)
        songs[nowPos].second = 0
        bindMiniUI(songs[nowPos], startSecond = 0)

        // 재생 상태 유지
        startMiniTimer(startSecond = 0, isPlaying = isMiniPlaying)

        PlaybackStore.save(this,
            PlaybackState(songs[nowPos].id, 0, isMiniPlaying))
    }

    private fun bindMiniUI(song: Song, startSecond: Int) {
        binding.homeMiniPlayerTitle.text = song.title ?: ""
        binding.homeMiniPlayerSinger.text = song.singer ?: ""
        // SeekBar 0~100 퍼센트
        val percent = if (song.playTime > 0)
            ((startSecond.toFloat() / song.playTime) * 100).toInt()
        else 0
        binding.miniPlayerSongProgress.progress = percent.coerceIn(0, 100)
    }

    private fun startMiniTimer(startSecond: Int, isPlaying: Boolean) {
        miniTimer?.interrupt()
        miniTimer = MiniTimer(
            playTime = songs[nowPos].playTime,
            startSecond = startSecond,
            isPlaying = isPlaying
        ).apply { start() }
    }

    //Play & Pause 버튼 클릭시 아이콘이 토글되는 로직
    private fun setMiniPlayIcon(isPlaying: Boolean) {
        binding.homeMiniPlayerPlay.setImageResource(
            if (isPlaying) R.drawable.btn_miniplay_pause   // play 아이콘
            else R.drawable.btn_miniplayer_play            // pause 아이콘
        )
    }

    // 진행률만 담당하는 미니 타이머
    inner class MiniTimer(
        private val playTime: Int,
        startSecond: Int = 0,
        @Volatile var isPlaying: Boolean = true
    ) : Thread() {

        private var second: Int = startSecond.coerceAtLeast(0)
        private var mills: Int = 0 // ms 누적
        private var saveTick: Int = 0

        override fun run() {
            try {
                if (playTime <= 0) {
                    runOnUiThread { binding.miniPlayerSongProgress.progress = 0 }
                    return
                }

                while (!isInterrupted) {
                    if (second >= playTime) break

                    if (isPlaying) {
                        sleep(50)
                        mills += 50
                        saveTick+=50

                        val elapsedMs = second * 1000 + mills
                        val totalMs = playTime * 1000
                        val percent = ((elapsedMs.toFloat() / totalMs) * 100)
                            .toInt().coerceIn(0, 100)

                        runOnUiThread {
                            binding.miniPlayerSongProgress.progress = percent
                        }

                        if (mills >= 1000) {
                            mills -= 1000
                            second++
                        }

                        if (saveTick >= 500) {
                            saveTick = 0
                            val id = songs[nowPos].id
                            PlaybackStore.save(
                                this@MainActivity,
                                PlaybackState(id, elapsedMs, isMiniPlaying)
                            )
                        }
                    } else {
                        sleep(80)
                    }
                }
            } catch (e: InterruptedException) {
                // 정상 종료
            } catch (t: Throwable) {
                Log.e("MiniTimer", "error", t)
            }
        }
    }

    // 기타 유틸
    private fun openSongActivity() {
        if (songs.isEmpty()) return
        val cur = songs[nowPos]
        // 현재 미니플레이어 진행 위치를 저장해서 SongActivity가 이어받도록
        val playTime = cur.playTime
        val percent = binding.miniPlayerSongProgress.progress
        if (playTime > 0) {
            cur.second = (percent * playTime) / 100
        }
        val songJson = Gson().toJson(cur)
        getSharedPreferences("song", MODE_PRIVATE)
            .edit()
            .putInt("songId", cur.id)
            .putString("song_json", songJson)
            .apply()

        startActivity(Intent(this, SongActivity::class.java))
    }

    private fun getPlayingSongPosition(songId: Int): Int {
        if (songId == 0) return 0
        for (i in 0 until songs.size) if (songs[i].id == songId) return i
        return 0
    }


    private fun updateMiniPlayer() {
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songJson = spf.getString("song_json", null)?:return

        if (songJson != null) {
            val song = Gson().fromJson(songJson, Song::class.java)

            // 미니플레이어의 UI 요소들을 업데이트
            binding.homeMiniPlayerTitle.text = song.title
            binding.homeMiniPlayerSinger.text = song.singer
        }
    }

    override fun onResume() {
        super.onResume()
        updateMiniPlayer()
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
                R.id.menu_look -> {
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

 //더미 데이터 주입 로직
private fun inputDummySongs(context: Context){
    val songDB= SongDatabase.getInstance(context.applicationContext)!!
    val songs=songDB.songDao().getSongs()
    if(songs.isNotEmpty()) return

    songDB.songDao().insert(
        Song(id=0,"Lilac","아이유 (IU)",0,200,false,"music_lilac",R.drawable.img_album_exp2,false)
    )
    songDB.songDao().insert(
        Song(id=1,"Flu","아이유 (IU)",0,200,false,"music_flu",R.drawable.img_album_exp2,false)
    )
    songDB.songDao().insert(
        Song(id=2,"Butter","방탄소년단 (BTS)",0,190,false,"music_butter",R.drawable.img_album_exp,false)
    )
    songDB.songDao().insert(
        Song(id=3,"Next Level","에스파 (AESPA)",0,210,false,"music_next",R.drawable.img_album_exp3,false)
    )
    songDB.songDao().insert(
        Song(id=4,"Boy with Luv","방탄소년단 (BTS)",0,230,false,"music_boy",R.drawable.img_album_exp4,false)
    )
    songDB.songDao().insert(
        Song(id=5,"BBoom BBoom","모모랜드 (MOMOLAND)",0,240,false,"music_bboom",R.drawable.img_album_exp5,false)
    )

    val _songs = songDB.songDao().getSongs()
    Log.d("DB data", _songs.toString())
}
//더미 데이터 주입 로직
private fun inputDummyAlbums(context: Context){
    val songDB= SongDatabase.getInstance(context.applicationContext)!!
    val albums=songDB.albumDao().getAlbums()
    if(albums.isNotEmpty()) return

    songDB.albumDao().insert(
        Album(0,"IU 5th Album : 'LILAC'","아이유 (IU)",R.drawable.img_album_exp2)
    )
    songDB.albumDao().insert(
        Album(1,"Butter","방탄소년단 (BTS)",R.drawable.img_album_exp)
    )
    songDB.albumDao().insert(
        Album(2,"Next Level Remixes","에스파 (AESPA)",R.drawable.img_album_exp3)
    )
    songDB.albumDao().insert(
        Album(3,"MAP OF THE SEOUL : PERSONA","방탄소년단 (BTS)",R.drawable.img_album_exp4)
    )
    songDB.albumDao().insert(
        Album(4,"GREAT!","모모랜드 (MOMOLAND)",R.drawable.img_album_exp5)
    )
}