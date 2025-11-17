package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LockerAlbumTable")
data class LockerAlbum(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var title: String = "",
    var singer: String = "",
    var coverImg: Int? = null,
    var songs:ArrayList<Song>?=null,
    var isLike: Boolean = false
)
