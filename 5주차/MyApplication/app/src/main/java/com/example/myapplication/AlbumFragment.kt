package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

class AlbumFragment: Fragment() {

    private var _binding: FragmentAlbumBinding? = null

    private var songDatas= ArrayList<Song>()
    private val binding get() = _binding!!
    private var gson: Gson =Gson()

    private val information = arrayListOf("수록곡", "상세정보", "영상")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)

        val albumJson=arguments?.getString("album")
        val album =gson.fromJson(albumJson,Album::class.java)
        setInit(album)

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
