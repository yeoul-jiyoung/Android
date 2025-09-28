package com.example.myapplication

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication.databinding.FragmentLockerMusicfileBinding

class LockerVPAdapter (fragment : Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int  = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> SavedSongFragment()
            else -> MusicFileFragment()
        }
    }
}