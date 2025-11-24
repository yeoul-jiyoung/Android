package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentLockerMusicfileBinding
import com.example.myapplication.databinding.FragmentVideoBinding

class MusicFileFragment: Fragment() {

    private var _binding: FragmentLockerMusicfileBinding?=null
    private val binding get()=_binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLockerMusicfileBinding.inflate(inflater, container, false)

        return binding.root
    }
}