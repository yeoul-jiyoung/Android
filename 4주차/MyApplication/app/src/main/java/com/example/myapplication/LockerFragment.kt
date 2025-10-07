package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentLockerBinding
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {
    private var _binding: FragmentLockerBinding?=null
    private var SavedSongDatas=ArrayList<SavedSong>()
    private val binding get()=_binding!!
    private val information = arrayListOf("저장한곡", "음악파일", "저장앨범")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLockerBinding.inflate(inflater, container, false)

        SavedSongDatas.apply{
            add(SavedSong(title="Butter", singer = "방탄소년단(BTS)", R.drawable.img_album_exp))
            add(SavedSong(title = "Lilac","아이유(IU)",R.drawable.img_album_exp2))
            add(SavedSong("Next Level","에스파(AESPA)",R.drawable.img_album_exp3))
            add(SavedSong("Boy with Luv","방탄소년단(BTS)",R.drawable.img_album_exp4))
            add(SavedSong("BBoom BBoom","모모랜드(MOMOLAND",R.drawable.img_album_exp5))
            add(SavedSong("Weekend","태연(Tea Yeon)",R.drawable.img_album_exp6))
            add(SavedSong(title="Butter", singer = "방탄소년단(BTS)", R.drawable.img_album_exp))
            add(SavedSong(title = "Lilac","아이유(IU)",R.drawable.img_album_exp2))
            add(SavedSong("Next Level","에스파(AESPA)",R.drawable.img_album_exp3))
            add(SavedSong("Boy with Luv","방탄소년단(BTS)",R.drawable.img_album_exp4))
            add(SavedSong("BBoom BBoom","모모랜드(MOMOLAND",R.drawable.img_album_exp5))
            add(SavedSong("Weekend","태연(Tea Yeon)",R.drawable.img_album_exp6))
        }

        val lockerRVAdapter= LockerRVAdapter(SavedSongDatas)
        binding.lockerSongRv.adapter=lockerRVAdapter
        binding.lockerSongRv.layoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)

        

        val lockerAdapter = LockerVPAdapter(this)
        binding.lockerContentVp.adapter = lockerAdapter

        TabLayoutMediator(binding.lockerContentTb, binding.lockerContentVp) { tab, position ->
            tab.text = information[position]
        }.attach()

        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}