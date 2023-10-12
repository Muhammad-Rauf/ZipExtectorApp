package com.example.zipextectorapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zipextectorapp.R
import com.example.zipextractor.model.MainEntity


class ImagePagerAdapter(private val myContext: Context, private val imagesList: List<MainEntity>) : RecyclerView.Adapter<ImagePagerAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageView = itemView.findViewById<ImageView>(R.id.allimageViewID)


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImagePagerAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_pager, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: ImagePagerAdapter.MyViewHolder, position: Int) {
        val currentImage = imagesList[position]
        Glide.with(holder.itemView.context).load(currentImage.filePath)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
       return  imagesList.size
    }



}