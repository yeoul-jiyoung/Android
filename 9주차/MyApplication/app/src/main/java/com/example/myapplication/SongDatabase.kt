package com.example.myapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities=[Song::class,User::class,LikedTable::class, Album::class,Like::class],version=2)
abstract class SongDatabase: RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun likedSongDao(): LikedSongDao
    abstract fun userDao(): UserDao
    abstract fun albumDao(): AlbumDao
    companion object{
        private var instance: SongDatabase?=null

        @Synchronized
        fun getInstance(context: Context): SongDatabase?{
            if(instance==null){
                synchronized(SongDatabase::class){
                    instance= Room.databaseBuilder(
                        context.applicationContext,
                        SongDatabase::class.java,
                        "song-database"
                    ).allowMainThreadQueries().build()
                }
            }
            return instance
        }
    }
}