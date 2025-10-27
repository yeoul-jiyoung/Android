package com.example.myapplication

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.SongActivityBinding
import com.google.gson.Gson
import java.util.Timer

class SongActivity : AppCompatActivity() {

    lateinit var binding: SongActivityBinding
    lateinit var timer: Timer
    private var mediaPlayer: MediaPlayer? = null
    private var gson: Gson = Gson()

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SongActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPlayList()
        initSong()
        initClickListner()

    }


    override fun onPause() {
        super.onPause()


        songs[nowPos].second = ((binding.songProgress.progress * songs[nowPos].playTime)/100)/1000
        songs[nowPos].isPlaying = false
        setPlayerStatus(false)

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() // 에디터

        val songJson=gson.toJson(songs[nowPos])
        editor.putInt("songId",songs[nowPos].id)

        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun initPlayList(){
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }
    private fun initClickListner(){
        binding.songActivityArrowDown.setOnClickListener {
            finish()
        }

        binding.songActivityNextPlayerImgIv.setOnClickListener {
            moveSong(+1)
        }

        binding.songActivityPreviousPlayerImgIv.setOnClickListener {
            moveSong(-1)
        }
    }

    private fun initSong(){
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId",0)

        nowPos = getPlayingSongPosition(songId)

        Log.d("now Song ID",songs[nowPos].id.toString())

        startTimer()
        setPlayer(songs[nowPos])
    }

    private fun moveSong(direct:Int){
        if(nowPos+direct<0){
            Toast.makeText(this,"first song", Toast.LENGTH_SHORT).show()
            return
        }
        if(nowPos+direct>=songs.size){
            Toast.makeText(this,"last song", Toast.LENGTH_SHORT).show()
        }
        nowPos+=direct

        timer.interrupt()
        startTimer()

        mediaPlayer?.release()
        mediaPlayer = null

        setPlayer(songs[nowPos])
    }

    private fun getPlayingSongPosition(songId: Int): Int{
        for (i in 0 until songs.size){
            if (songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    private fun setPlayer(song: Song){
        binding.songActivityTitle.text = song.title
        binding.songActivitySinger.text = song.singer
        binding.songStartTime.text = String.format("%02d:%02d",song.second / 60, song.second % 60)
        binding.songEndTime.text = String.format("%02d:%02d",song.playTime / 60, song.playTime % 60)
        binding.songActivityTitleImgIv.setImageResource(song.coverImg!!)
        binding.songProgress.progress = (song.second * 1000 / song.playTime)
//        val music=resources.getIdentifier(song.music,"raw",this.packageName)
//        mediaPlayer= MediaPlayer.create(this.music)
        setPlayerStatus(song.isPlaying)

        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)

        if (song.isLike){
            binding.songActivityLikeOnImgIv.setImageResource(R.drawable.ic_my_like_on)
        } else{
            binding.songActivityLikeImgIv.setImageResource(R.drawable.ic_my_like_off)
        }

        setPlayerStatus(song.isPlaying)

    }

    private fun setPlayerStatus (isPlaying : Boolean){
        songs[nowPos].isPlaying = isPlaying
        timer.isPlaying = isPlaying

        if(isPlaying){
            binding.songActivityPlayPlayerImgIv.visibility = View.GONE
            binding.songActivityPausePlayerImgIv.visibility = View.VISIBLE
            mediaPlayer?.start()
        } else {
            binding.songActivityPlayPlayerImgIv.visibility = View.VISIBLE
            binding.songActivityPausePlayerImgIv.visibility = View.GONE
            if(mediaPlayer?.isPlaying == true){
                mediaPlayer?.pause()
            }
        }

    }

    private fun startTimer(){
        timer = Timer(songs[nowPos].playTime,songs[nowPos].isPlaying)
        timer.start()
    }

    inner class Timer(private val playTime: Int,var isPlaying: Boolean = true):Thread(){

        private var second : Int = 0
        private var mills: Float = 0f

        override fun run() {
            super.run()
            try {
                while (true){

                    if (second >= playTime){
                        break
                    }

                    if (isPlaying){
                        sleep(50)
                        mills += 50

                        runOnUiThread {
                            binding.songProgress.progress = ((mills / playTime)*100).toInt()
                        }
                    }
                }
            }catch (e: InterruptedException){
                Log.d("Song","쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }
}