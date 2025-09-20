package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageView
import android.widget.TextView
import android.graphics.Color
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setEmotionClick(
            imageViewId = R.id.iv_happy,
            textViewId = R.id.tv_happymention,
            message = "행복을 선택했어요!",
            highlightColor = Color.parseColor("yellow")
        )

        setEmotionClick(
            imageViewId = R.id.iv_good,
            textViewId = R.id.tv_goodmention,
            message = "좋음을 선택했어요!",
            highlightColor = Color.parseColor("blue")
        )

        setEmotionClick(
            imageViewId = R.id.iv_soso,
            textViewId = R.id.tv_sosomention,
            message = "평범한 하루를 선택했어요!",
            highlightColor = Color.parseColor("purple")
        )

        setEmotionClick(
            imageViewId = R.id.iv_anxious,
            textViewId = R.id.tv_anxiousmention,
            message = "불안을 선택했어요!",
            highlightColor = Color.parseColor("green")
        )

        setEmotionClick(
            imageViewId = R.id.iv_anger,
            textViewId = R.id.tv_angermention,
            message = "화남을 선택했어요!",
            highlightColor = Color.parseColor("red")
        )
    }

    private fun setEmotionClick(
        imageViewId: Int,
        textViewId: Int,
        message: String,
        highlightColor: Int
    ) {
        val imageView = findViewById<ImageView>(imageViewId)
        val textView = findViewById<TextView>(textViewId)

        imageView.setOnClickListener {
            textView.setTextColor(highlightColor)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
