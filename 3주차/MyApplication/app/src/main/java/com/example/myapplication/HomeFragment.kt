package com.example.myapplication;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.databinding.MainActivityBinding

class HomeFragment: Fragment() {
    lateinit var binding: MainActivityBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= MainActivityBinding.inflate(inflater,container,false)

        val pannelAdapter= PannelVPAdapter(fragment=this)
        pannelAdapter.addFragment(PannelFragment())
        binding.homePannelVp.adapter=pannelAdapter
        binding.homePannelVp.orientation= ViewPager2.ORIENTATION_HORIZONTAL

        return binding.root
    }
}
