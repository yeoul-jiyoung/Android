package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentDetailBinding
import com.example.myapplication.databinding.FragmentSongBinding

class SongRVAdapter(private val songList: ArrayList<Song>): RecyclerView.Adapter<SongRVAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): SongRVAdapter.ViewHolder {
        val binding: FragmentSongBinding= FragmentSongBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup,false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongRVAdapter.ViewHolder, position: Int) {
        holder.bind(songList[position])
    }

    override fun getItemCount(): Int = songList.size

    inner class ViewHolder(val binding: FragmentSongBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(song:Song){
            binding.itemAlbumTitleTv.text=song.title
            binding.itemAlbumSingerTv.text=song.singer
        }
    }
}