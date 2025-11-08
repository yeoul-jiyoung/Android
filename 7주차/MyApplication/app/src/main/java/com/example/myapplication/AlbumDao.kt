package com.example.myapplication

import androidx.room.*

@Dao
interface AlbumDao {
    @Insert
    fun insert(album: Album)
    @Update
    fun update(album: Album)
    @Delete
    fun delete(album: Album)

    @Query("SELECT * FROM AlbumTable")
    fun getAlbums(): List<Album>
    @Query("SELECT * FROM AlbumTable WHERE id= :id")
    fun getAlbum(id: Int): Album

    @Insert
    fun likeAlbum(like: Like)
    @Query("SELECT id FROM LikeTable WHERE userId= :userId AND albumId= :albumId")
    fun isLikedAlbum(userId: Int, albumId: Int): Int?

    @Query("DELETE FROM LikeTable WHERE userId= :userId AND albumId= :albumId")
    fun disLikedAlbum(userId: Int, albumId: Int)

    @Query("SELECT AT.* FROM LikeTable as LT LEFT JOIN AlbumTable as AT On LT.albumId= AT.id WHERE LT.userId= :userId")
    fun getLikedAlbums(userId: Int): List<Album>
}
