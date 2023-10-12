package com.example.zipextectorapp.Activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.zipextectorapp.Adapters.ImageSliderAdapter
import com.example.zipextectorapp.Utills.sendImageList
import com.example.zipextectorapp.databinding.ActivityDisplayAllDataBinding
import com.example.zipextractor.model.MainEntity


class DisplayAllData : AppCompatActivity() {
    var sliderImagesList: ArrayList<MainEntity> = ArrayList()
    private lateinit var binding: ActivityDisplayAllDataBinding
    private  var imagesAdapter: ImageSliderAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayAllDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imagepath = intent.getStringExtra("position")
        Log.d("imagePath", "path: $imagepath")
        Glide.with(this).load(imagepath).into(binding.viewPager2ID)
        binding.imagesbackId.setOnClickListener {
            finish()
        }

          /*   sendImageList = {
             sliderImagesList=it
             Log.d("checkImageList", " getListSize : ${sliderImagesList.size}: ")
                 imagesAdapter =ImageSliderAdapter( sliderImagesList,  binding.viewPager2ID,this)
                 Log.d("checkImageList", " finalList : ${sliderImagesList.size}: ")
                 binding.viewPager2ID.adapter = imagesAdapter
                 binding.viewPager2ID.offscreenPageLimit = 3

       }*/

    }

    override fun onResume() {
        super.onResume()
    }

    }

