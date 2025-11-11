//package com.example.myapplication
//
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//
//@Entity(tableName="SongTable")
//data class Song(
//    var title: String?="",
//    var singer: String?="",
//    var second:Int=0,
//    var playTime:Int=0,
//    var isPlaying:Boolean=false,
//    var music:String="",
//    var coverImg:Int?=null,
//    var isLike:Boolean=false
//){
//    @PrimaryKey(autoGenerate=true)var id:Int=0
//}

// Firebase 데이터
package com.example.myapplication

data class Song(
    var title: String = "",
    var singer: String = "",
    var second: Int = 0,
    var playTime: Int = 0,
    var isPlaying: Boolean = false,
    var music: String = "",
    var coverImg: Int? = null, // Firebase는 Int? 타입을 직접 지원하지 않으므로, 이 방식은 잠재적 오류 가능성
    var isLike: Boolean = false,
    var id: Int = 0 // id 필드도 추가해주는 것이 좋음
) {
    // Firebase SDK가 객체를 역직렬화할 수 있도록 매개변수 없는 생성자를 추가
    constructor() : this("", "", 0, 0, false, "", null, false, 0)
}

