package com.example.myapplication

import android.os.Bundle
import android.renderscript.ScriptGroup
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.databinding.FragmentHomeBinding
import com.google.gson.Gson

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var albumDatas=ArrayList<Album>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        albumDatas.apply{
            add(Album(title="Butter", singer = "방탄소년단(BTS)", R.drawable.img_album_exp))
            add(Album(title = "Lilac","아이유(IU)",R.drawable.img_album_exp2))
            add(Album("Next Level","에스파(AESPA)",R.drawable.img_album_exp3))
            add(Album("Boy with Luv","방탄소년단(BTS)",R.drawable.img_album_exp4))
            add(Album("BBoom BBoom","모모랜드(MOMOLAND",R.drawable.img_album_exp5))
            add(Album("Weekend","태연(Tea Yeon)",R.drawable.img_album_exp6))
        }

        val albumRVAdapter= AlbumRVAdapter(albumDatas)
        binding.homeTodayMusicAlbumRv.adapter=albumRVAdapter
        binding.homeTodayMusicAlbumRv.layoutManager= LinearLayoutManager(context,
            LinearLayoutManager.HORIZONTAL,false)

        albumRVAdapter.setMyItemClickListner(object : AlbumRVAdapter.MyItemClickListner{
            override fun onItemClick(album: Album) {
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, AlbumFragment().apply {
                        arguments= Bundle().apply {
                            val gson= Gson()
                            val albumJson=gson.toJson(album)
                            putString("album",albumJson)
                        }
                    })
                    .commitAllowingStateLoss()
            }
        })
        
        val adapter = PannelVPAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        adapter.addFragment(PannelFragment())
        binding.homePannelVp.adapter = adapter
        binding.homePannelVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
