package com.example.myapplication

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.SongActivityBinding
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SongActivity : AppCompatActivity() {

    private lateinit var binding: SongActivityBinding

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private val gson: Gson = Gson()

    private val songs = arrayListOf<Song>()
    private lateinit var songDB: SongDatabase
    private var nowPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SongActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPlayList()
        initSong()
        initClickListener()
    }

    override fun onResume() {
        super.onResume()
        // 화면 진입 시 기본은 "Play만 보이게"
        setPlayerStatus(false)
    }

    override fun onPause() {
        super.onPause()

        val posSec = (mediaPlayer?.currentPosition ?: 0) / 1000
        if (songs.isNotEmpty()) {
            songs[nowPos].second = posSec
            songs[nowPos].isPlaying = false
        }
        setPlayerStatus(false)

        val sp = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sp.edit()
        if (songs.isNotEmpty()) {
            editor.putInt("songId", songs[nowPos].id)
            // 원한다면 직렬화 데이터도 저장 가능:
            // editor.putString("songJson", gson.toJson(songs[nowPos]))
        }
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        progressJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // DB에서 재생목록 로드
    private fun initPlayList() {
        songDB = SongDatabase.getInstance(this)!!
        songs.clear()
        songs.addAll(songDB.songDao().getSongs())
    }

    // 클릭 리스너
    private fun initClickListener() {
        binding.songActivityArrowDown.setOnClickListener { finish() }

        binding.songActivityNextPlayerImgIv.setOnClickListener { moveSong(+1) }
        binding.songActivityPreviousPlayerImgIv.setOnClickListener { moveSong(-1) }

        binding.songActivityPlayPlayerImgIv.setOnClickListener { setPlayerStatus(true) }
        binding.songActivityPausePlayerImgIv.setOnClickListener { setPlayerStatus(false) }
    }

    // 화면 진입 시 선택 곡 세팅
    private fun initSong() {
        if (songs.isEmpty()) {
            Toast.makeText(this, "재생할 노래가 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", songs.first().id)
        nowPos = getPlayingSongPosition(songId).coerceIn(0, songs.lastIndex)

        setPlayer(songs[nowPos])
    }

    // 현재 곡에서 ±1 이동
    private fun moveSong(direct: Int) {
        if (songs.isEmpty()) return

        if (nowPos + direct < 0) {
            Toast.makeText(this, "first song", Toast.LENGTH_SHORT).show()
            return
        }
        if (nowPos + direct >= songs.size) {
            Toast.makeText(this, "last song", Toast.LENGTH_SHORT).show()
            return
        }

        nowPos += direct

        progressJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null

        setPlayer(songs[nowPos])
    }

    private fun getPlayingSongPosition(songId: Int): Int {
        for (i in 0 until songs.size) {
            if (songs[i].id == songId) return i
        }
        return 0
    }

    //플레이어에 곡 세팅 (안전 가드 포함)
    private fun setPlayer(song: Song) {
        binding.songActivityTitle.text  = song.title ?: ""
        binding.songActivitySinger.text = song.singer ?: ""

        binding.songStartTime.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTime.text   = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)

        // 커버 이미지 널 가드 (기본 이미지로 대체)
        val cover = song.coverImg
        if (cover != null) {
            binding.songActivityTitleImgIv.setImageResource(cover)
        } else {
            binding.songActivityTitleImgIv.setImageResource(R.drawable.img_album_exp2)
        }

        // SeekBar 초기화 (0~100%)
        binding.songProgress.progress =
            if (song.playTime > 0) ((song.second.toFloat() / song.playTime) * 100).toInt() else 0

        // 음원 리소스 확인
        val musicRes = resources.getIdentifier(song.music, "raw", packageName)
        if (musicRes == 0) {
            Toast.makeText(this, "오디오 리소스를 찾을 수 없습니다: ${song.music}", Toast.LENGTH_SHORT).show()
            setPlayerStatus(false)
            return
        }

        // MediaPlayer 생성 (널 가드)
        mediaPlayer?.release()
        val created = MediaPlayer.create(this, musicRes)
        if (created == null) {
            Toast.makeText(this, "플레이어 초기화 실패", Toast.LENGTH_SHORT).show()
            setPlayerStatus(false)
            return
        }
        mediaPlayer = created.apply {
            seekTo(song.second * 1000)
            setOnCompletionListener {
                setPlayerStatus(false)
                moveSong(+1)
            }
        }

        // 좋아요 아이콘 (원한다면 visibility 토글 추가 가능)
        if (song.isLike) {
            binding.songActivityLikeOnImgIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.songActivityLikeImgIv.setImageResource(R.drawable.ic_my_like_off)
        }

        // 초기 상태는 항상 멈춤(Play만 보이도록)
        setPlayerStatus(false)
    }

    //재생/일시정지 및 버튼 토글
    private fun setPlayerStatus(isPlaying: Boolean) {
        if (songs.isNotEmpty()) songs[nowPos].isPlaying = isPlaying

        if (isPlaying) {
            binding.songActivityPlayPlayerImgIv.visibility = View.GONE
            binding.songActivityPausePlayerImgIv.visibility = View.VISIBLE
            mediaPlayer?.start()
            startProgressUpdates()
        } else {
            binding.songActivityPlayPlayerImgIv.visibility = View.VISIBLE
            binding.songActivityPausePlayerImgIv.visibility = View.GONE
            if (mediaPlayer?.isPlaying == true) mediaPlayer?.pause()
            progressJob?.cancel()
        }
    }

    //진행바/시간 갱신 루프
    private fun startProgressUpdates() {
        progressJob?.cancel()

        progressJob = lifecycleScope.launch {
            while (isActive) {
                val mp = mediaPlayer
                if (songs.isNotEmpty() && songs[nowPos].isPlaying && mp != null) {
                    val posMs = mp.currentPosition
                    val durMs = songs[nowPos].playTime * 1000

                    val progressPercent =
                        if (durMs > 0) ((posMs.toFloat() / durMs) * 100).toInt() else 0
                    binding.songProgress.progress = progressPercent

                    val sec = posMs / 1000
                    binding.songStartTime.text = String.format("%02d:%02d", sec / 60, sec % 60)
                }
                delay(50)
            }
        }
    }
}
