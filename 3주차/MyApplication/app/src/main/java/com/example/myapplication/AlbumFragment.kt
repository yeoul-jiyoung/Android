package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.AlbumFragmentBinding

class AlbumFragment: Fragment() {
    lateinit var binding: AlbumFragmentBinding

    private val information = arrayListOf("수록곡", "상세정보", "영상")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= AlbumFragmentBinding.inflate(inflater,container,false)


        val albumAdapter= AlbumVPAdapter(fragment = this)
        binding.albumContentTp.adapter=albumAdapter
        return binding.root
    }
}