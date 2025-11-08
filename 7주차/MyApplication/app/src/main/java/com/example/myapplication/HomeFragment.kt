package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.databinding.FragmentHomeBinding
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val albumDatas = ArrayList<Album>()
    private lateinit var pannelAdapter: PannelVPAdapter

    /** 자동 슬라이드 코루틴 잡 */
    private var autoSlideJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        // ---- 오늘의 앨범 가로 RV 세팅 ----
        albumDatas.apply {
            add(Album(title = "Butter", singer = "방탄소년단(BTS)", coverImg = R.drawable.img_album_exp))
            add(Album(title = "Lilac", singer = "아이유(IU)", coverImg = R.drawable.img_album_exp2))
            add(Album(title = "Next Level", singer = "에스파(AESPA)", coverImg = R.drawable.img_album_exp3))
            add(Album(title = "Boy with Luv", singer = "방탄소년단(BTS)", coverImg = R.drawable.img_album_exp4))
            add(Album(title = "BBoom BBoom", singer = "모모랜드(MOMOLAND)", coverImg = R.drawable.img_album_exp5))
            add(Album(title = "Weekend", singer = "태연(Tae Yeon)", coverImg = R.drawable.img_album_exp6))
        }

        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        binding.homeTodayMusicAlbumRv.apply {
            adapter = albumRVAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        albumRVAdapter.setMyItemClickListner(object : AlbumRVAdapter.MyItemClickListner {
            override fun onItemClick(album: Album) {
                (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, AlbumFragment().apply {
                        arguments = Bundle().apply {
                            val gson = Gson()
                            putString("album", gson.toJson(album))
                        }
                    })
                    .commitAllowingStateLoss()
            }
        })

        //패널 ViewPager2 + Indicator 세팅
        pannelAdapter = PannelVPAdapter(childFragmentManager, viewLifecycleOwner.lifecycle).apply {
            addFragment(PannelFragment()) // 1
            addFragment(PannelFragment()) // 2
            addFragment(PannelFragment()) // 3
        }

        binding.homePannelVp.apply {
            adapter = pannelAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 1
        }

        // Indicator 연결 (adapter 이후에)
        binding.homePannelIndicator.setViewPager(binding.homePannelVp)
        // 어댑터 데이터 변경 시 인디케이터 갱신
        pannelAdapter.registerAdapterDataObserver(binding.homePannelIndicator.adapterDataObserver)
    }

    // 자동 슬라이드 제어
    override fun onResume() {
        super.onResume()
        startAutoSlide()
    }

    override fun onPause() {
        super.onPause()
        stopAutoSlide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoSlide()
        _binding = null
    }

    private fun startAutoSlide(intervalMs: Long = 3000L) {
        stopAutoSlide() // 중복 방지
        autoSlideJob = viewLifecycleOwner.lifecycleScope.launch {
            // adapter 세팅/페이지 수 확보 대기
            delay(100)
            val vp = binding.homePannelVp
            while (isActive) {
                delay(intervalMs)
                val count = pannelAdapter.itemCount
                if (count <= 1) continue
                val next = (vp.currentItem + 1) % count
                vp.setCurrentItem(next, true)
            }
        }
    }

    private fun stopAutoSlide() {
        autoSlideJob?.cancel()
        autoSlideJob = null
    }
}
