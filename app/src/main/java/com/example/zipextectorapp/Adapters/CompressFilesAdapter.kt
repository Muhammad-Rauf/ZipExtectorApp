package com.example.zipextectorapp.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.zipextectorapp.Models.CompressZipModel
import com.example.zipextectorapp.R
import com.example.zipextractor.model.MainEntity

class CompressFilesAdapter(val myContext: Context, var compressList: ArrayList<CompressZipModel>, val listener: onSelectionHandling): RecyclerView.Adapter<CompressFilesAdapter.CompressViewHolder>() {
    var count: Int = 0
    private var selection = false

    inner class CompressViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val compressFileName: TextView = itemView.findViewById(R.id.appName_id)
        val compressFilSize: TextView = itemView.findViewById(R.id.appPackage_id)
        val compressIcon: ImageView = itemView.findViewById(R.id.appIcon_id)
        val carview: CardView = itemView.findViewById(R.id.zipcarview_id)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompressFilesAdapter.CompressViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.appsitem_layout, parent, false)
        return CompressViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompressFilesAdapter.CompressViewHolder, position: Int) {
        val currentZip = compressList[position]
        holder.compressFileName.text = currentZip.zipName
        var sizeZip= calculatefileSize(currentZip.zipSize.toLong())
        holder.compressFilSize.text =sizeZip
        holder.carview.setBackgroundColor(
            if (currentZip.multipleSelection) {
                Color.parseColor("#264087F5")
            } else {
                Color.parseColor("#ffffff")
            }
        )

       if (currentZip.fileType=="zip"){

           holder.compressIcon.setImageResource(R.drawable.zip_icon)
       }
        else if (currentZip.fileType=="docs"){

           holder.compressIcon.setImageResource(R.drawable.documents_icon)
       }
       else if (currentZip.fileType=="apk"){

           holder.compressIcon.setImageResource(R.drawable.apk_icons)
       }

        holder.carview.setOnLongClickListener {
            currentZip.multipleSelection = !currentZip.multipleSelection
            holder.carview.setBackgroundColor(
                if (currentZip.multipleSelection) {
                    Color.parseColor("#264087F5")
                } else {
                    Color.parseColor("#ffffff")
                }
            )
            if (currentZip.multipleSelection) {
                count = 1
            } else count = 0
            listener.onClick(1)

            return@setOnLongClickListener true
        }
        holder.carview.setOnClickListener(View.OnClickListener {
            if (count > 0) {
                currentZip.multipleSelection = !currentZip.multipleSelection
                holder.carview.setBackgroundColor(
                    if (currentZip.multipleSelection) {

                        Color.parseColor("#264087F5")
                    } else {
                        Color.parseColor("#ffffff")
                    }

                )
                listener.onClick(1)
            } else
                Toast.makeText(myContext, "You Click  $position", Toast.LENGTH_SHORT).show()
        })






    }
    override fun getItemCount(): Int {
        return compressList.size
    }

    val allSelectedItems: java.util.ArrayList<CompressZipModel>
        get() {
            val selectedObjects = java.util.ArrayList<CompressZipModel>()
            if (compressList.isNotEmpty()) {
                for (item in compressList) {
                    if (item.multipleSelection) selectedObjects.add(item)
                }
            }
            return selectedObjects
        }

    fun selectAllItem(isSelectedAll: Boolean) {

        for (obj in compressList) {
            obj.multipleSelection = isSelectedAll
        }
        this.selection = isSelectedAll

        if (allSelectedItems.size >= compressList.size) {
            listener.onSelectAll(true, allSelectedItems.size)
        } else {
            listener.onSelectAll(false, 0)
        }
        notifyDataSetChanged()
    }

    fun getItemSelected(): java.util.ArrayList<CompressZipModel> {
        return allSelectedItems
    }

    fun clearCount() {
        count = 0
    }
    fun getZipSelectedItems(): ArrayList<CompressZipModel>{
        for (i in allSelectedItems.indices) {
            if (compressList.contains(allSelectedItems[i])) {
                val index: Int = compressList.indexOf(allSelectedItems[i])
                if (index >= 0 && index < compressList.size) {
                    compressList[index].multipleSelection = true
                }
            }
        }
        return allSelectedItems
    }



    private fun calculatefileSize(size: Long): kotlin.String {
        val byteUnits = listOf("B", "KB", "MB", "GB", "TB")
        var fileSize = size.toDouble()
        var index = 0
        while (fileSize >= 1024 && index < byteUnits.size - 1) {
            fileSize /= 1024
            index++
        }
        return "%.2f %s".format(fileSize, byteUnits[index])
    }
    interface onSelectionHandling {
        fun onClick(total: Int)
        fun onSelectAll(isAllSelected: Boolean, count: Int)
    }



}