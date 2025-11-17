package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import kotlin.math.PI

class AlbumFragment: Fragment() {
    private var _binding: FragmentAlbumBinding? = null

    private var songDatas= ArrayList<Song>()
    private val binding get() = _binding!!
    private var gson: Gson =Gson()

    private val information = arrayListOf("수록곡", "상세정보", "영상")

    private var isLiked: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)

        //Home에서 넘어온 데이터 받아오기
        val albumJson=arguments?.getString("album")
        val album =gson.fromJson(albumJson,Album::class.java)

        //Home에서 넘어온 데이터를 반영
        isLiked=isLikedAlbum(album.id) //isLiked 초기 설정
        setInit(album)
        setOnClickListeners(album)

        //val albumAdapter = AlbumVPAdapter(this)

        binding.albumContentVp.adapter = AlbumVPAdapter(this)

        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp) {
        tab, position -> tab.text = information[position]
        }.attach()

        return binding.root
    }

    private fun setInit(album: Album){
        binding.songLockerTitleImgIv.setImageResource(album.coverImg!!)
        binding.songLockerTitle.text=album.title.toString()
        binding.songLockerSinger.text=album.singer.toString()

        //앨범에서 좋아요 버튼 클릭시 이미지 교체
        if(isLikedAlbum(album.id)){
            binding.songLockerLike.setImageResource(R.drawable.ic_my_like_on)
            isLiked=true
            }else{
            binding.songLockerLike.setImageResource(R.drawable.ic_my_like_off)
            isLiked=false
        }
    }
    private fun getJwt(): Int{
        val spf = activity?.getSharedPreferences("song", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getInt("jwt", 0)
    }

    //앨범을 좋아요 눌렀을 때 라이크 테이블에 정보를 추가해 주는 함수
    private fun likeAlbum(userId:Int, albumId:Int) {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val like = Like(userId, albumId)

        songDB.albumDao().likeAlbum(like)
    }

    //앨범에 사용자가 좋아요 클릭 여부를 확인하기 위한 함수
    private fun isLikedAlbum(albumId: Int): Boolean {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getJwt()

        val likeId=songDB.albumDao().isLikedAlbum(userId, albumId)
        return likeId != null
    }
    private fun disLikedAlbum(albumId: Int) {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getJwt()

        val likeId=songDB.albumDao().isLikedAlbum(userId, albumId)
    }

    private fun setOnClickListeners(album: Album) {
        val userId = getJwt()
        binding.songLockerLike.setOnClickListener {
            if (isLiked) {
                binding.songLockerLike.setImageResource(R.drawable.ic_my_like_off)
                disLikedAlbum(album.id)
                isLiked=false
            } else {
                binding.songLockerLike.setImageResource(R.drawable.ic_my_like_on)
                likeAlbum(userId, album.id)
                isLiked=true
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
