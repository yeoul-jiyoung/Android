package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentSongBinding

class SongFragment : Fragment() {

    private var _binding: FragmentSongBinding? = null
    private val binding get() = _binding!!
    private var song= ArrayList<Song>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSongBinding.inflate(inflater, container, false)

        song.apply {
            add(Song(title = "Butter", singer = "방탄소년단(BTS)"))
            add(Song(title = "Lilac", singer ="아이유(IU)"))
            add(Song(title="Next Level", singer ="에스파(AESPA)"))
            add(Song(title="Boy with Luv", singer ="방탄소년단(BTS)"))
            add(Song(title="BBoom BBoom", singer ="모모랜드(MOMOLAND"))
            add(Song(title="Weekend", singer ="태연(Tea Yeon)"))
            add(Song(title = "Butter", singer = "방탄소년단(BTS)"))
            add(Song(title = "Lilac", singer ="아이유(IU)"))
            add(Song(title="Next Level", singer ="에스파(AESPA)"))
            add(Song(title="Boy with Luv", singer ="방탄소년단(BTS)"))
            add(Song(title="Weekend", singer ="태연(Tea Yeon)"))
            add(Song(title = "Butter", singer = "방탄소년단(BTS)"))
            add(Song(title = "Lilac", singer ="아이유(IU)"))
            add(Song(title="Next Level", singer ="에스파(AESPA)"))
            add(Song(title="Boy with Luv", singer ="방탄소년단(BTS)"))
            add(Song(title="BBoom BBoom", singer ="모모랜드(MOMOLAND"))
            add(Song(title="Weekend", singer ="태연(Tea Yeon)"))
            add(Song(title = "Butter", singer = "방탄소년단(BTS)"))
            add(Song(title = "Lilac", singer ="아이유(IU)"))
            add(Song(title="Next Level", singer ="에스파(AESPA)"))
            add(Song(title="Boy with Luv", singer ="방탄소년단(BTS)"))
            add(Song(title="Weekend", singer ="태연(Tea Yeon)"))
        }

        val SongRVAdapter= SongRVAdapter(song)
        binding.albumSongRecyclerView.adapter=SongRVAdapter
        binding.albumSongRecyclerView.layoutManager= LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)

        SongRVAdapter.setMyItemClickListener(object : SongRVAdapter.MyItemClickListener{
            override fun onSongClick(song: Song) {
                //(activity as MainActivity).updateMiniPlayer(song)
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}