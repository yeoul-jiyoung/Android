package com.example.myapplication

data class LockerAlbum(
    var title:String?="",
    var singer:String?="",
    var info:String?="",
    var coverImg:Int?=null,
    var songs:ArrayList<Song>?=null
)
