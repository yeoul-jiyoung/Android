package com.example.myapplication

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.ArrayList

class PannelVPAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(/* fragmentManager = */ fragmentManager, /* lifecycle = */ lifecycle) {

    private val fragmentlist: ArrayList<Fragment> =ArrayList()

    override fun getItemCount(): Int = fragmentlist.size

    override fun createFragment(position: Int): Fragment =fragmentlist[position]

    fun addFragment(fragment: Fragment){
        fragmentlist.add(fragment)
        notifyItemInserted(fragmentlist.size-1)
    }
}