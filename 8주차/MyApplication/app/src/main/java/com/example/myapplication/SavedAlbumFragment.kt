package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentLockerSavedalbumBinding
import com.example.myapplication.databinding.FragmentVideoBinding

class SavedAlbumFragment: Fragment() {

    private var _binding: FragmentLockerSavedalbumBinding?=null
    private var albumDatas= ArrayList<LockerAlbum>()
    private val binding get()=_binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLockerSavedalbumBinding.inflate(inflater, container, false)

        val lockerAlbumRVAdapter= LockerAlbumRVAdapter(albumDatas)
        binding.lockerSavedSongRecyclerView.adapter=lockerAlbumRVAdapter
        binding.lockerSavedSongRecyclerView.layoutManager= LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)

//        albumDatas.apply{
//            add(LockerAlbum(title="Butter", singer = "방탄소년단(BTS)", info="2021.03.25 | 정규 | 댄스 팝",R.drawable.img_album_exp))
//            add(LockerAlbum(title="Lilac", singer = "아이유(IU)", info="2021.03.25 | 정규 | 댄스 팝",R.drawable.img_album_exp2))
//            add(LockerAlbum(title="Next Level", singer = "에스파(AESPA)", info="2021.03.25 | 정규 | 댄스 팝",R.drawable.img_album_exp3))
//            add(LockerAlbum(title="Boy with Luv", singer = "방탄소년단(BTS)", info="2021.03.25 | 정규 | 댄스 팝",R.drawable.img_album_exp4))
//            add(LockerAlbum(title="BBoom BBoom", singer = "모모랜드(MOMOLAND)", info="2021.03.25 | 정규 | 댄스 팝",R.drawable.img_album_exp5))
//            add(LockerAlbum(title="Weekend", singer = "태연(Tae Yeon)", info="2021.03.25 | 정규 | 댄스 팝",R.drawable.img_album_exp6))
//            add(LockerAlbum(title="Butter", singer = "방탄소년단(BTS)", info="2021.03.25 | 정규 | 댄스 팝",R.drawable.img_album_exp))
//            add(LockerAlbum(title="Lilac", singer = "아이유(IU)", info="2021.03.25 | 정규 | 댄스 팝",R.drawable.img_album_exp2))
//            add(LockerAlbum(title="Next Level", singer = "에스파(AESPA)", info="2021.03.25 | 정규 | 댄스 팝",R.drawable.img_album_exp3))
//            add(LockerAlbum(title="Boy with Luv", singer = "방탄소년단(BTS)", info="2021.03.25 | 정규 | 댄스 팝",R.drawable.img_album_exp4))
//            add(LockerAlbum(title="BBoom BBoom", singer = "모모랜드(MOMOLAND)", info="2021.03.25 | 정규 | 댄스 팝",R.drawable.img_album_exp5))
//            add(LockerAlbum(title="Weekend", singer = "태연(Tae Yeon)", info="2021.03.25 | 정규 | 댄스 팝",R.drawable.img_album_exp6))
//        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}