package com.example.myapplication

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LikedSongDao {
    @Insert
    fun insert(likedSong: LikedTable)

    @Query("DELETE FROM LikedSongTable WHERE userId = :userId AND songId = :songId")
    fun delete(userId: Int, songId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM LikedSongTable WHERE userId = :userId AND songId = :songId)")
    fun isLiked(userId: Int, songId: Int): Boolean

    @Query("""
        SELECT ST.* 
        FROM LikedSongTable AS LS 
        LEFT JOIN SongTable AS ST ON LS.songId = ST.id
        WHERE LS.userId = :userId
    """)
    fun getLikedSongs(userId: Int): List<Song>
}