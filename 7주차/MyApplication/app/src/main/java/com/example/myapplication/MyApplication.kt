package com.example.myapplication

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.database.database
//import com.google.firebase.database.ktx.database
//import com.google.firebase.ktx.Firebase
import kotlin.io.path.exists

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 앱이 처음 시작될 때 한 번만 노래 데이터를 Firebase에 저장
        val database = Firebase.database
        val songsRef = database.getReference("songs")

        // 데이터가 이미 있는지 확인 후, 없으면 초기 데이터 추가
        songsRef.get().addOnSuccessListener { dataSnapshot ->
            if (!dataSnapshot.exists()) {
                val initialSongs = arrayListOf(
                    Song("Butter", "방탄소년단", 0, 164, false, "butter", R.drawable.img_album_exp, false,0),
                    Song("LILAC", "아이유 (IU)", 0, 214, false, "lilac", R.drawable.img_album_exp2, false,1),
                    Song("Next Level", "aespa", 0, 221, false, "next_level", R.drawable.img_album_exp3, false,2),
                    Song("Boy with Luv", "방탄소년단", 0, 220, false, "boy_with_luv", R.drawable.img_album_exp4, false,3),
                    Song("뿜뿜", "모모랜드", 0, 210, false, "bboom_bboom", R.drawable.img_album_exp5, false,4),
                    Song("Weekend", "태연", 0, 233, false, "weekend", R.drawable.img_album_exp6, false,5)
                )
                // Firebase는 고유 키를 자동으로 생성해 주므로, id를 키로 사용
                initialSongs.forEachIndexed { index, song ->
                    songsRef.child(index.toString()).setValue(song)
                }
            }
        }
    }
}
    