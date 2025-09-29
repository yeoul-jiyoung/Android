package com.example.myapplication

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 각 감정 클릭 이벤트 설정
        setEmotionClick(
            imageView = binding.ivHappy,
            textView = binding.tvHappymention,
            message = "행복을 선택했어요!",
            highlightColor = ContextCompat.getColor(this, R.color.happy_color)
        )

        setEmotionClick(
            imageView = binding.ivGood,
            textView = binding.tvGoodmention,
            message = "좋음을 선택했어요!",
            highlightColor = ContextCompat.getColor(this, R.color.good_color)
        )

        setEmotionClick(
            imageView = binding.ivSoso,
            textView = binding.tvSosomention,
            message = "평범한 하루를 선택했어요!",
            highlightColor = ContextCompat.getColor(this, R.color.soso_color)
        )

        setEmotionClick(
            imageView = binding.ivAnxious,
            textView = binding.tvAnxiousmention,
            message = "불안을 선택했어요!",
            highlightColor = ContextCompat.getColor(this, R.color.anxious_color)
        )

        setEmotionClick(
            imageView = binding.ivAnger,
            textView = binding.tvAngermention,
            message = "화남을 선택했어요!",
            highlightColor = ContextCompat.getColor(this, R.color.anger_color)
        )
    }

    // 클릭 이벤트 함수
    private fun setEmotionClick(
        imageView: ImageView,
        textView: TextView,
        message: String,
        highlightColor: Int
    ) {
        imageView.setOnClickListener {
            textView.setTextColor(highlightColor)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
