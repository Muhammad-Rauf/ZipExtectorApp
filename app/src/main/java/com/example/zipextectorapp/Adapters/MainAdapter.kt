package com.example.zipextectorapp.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zipextectorapp.Activities.DisplayAllData
import com.example.zipextectorapp.Activities.PlayVideos
import com.example.zipextectorapp.R
import com.example.zipextectorapp.Utills.showToast
import com.example.zipextractor.model.MainEntity

class MainAdapter(
    private val myContext: Context,
    private var allImageList: ArrayList<MainEntity>,
    private val listener: SelectionHandler
) : RecyclerView.Adapter<MainAdapter.GalleryViewHolder>() {

    var isSelectionMode: Boolean = false

    fun getSelectedItems(): ArrayList<MainEntity> {
        return allImageList.filter { it.multipleSelection } as ArrayList
    }

    fun getSelectedCount(): Int = getSelectedItems().size

    fun isAllSelected() = getSelectedCount() == itemCount


    inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView =
            itemView.findViewById(R.id.folderTile_id)
        val folderPath: TextView =
            itemView.findViewById(R.id.folderPath_id)
        val folderSize: TextView =
            itemView.findViewById(R.id.folderSize_id)
        val imagesIcon: ImageView =
            itemView.findViewById(R.id.folderIcon_id)
        val cardView: CardView = itemView.findViewById(R.id.carview_id)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainAdapter.GalleryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.galleryitem_layout, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainAdapter.GalleryViewHolder, position: Int) {
        val currentImage = allImageList[position]
        holder.titleText.text = currentImage.fileName
        holder.folderPath.text = currentImage.filePath
        holder.folderSize.text = currentImage.fileSize
        holder.cardView.setBackgroundColor(
            if (currentImage.multipleSelection) {
                Color.parseColor("#264087F5")
            } else {
                Color.parseColor("#ffffff")
            }
        )
        when (currentImage.fileType) {
            "images" -> {
                Glide.with(holder.itemView.context).load(currentImage.filePath)
                    .into(holder.imagesIcon)
            }
            "videos" -> {
                Glide.with(holder.itemView.context).load(currentImage.filePath)
                    .placeholder(R.drawable.videoplaceholder).into(holder.imagesIcon)
            }
            "audios" -> {
                holder.imagesIcon.setImageResource(R.drawable.musicicon)
            }
        }

        holder.cardView.setOnLongClickListener {
            isSelectionMode = true
            currentImage.multipleSelection = !currentImage.multipleSelection
            listener.onLongClick()
            notifyItemChanged(position)
            return@setOnLongClickListener true
        }

        holder.cardView.setOnClickListener(View.OnClickListener {
            if (isSelectionMode)
            {
                currentImage.multipleSelection = !currentImage.multipleSelection
                listener.onClick(position)
            } else
            {
                when (currentImage.fileType) {
                    "images" -> {
                       // sendImageList?.invoke(allImageList)
                        Log.d("checkImageList", " sendListSize : ${allImageList.size}: ")
                        myContext.showToast("You Click $position")
                        val intent= Intent(myContext, DisplayAllData::class.java)
                       intent.putExtra("position", currentImage.filePath)
                        myContext.startActivity(intent)

                    }
                    "videos" -> {
                        val intent= Intent(myContext, PlayVideos::class.java)
                        intent.putExtra("video", allImageList[position].filePath)
                        myContext.startActivity(intent)

                    }
                    "audios" -> {
                        val intent= Intent(myContext, PlayVideos::class.java)
                        intent.putExtra("video", allImageList[position].filePath)
                        myContext.startActivity(intent)

                    }
                }

            }
            notifyItemChanged(position)

        })
    }

    override fun getItemCount(): Int {
        return allImageList.size
    }

    fun selectAllItem(isSelectedAll: Boolean) {
        allImageList.forEach {
            it.multipleSelection = isSelectedAll
        }
        listener.onSelectAll(isSelectedAll)
        notifyDataSetChanged()
    }

    fun getZipSelectedItems(): ArrayList<MainEntity> {
        return allImageList.filter { it.multipleSelection } as ArrayList
    }

    interface SelectionHandler {
        fun onClick(position: Int)
        fun onLongClick()
        fun onSelectAll(isAllSelected: Boolean)
    }

}