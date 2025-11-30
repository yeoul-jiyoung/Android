package com.example.myapplication
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.ArrayList
@Entity(tableName = "AlbumTable")
data class Album (
    @PrimaryKey
    var id: Int ,

    var title: String = "",
    var singer: String = "",
    //var isLike: Boolean = false,
    var coverImg: Int? = null
)
