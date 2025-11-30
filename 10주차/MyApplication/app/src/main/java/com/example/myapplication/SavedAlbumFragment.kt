package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentLockerSavedalbumBinding
import com.example.myapplication.databinding.FragmentVideoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        lockerAlbumRVAdapter.setMyItemClickListener(
            object : LockerAlbumRVAdapter.MyItemClickListener {
                override fun onMoreClick(album: LockerAlbum, position: Int) {
                    val userId = getJwt()

                    // DB에서 Like 삭제
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        val db = SongDatabase.getInstance(requireContext())!!
                        db.albumDao().disLikedAlbum(userId, album.albumId)

                        // UI 갱신
                        withContext(Dispatchers.Main) {
                            albumDatas.removeAt(position)
                            lockerAlbumRVAdapter.notifyItemRemoved(position)
                        }
                    }
                }
            }
        )

        // DB에서 좋아요 한 앨범만 가져와서 albumData 채우기
        viewLifecycleOwner.lifecycleScope.launch {
            // IO 스레드에서 DB 쿼리
            val likedAlbums = withContext(Dispatchers.IO) {
                val songDB = SongDatabase.getInstance(requireContext())!!
                val userId = getJwt()
                songDB.albumDao().getLikedAlbums(userId)   // List<Album>
            }

            // 메인 스레드에서 UI용 리스트 갱신
            albumDatas.clear()
            albumDatas.addAll(
                likedAlbums.map { album ->
                    // LockerAlbum(title, singer, info, coverImg)
                    LockerAlbum(
                        albumId = album.id,              // 삭제할 때 쓸 ID
                        title = album.title,
                        singer = album.singer,
                        coverImg = album.coverImg
                    )
                }
            )
            lockerAlbumRVAdapter.notifyDataSetChanged()
        }

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

    // 로그인할 때 저장해 둔 jwt(=userId) 가져오는 함수
    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf?.getInt("jwt", 0) ?: 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}