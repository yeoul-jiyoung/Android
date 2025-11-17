package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemLockerAlbumBinding

class LockerAlbumRVAdapter(private val albumList:ArrayList<LockerAlbum>): RecyclerView.Adapter<LockerAlbumRVAdapter.ViewHolder> (){
    override fun onCreateViewHolder(viewGroup: ViewGroup,viewType:Int): LockerAlbumRVAdapter.ViewHolder{
        val binding: ItemLockerAlbumBinding= ItemLockerAlbumBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LockerAlbumRVAdapter.ViewHolder, position: Int) {
        holder.bind(albumList[position])
    }

    override fun getItemCount(): Int = albumList.size

    inner class ViewHolder(val binding: ItemLockerAlbumBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(lockeralbum: LockerAlbum){
            binding.itemAlbumTitleTv.text=lockeralbum.title
            binding.itemAlbumSingerTv.text=lockeralbum.singer
            //binding.itemAlbumMusicTitleInfoTv.text=lockeralbum.info
            binding.itemAlbumImgIv.setImageResource(lockeralbum.coverImg!!)
        }
    }
}