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

    //앨범 프레그먼트에 들어갈 때 현재 사용자가 이 앨범에 좋아요를 눌렀는지 확인하는 함수
    @Query("SELECT id FROM LikeTable WHERE userId= :userId AND albumId= :albumId")
    fun isLikedAlbum(userId: Int, albumId: Int): Int?

    //좋아요를 취소하는 쿼리문
    @Query("DELETE FROM LikeTable WHERE userId= :userId AND albumId= :albumId")
    fun disLikedAlbum(userId: Int, albumId: Int)

    //보관함에서 유저를 구분하여 좋아요를 누른 앨범의 정보를 가져오는 쿼리문
    @Query("SELECT AT.* FROM LikeTable as LT LEFT JOIN AlbumTable as AT On LT.albumId= AT.id WHERE LT.userId= :userId")
    fun getLikedAlbums(userId: Int): List<Album>
}
