package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import kotlin.math.PI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumFragment: Fragment() {
    private var _binding: FragmentAlbumBinding? = null

    private var songDatas = ArrayList<Song>()
    private val binding get() = _binding!!
    private var gson: Gson = Gson()

    private val information = arrayListOf("수록곡", "상세정보", "영상")

    private var isLiked: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)

        // Home에서 넘어온 데이터 받아오기
        val albumJson = arguments?.getString("album")
        val album = gson.fromJson(albumJson, Album::class.java)

        // 앨범 기본 UI 세팅 (이미지, 제목, 가수)
        setInit(album)

        // 좋아요 초기 상태는 DB에서 코루틴으로 조회
        viewLifecycleOwner.lifecycleScope.launch {
            val liked = withContext(Dispatchers.IO) {
                isLikedAlbumBlocking(album.id)
            }
            isLiked = liked

            if (liked) {
                binding.songLockerLike.setImageResource(R.drawable.ic_my_like_on)
            } else {
                binding.songLockerLike.setImageResource(R.drawable.ic_my_like_off)
            }

            // 좋아요 버튼 클릭 리스너 설정
            setOnClickListeners(album)
        }

        binding.songLockerArrowImgIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, HomeFragment())
                .commitAllowingStateLoss()
        }

        binding.albumContentVp.adapter = AlbumVPAdapter(this)

        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp) { tab, position ->
            tab.text = information[position]
        }.attach()

        return binding.root
    }

    private fun setInit(album: Album) {
        binding.songLockerTitleImgIv.setImageResource(album.coverImg!!)
        binding.songLockerTitle.text = album.title.toString()
        binding.songLockerSinger.text = album.singer.toString()
        // 좋아요 아이콘 관련 로직은 코루틴에서 isLiked 설정 후 처리
    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf?.getInt("jwt", 0) ?: 0
    }

    // ----- DB 블로킹 호출용 함수들 (반드시 IO 스레드에서만 호출) -----

    // 앨범에 사용자가 좋아요 클릭 여부를 확인하기 위한 함수 (blocking)
    private fun isLikedAlbumBlocking(albumId: Int): Boolean {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getJwt()

        val likeId = songDB.albumDao().isLikedAlbum(userId, albumId)
        return likeId != null
    }

    // 앨범을 좋아요 눌렀을 때 LikeTable에 정보를 추가해 주는 함수 (blocking)
    private fun likeAlbumBlocking(userId: Int, albumId: Int) {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val like = Like(userId, albumId)

        songDB.albumDao().likeAlbum(like)
    }

    // 좋아요 취소 함수 (blocking)
    private fun disLikedAlbumBlocking(albumId: Int) {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getJwt()

        songDB.albumDao().disLikedAlbum(userId, albumId)
    }

    // ----- UI + 코루틴 연결 부분 -----

    private fun setOnClickListeners(album: Album) {
        val userId = getJwt()

        binding.songLockerLike.setOnClickListener {
            if (isLiked) {
                // UI 먼저 업데이트
                binding.songLockerLike.setImageResource(R.drawable.ic_my_like_off)
                isLiked = false

                // DB 작업은 IO 스레드에서
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    disLikedAlbumBlocking(album.id)
                }
            } else {
                binding.songLockerLike.setImageResource(R.drawable.ic_my_like_on)
                isLiked = true

                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    likeAlbumBlocking(userId, album.id)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
