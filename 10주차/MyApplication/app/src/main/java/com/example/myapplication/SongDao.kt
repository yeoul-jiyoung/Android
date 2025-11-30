package com.example.myapplication

import androidx.room.*
@Dao
interface SongDao {
    @Insert
    fun insert(song:Song)

    @Update
    fun update(song:Song)

    @Delete
    fun delete(song:Song)

    @Query("SELECT * FROM SongTable")
    fun getSongs():List<Song>

    @Query("SELECT * FROM SongTable WHERE id= :id")
    fun getSong(id:Int):Song


    //좋아요 업데이트를 위한 쿼리
    @Query("UPDATE SongTable SET isLike= :isLike WHERE id = :id")
    fun updateIsLikeById(isLike: Boolean,id: Int)

    @Query("SELECT * FROM SongTable WHERE isLike= :isLike")
    fun getLikedSongs(isLike: Boolean): List<Song>

}