package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentLockerSavedalbumBinding
import com.example.myapplication.databinding.FragmentVideoBinding

class SavedAlbumFragment: Fragment() {

    private var _binding: FragmentLockerSavedalbumBinding?=null
    private val binding get()=_binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLockerSavedalbumBinding.inflate(inflater, container, false)

        return binding.root
    }
}