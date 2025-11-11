package com.example.myapplication

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.SongActivityBinding
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SongActivity : AppCompatActivity() {

    private lateinit var binding: SongActivityBinding

    private var mediaPlayer: MediaPlayer? = null
    private val gson: Gson = Gson()

    private val songs = arrayListOf<Song>()

    //DB 주석
    //private lateinit var songDB: SongDatabase

    private val database= Firebase.database //Firebase 인스턴스
    private val songRef=database.getReference("songs") //songs 경로 참조

    private var timer: Timer? = null
    private var nowPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SongActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //로딩 상태: 재생 버튼 등 잠시 비활성화
        setControlsEnabled(false)

        initPlayList()
        //initSong()
        initClickListener()
    }

    private fun setControlsEnabled(enabled: Boolean) {
        binding.songActivityPlayPlayerImgIv.isEnabled = enabled
        binding.songActivityPausePlayerImgIv.isEnabled = enabled
        binding.songActivityNextPlayerImgIv.isEnabled = enabled
        binding.songActivityPreviousPlayerImgIv.isEnabled = enabled
        binding.songActivityLikeImgIv.isEnabled = enabled
    }

    override fun onStop() {
        super.onStop()
        if (songs.isEmpty()) return

        // 현재 위치/상태 저장
        val posSecFromPlayer = (mediaPlayer?.currentPosition ?: 0) / 1000
        val playTime = songs[nowPos].playTime
        val percent = binding.songProgress.progress
        val posSecFromPercent = if (playTime > 0) (percent * playTime) / 100 else 0
        songs[nowPos].second = maxOf(posSecFromPlayer, posSecFromPercent)

        songs[nowPos].isPlaying = false
        setPlayerStatus(false)

        // 다른 화면(미니플레이어, 앨범 프래그먼트)과 데이터 동기화를 위해 현재 곡의 전체 정보를 JSON으로 저장
        val songJson = gson.toJson(songs[nowPos])

        getSharedPreferences("song", MODE_PRIVATE)
            .edit()
            .putInt("songId", songs[nowPos].id) // 기존 로직 유지를 위해 id도 저장
            .putString("song_json", songJson)   // 곡 전체 정보를 JSON으로 저장
            .apply()
    }


    override fun onDestroy() {
        super.onDestroy()
        timer?.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun initPlayList() {
        //songDB = SongDatabase.getInstance(this)!!
        //songs.clear()
        //songs.addAll(songDB.songDao().getSongs())

        //Firebase에서 데이터 가져오기
        songRef.get().addOnSuccessListener { dataSnapshot ->
            // 임시 리스트에 Firebase에서 가져온 데이터를 담습니다.
            val songListFromFirebase = mutableListOf<Song>()
            for (snapshot in dataSnapshot.children) {
                snapshot.getValue(Song::class.java)?.let { song ->
                    snapshot.key?.toIntOrNull()?.let { song.id = it }
                    songListFromFirebase.add(song)
                }
            }

            songs.clear()
            // 가져온 리스트를 song.id 기준으로 오름차순 정렬하여 songs 리스트에 추가합니다.
            songs.addAll(songListFromFirebase.sortedBy { it.id })

            if (songs.isEmpty()) {
                Toast.makeText(this, "서버에서 곡을 찾지 못했습니다.", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            setControlsEnabled(true)
            initSong()
        }
            .addOnFailureListener { e ->
                Log.e("SongActivity", "Firebase 로딩 실패", e)
                Toast.makeText(this, "네트워크 오류로 곡을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                setControlsEnabled(false)
            }
    }

    private fun savePlayback() {
        val id = if (songs.isNotEmpty()) songs[nowPos].id else 0
        val pos = mediaPlayer?.currentPosition ?: 0
        val playing = songs.getOrNull(nowPos)?.isPlaying ?: false
        PlaybackStore.save(this, PlaybackState(id, pos, playing))
    }

    private fun initClickListener() {
        binding.songActivityArrowDown.setOnClickListener { finish() }

        binding.songActivityPlayPlayerImgIv.setOnClickListener { setPlayerStatus(true) }
        binding.songActivityPausePlayerImgIv.setOnClickListener { setPlayerStatus(false) }

        binding.songActivityNextPlayerImgIv.setOnClickListener { moveSong(+1) }
        binding.songActivityPreviousPlayerImgIv.setOnClickListener { moveSong(-1) }

        binding.songActivityLikeImgIv.setOnClickListener {
            //setLike(songs[nowPos].isLike)
            setLike()
        }
    }

    private fun initSong() {
        if (songs.isEmpty()) {
            Toast.makeText(this, "재생할 노래가 없습니다.", Toast.LENGTH_SHORT).show()
            //finish()
            return
        }

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)
        nowPos = getPlayingSongPosition(songId).coerceIn(0, songs.lastIndex)

        PlaybackStore.read(this)?.let { st ->
            if (st.songId != 0) nowPos = getPlayingSongPosition(st.songId)
            songs[nowPos].second = (st.posMs / 1000).coerceAtLeast(0)
            songs[nowPos].isPlaying = st.isPlaying
        }

        setPlayer(songs[nowPos])
        startTimer()
    }

//    //DB의 setLike
//    private fun setLike(isLike: Boolean){
//        songs[nowPos].isLike=!isLike
//        songDB.songDao().updateIsLikeById(!isLike,songs[nowPos].id)
//
//        if(!isLike){
//            binding.songActivityLikeImgIv.setImageResource(R.drawable.ic_my_like_on)
//        }else{
//            binding.songActivityLikeImgIv.setImageResource(R.drawable.ic_my_like_off)
//        }
//    }

    //Firebase의 setLike
    private fun setLike() {
        val currentSong = songs[nowPos]
        val newIsLike = !currentSong.isLike

        // 1. UI와 메모리 데이터 즉시 업데이트 (낙관적 UI 업데이트)
        currentSong.isLike = newIsLike
        binding.songActivityLikeImgIv.setImageResource(
            if (newIsLike) R.drawable.ic_my_like_on
            else R.drawable.ic_my_like_off
        )

        // 2. Firebase에 데이터 저장 요청
        songRef.child(currentSong.id.toString()).child("isLike").setValue(newIsLike)
            .addOnFailureListener { e ->
                // 3. Firebase 저장 실패 시 롤백
                Log.e("Firebase", "좋아요 상태 저장 실패", e)
                Toast.makeText(this, "네트워크 오류로 좋아요 상태를 저장하지 못했습니다.", Toast.LENGTH_SHORT).show()

                // 메모리 데이터 롤백
                currentSong.isLike = !newIsLike

                // UI 롤백 (현재 보고 있는 곡이 롤백 대상 곡일 경우에만)
                if (nowPos == getPlayingSongPosition(currentSong.id)) {
                    binding.songActivityLikeImgIv.setImageResource(
                        if (!newIsLike) R.drawable.ic_my_like_on
                        else R.drawable.ic_my_like_off
                    )
                }
            }
    }


    private fun moveSong(direct: Int) {
        if (nowPos + direct !in 0..songs.lastIndex) {
            Toast.makeText(this, if (direct < 0) "first song" else "last song", Toast.LENGTH_SHORT).show()
            return
        }
        nowPos += direct

        timer?.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null

        setPlayer(songs[nowPos])
        startTimer()
    }

    private fun getPlayingSongPosition(songId: Int): Int {
        for (i in 0 until songs.size) if (songs[i].id == songId) return i
        return 0
    }

    private fun setPlayer(song: Song) {

        // 타이틀/가수/커버
        binding.songActivityTitle.text = song.title
        binding.songActivitySinger.text = song.singer
        // 앨범커버: 리소스 "이름"으로 로드
        val resId = song.coverImg ?: 0
        binding.songActivityTitleImgIv.setImageResource(
            if (resId != 0) resId else R.drawable.img_album_exp2
        )

        // 시작/끝 시간
        binding.songStartTime.text = String.format("%02d:%02d",song.second/60,song.second%60)
        binding.songEndTime.text = String.format("%02d:%02d",song.playTime/60,song.playTime%60)

        // 초기 퍼센트(0~100)
        val playTime = song.playTime
        val percent = if (playTime > 0) ((song.second.toFloat() / playTime) * 100).toInt() else 0
        //binding.songProgress.progress = (song.second*1000/song.playTime)
        binding.songProgress.progress = percent.coerceIn(0, 100)

        // 오디오 리소스
        val music = resources.getIdentifier(song.music, "raw", packageName)
        if (music == 0) {
            Toast.makeText(this, "오디오 리소스를 찾을 수 없습니다: ${song.music}", Toast.LENGTH_SHORT).show()
            mediaPlayer?.release(); mediaPlayer = null
            setPlayerStatus(isPlaying = true)
            return
        }

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, music)?.apply {
            // 저장된 위치로 시킹
            seekTo((song.second).coerceAtLeast(0) * 1000)
            setOnCompletionListener {
                setPlayerStatus(isPlaying=true)
                moveSong(+1)
            }
        }

        // 좋아요 아이콘 상태를 현재 곡의 isLike 값에 맞춰 설정
        binding.songActivityLikeImgIv.setImageResource(
            if (song.isLike) R.drawable.ic_my_like_on
            else R.drawable.ic_my_like_off
        )


        // 기본은 저장된 상태대로
        setPlayerStatus(song.isPlaying)
    }

    private fun setPlayerStatus(isPlaying: Boolean) {
        if (songs.isNotEmpty()) songs[nowPos].isPlaying = isPlaying

        // 타이머 존재 보장 + isPlaying 동기화
        if (timer == null) startTimer()
        timer?.isPlaying = isPlaying

        if (isPlaying) {
            binding.songActivityPlayPlayerImgIv.visibility = View.GONE
            binding.songActivityPausePlayerImgIv.visibility = View.VISIBLE
            mediaPlayer?.start()
            savePlayback()
        } else {
            binding.songActivityPlayPlayerImgIv.visibility = View.VISIBLE
            binding.songActivityPausePlayerImgIv.visibility = View.GONE
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) mp.pause()
                val sec = mp.currentPosition / 1000
                binding.songStartTime.text = formatSec(sec)
                if (songs.isNotEmpty()) songs[nowPos].second = sec
                // 중복 pause 제거 (한 번만 호출)
                savePlayback()
            }
        }
    }

    private fun startTimer() {
        timer?.interrupt()
        timer = Timer(
            playTime = songs[nowPos].playTime,
            startSecond = songs[nowPos].second,
            isPlaying = songs[nowPos].isPlaying
        ).apply { start() }
    }

    private fun formatSec(totalSec: Int): String {
        val m = (totalSec / 60).coerceAtLeast(0)
        val s = (totalSec % 60).coerceAtLeast(0)
        return String.format("%02d:%02d", m, s)
    }

    // Coroutine 으로 코드 변경
    inner class Timer(
        private val playTime: Int,
        startSecond: Int = 0,
        @Volatile var isPlaying: Boolean = true
    ) {
        private var second: Int = startSecond.coerceAtLeast(0)
        private var mills: Int = 0            // ms 누적
        private var saveTick: Int = 0         // 0.5초 저장 간격 누적
        private var job: Job? = null

        fun start() {
            // 코루틴 시작
            job = lifecycleScope.launch {
                if (playTime <= 0) {
                    // UI 초기화
                    binding.songProgress.progress = 0
                    binding.songStartTime.text = "00:00"
                    return@launch
                }

                while (isActive) {
                    // 화면이 보이지 않는 상태면 가볍게 대기
                    if (!lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.STARTED)) {
                        delay(80)
                        continue
                    }

                    if (second >= playTime) break

                    if (isPlaying) {
                        delay(50)
                        mills += 50

                        // 0.5초마다 저장
                        saveTick += 50
                        if (saveTick >= 500) {
                            saveTick = 0
                            savePlayback()
                        }

                        // 퍼센트(0~100) 계산
                        val elapsedMs = second * 1000 + mills
                        val totalMs = playTime * 1000
                        val percent = ((elapsedMs.toFloat() / totalMs) * 100).toInt().coerceIn(0, 100)

                        // UI 갱신
                        binding.songProgress.progress = percent

                        if (mills >= 1000) {
                            mills -= 1000
                            second++
                            binding.songStartTime.text = formatSec(second)
                        }
                    } else {
                        // 멈춤 상태에서는 과도한 busy-loop 방지
                        delay(80)
                    }
                }
            }
        }

        fun interrupt() {
            job?.cancel()
            job = null
        }
    }
}
