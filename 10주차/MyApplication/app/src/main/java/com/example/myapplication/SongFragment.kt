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
            add(Song(id=0, title = "Butter", singer = "방탄소년단(BTS)"))
            add(Song(id=1,title = "Lilac", singer ="아이유(IU)"))
            add(Song(id=2,title="Next Level", singer ="에스파(AESPA)"))
            add(Song(id=3,title="Boy with Luv", singer ="방탄소년단(BTS)"))
            add(Song(id=4,title="BBoom BBoom", singer ="모모랜드(MOMOLAND"))
            add(Song(id=5,title="Weekend", singer ="태연(Tea Yeon)"))
            add(Song(id=6,title = "Butter", singer = "방탄소년단(BTS)"))
            add(Song(id=7,title = "Lilac", singer ="아이유(IU)"))
            add(Song(id=8,title="Next Level", singer ="에스파(AESPA)"))
            add(Song(id=9,title="Boy with Luv", singer ="방탄소년단(BTS)"))
            add(Song(id=10,title="Weekend", singer ="태연(Tea Yeon)"))
            add(Song(id=11,title = "Butter", singer = "방탄소년단(BTS)"))
            add(Song(id=12,title = "Lilac", singer ="아이유(IU)"))
            add(Song(id=13,title="Next Level", singer ="에스파(AESPA)"))
            add(Song(id=14,title="Boy with Luv", singer ="방탄소년단(BTS)"))
            add(Song(id=15,title="BBoom BBoom", singer ="모모랜드(MOMOLAND"))
            add(Song(id=16,title="Weekend", singer ="태연(Tea Yeon)"))
            add(Song(id=17,title = "Butter", singer = "방탄소년단(BTS)"))
            add(Song(id=18,title = "Lilac", singer ="아이유(IU)"))
            add(Song(id=19,title="Next Level", singer ="에스파(AESPA)"))
            add(Song(id=20,title="Boy with Luv", singer ="방탄소년단(BTS)"))
            add(Song(id=21,title="Weekend", singer ="태연(Tea Yeon)"))
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