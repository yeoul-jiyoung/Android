package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentLockerTracksBinding

class LockerRVAdapter (private val lockList: ArrayList<SavedSong>): RecyclerView.Adapter<LockerRVAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): LockerRVAdapter.ViewHolder {
        val binding: FragmentLockerTracksBinding= FragmentLockerTracksBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)

        return ViewHolder(binding)
    }

    interface MyItemClickListener{
        fun onRemoveLocker(positon:Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener=itemClickListener
    }
    fun addItem(savedsong: SavedSong){
        lockList.add(savedsong)
        notifyDataSetChanged()
    }

    fun removeItem(positon:Int){
        lockList.removeAt(positon)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: LockerRVAdapter.ViewHolder, position: Int) {
        holder.bind(lockList[position])
        holder.binding.lockerTrackMoreImg.setOnClickListener { {mItemClickListener.onRemoveLocker(position)} }
    }

    override fun getItemCount(): Int = lockList.size

    inner class ViewHolder(val binding: FragmentLockerTracksBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(savedsong: SavedSong){
            binding.itemLockerTitleTv.text=savedsong.title
            binding.itemLockerSingerTv.text=savedsong.singer
            binding.lockerTrackImg.setImageResource(savedsong.coverImg!!)
    }

    }
}