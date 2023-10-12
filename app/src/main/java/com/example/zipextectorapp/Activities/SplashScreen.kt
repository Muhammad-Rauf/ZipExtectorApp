package com.example.zipextectorapp.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.example.zipextectorapp.R
import com.example.zipextectorapp.databinding.ActivityMainBinding
import com.example.zipextectorapp.databinding.ActivitySplashScreenBinding


class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val text = "<font color=#2962FF>Archive </font> <font color=#000000>Files</font>"
        binding.splashTitleId.text = Html.fromHtml(text)
        Handler(Looper.getMainLooper()).postDelayed({
            var intent=  Intent(this,HomeScreen::class.java)
            startActivity(intent)
            finish()
            //  startActivity(Intent(this, MainActivity::class.java))
        }, 5000)
    }
}