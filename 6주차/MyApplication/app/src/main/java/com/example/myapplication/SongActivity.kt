package com.example.myapplication

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.SongActivityBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {

    private lateinit var binding: SongActivityBinding

    private var mediaPlayer: MediaPlayer? = null
    private val gson: Gson = Gson()

    private val songs = arrayListOf<Song>()
    private lateinit var songDB: SongDatabase
    private var timer: Timer? = null
    private var nowPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SongActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPlayList()
        initSong()
        initClickListener()
    }

    override fun onPause() {
        super.onPause()
        if (songs.isEmpty()) return

        // 현재 위치/상태 저장
        val posSecFromPlayer = (mediaPlayer?.currentPosition ?: 0) / 1000
        val playTime = songs[nowPos].playTime
        val percent = binding.songProgress.progress
        val posSecFromPercent = if (playTime > 0) (percent * playTime) / 100 else 0
        songs[nowPos].second = maxOf(posSecFromPlayer, posSecFromPercent)

        songs[nowPos].isPlaying = false
        setPlayerStatus(false)

        getSharedPreferences("song", MODE_PRIVATE)
            .edit()
            .putInt("songId", songs[nowPos].id)
            .apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun initPlayList() {
        songDB = SongDatabase.getInstance(this)!!
        songs.clear()
        songs.addAll(songDB.songDao().getSongs())
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
            if (songs.isEmpty()) return@setOnClickListener
            val cur = songs[nowPos]
            cur.isLike = !cur.isLike
            songDB.songDao().updateIsLikeById(cur.isLike, cur.id)
            binding.songActivityLikeImgIv.setImageResource(
                if (cur.isLike) R.drawable.ic_my_like_on else R.drawable.ic_my_like_off
            )
        }
    }

    private fun initSong() {
        if (songs.isEmpty()) {
            Toast.makeText(this, "재생할 노래가 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
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
        startTimer()   // 기존 타이머/코루틴 시작 위치 유지
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
        song.coverImg?.let { binding.songActivityTitleImgIv.setImageResource(it) }

        // 시작/끝 시간
        binding.songStartTime.text = formatSec(song.second)
        binding.songEndTime.text = formatSec(song.playTime)

        // 초기 퍼센트(0~100), 0 나눔 방지
        val playTime = song.playTime
        val percent = if (playTime > 0) ((song.second.toFloat() / playTime) * 100).toInt() else 0
        binding.songProgress.progress = percent.coerceIn(0, 100)

        // 오디오 리소스 안전 가드
        val music = resources.getIdentifier(song.music, "raw", packageName)
        if (music == 0) {
            Toast.makeText(this, "오디오 리소스를 찾을 수 없습니다: ${song.music}", Toast.LENGTH_SHORT).show()
            mediaPlayer?.release(); mediaPlayer = null
            setPlayerStatus(false)
            return
        }

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, music)?.apply {
            // 저장된 위치로 시킹
            seekTo((song.second).coerceAtLeast(0) * 1000)
            setOnCompletionListener {
                setPlayerStatus(false)
                moveSong(+1)
            }
        }

        // 좋아요 아이콘
        binding.songActivityLikeImgIv.setImageResource(
            if (song.isLike) R.drawable.ic_my_like_on else R.drawable.ic_my_like_off
        )

        // 기본은 멈춘 상태
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
                mediaPlayer?.pause()
                savePlayback()
            }
        }
    }

    private fun startTimer() {
        timer?.interrupt() // 기존 쓰레드 정리
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

    //재생 진행률/시간 UI를 갱신하는 타이머
    inner class Timer(
        private val playTime: Int,
        startSecond: Int = 0,
        @Volatile var isPlaying: Boolean = true
    ) : Thread() {

        private var second: Int = startSecond.coerceAtLeast(0)
        private var mills: Int = 0 // ms 누적

        override fun run() {
            try {
                if (playTime <= 0) {
                    runOnUiThread {
                        binding.songProgress.progress = 0
                        binding.songStartTime.text = "00:00"
                    }
                    return
                }

                while (!isInterrupted) {

                    var saveTick=0

                    if (second >= playTime) break

                    if (isPlaying) {
                        sleep(50)
                        mills += 50

                        saveTick += 50
                        if (saveTick >= 500) {   // ✅ 0.5초마다 저장
                            saveTick = 0
                            savePlayback()
                        }

                        // 퍼센트(0~100) 계산
                        val elapsedMs = second * 1000 + mills
                        val totalMs = playTime * 1000
                        val percent = ((elapsedMs.toFloat() / totalMs) * 100).toInt().coerceIn(0, 100)

                        runOnUiThread {
                            binding.songProgress.progress = percent
                        }

                        if (mills >= 1000) {
                            mills -= 1000
                            second++
                            runOnUiThread {
                                binding.songStartTime.text = formatSec(second)
                            }
                        }
                    } else {
                        // 멈춤 상태에서는 과도한 busy-loop 방지
                        sleep(80)
                    }
                }
            } catch (e: InterruptedException) {
                // 정상 종료
            } catch (t: Throwable) {
                Log.e("Song", "Timer error", t)
            }
        }
    }
}
