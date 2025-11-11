package com.example.myapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentLockerSavedsongBinding

class SavedSongFragment : Fragment() {
    private var _binding: FragmentLockerSavedsongBinding? = null
    private val binding get()=_binding!!
    private val songs = arrayListOf<SavedSong>()
    //private lateinit var songDB: SongDatabase


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLockerSavedsongBinding.inflate(inflater, container, false)

        songs.apply{
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

        val adapter = LockerRVAdapter(songs)
        binding.lockerSavedSongRecyclerView.adapter = adapter
        binding.lockerSavedSongRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

//        adapter.setMyItemClickListener(object : LockerRVAdapter.MyItemClickListener {
//            override fun onRemoveLocker(positon: Int) {
//                adapter.removeItem(positon)
//            }
//        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
    }

    private fun initRecyclerview(){
        binding.lockerSavedSongRecyclerView.layoutManager= LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)

        val songRVAdapter= SavedSongRVAdapter()

        songRVAdapter.setMyItemClickListener(object : SavedSongRVAdapter.MyItemClickListener{
            override fun onRemoveSong(songId: Int) {
                //songDB.songDao().updateIsLikeById(false,songId)
            }
        })

        binding.lockerSavedSongRecyclerView.adapter=songRVAdapter
        //songRVAdapter.addSongs(songDB.songDao().getLikedSongs(true)as ArrayList<Song>)

    }
}