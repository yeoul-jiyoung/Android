package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemAlbumTrackBinding

class SongRVAdapter(private val songList: ArrayList<Song>): RecyclerView.Adapter<SongRVAdapter.ViewHolder>(){

    interface MyItemClickListener{
        fun onSongClick(song: Song)
    }
    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ItemAlbumTrackBinding= ItemAlbumTrackBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup,false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(songList[position])
    }

    override fun getItemCount(): Int = songList.size

    inner class ViewHolder(val binding:ItemAlbumTrackBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(song:Song){
            binding.itemAlbumTrackTitleTv.text=song.title
            binding.itemAlbumTrackSingerTv.text=song.singer

        }
    }
}