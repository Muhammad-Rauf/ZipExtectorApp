package com.example.zipextectorapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zipextectorapp.Models.AppModel

class AppsAdapter(val myContext: Context, var myAppList: ArrayList<AppModel>/*, var clickItem:onOpenAllData*/): RecyclerView.Adapter<AppsAdapter.AppsViewHolder>() {

    inner class AppsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val appName: TextView = itemView.findViewById(com.example.zipextectorapp.R.id.appName_id)
        val appPackageName: TextView = itemView.findViewById(com.example.zipextectorapp.R.id.appPackage_id)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsAdapter.AppsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(com.example.zipextectorapp.R.layout.appsitem_layout, parent, false)
        return AppsViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppsAdapter.AppsViewHolder, position: Int) {
        val currentApp = myAppList[position]
        holder.appName.text = currentApp.appName
        holder.appPackageName.text = currentApp.packages

    }

    override fun getItemCount(): Int {
        return myAppList.size
    }

/*     interface onOpenAllData {
         fun onOpenClick(images: MainEntity)
     }*/

}