package com.example.zipextectorapp.Activities

import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.example.zipextectorapp.databinding.ActivityPlayVideosBinding

class PlayVideos : AppCompatActivity() {
    private lateinit var binding: ActivityPlayVideosBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayVideosBinding.inflate(layoutInflater)
        setContentView(binding.root)
       var  selectVideo = intent.getStringExtra("video")
        if (selectVideo != null) {
            if (selectVideo.endsWith(".mp4") || selectVideo.endsWith("mk4")) {
                val mediaController = MediaController(this)
                mediaController.setAnchorView(binding.videoView)
                binding.videoView.setMediaController(mediaController)
                binding.videoView.setVideoPath(selectVideo)
                binding.videoView.start()
            }
            else  {
                val mediaController = MediaController(this)
                mediaController.setAnchorView(binding.videoView)
                binding.videoView.setMediaController(mediaController)
                binding.videoView.setVideoPath(selectVideo)
                binding.videoView.start()
            }
        }


        binding.playVideosbackId.setOnClickListener {
            finish()
        }

    }

}