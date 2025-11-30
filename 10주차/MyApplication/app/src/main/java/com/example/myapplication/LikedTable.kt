package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="LikedSongTable")
data class LikedTable(
    val userId:Int,
    val songId: Int
){
    @PrimaryKey(autoGenerate = true)var id:Int=0

}
