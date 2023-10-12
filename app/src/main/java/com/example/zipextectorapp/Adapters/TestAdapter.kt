/*
package com.example.zipextectorapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.zipextectorapp.databinding.GalleryitemLayoutBinding
import com.example.zipextractor.model.MainEntity

class TestAdapter<D : Any>(
    val onBind: ((GalleryitemLayoutBinding, D, Int) -> Unit)
) : ListAdapter<D, TestAdapter.TestViewHolder>(object :
    DiffUtil.ItemCallback<D>() {
    override fun areItemsTheSame(oldItem: D, newItem: D): Boolean {
        return (oldItem as MainEntity).fileId == (newItem as MainEntity).fileId
    }

    override fun areContentsTheSame(oldItem: D, newItem: D): Boolean {
        return (oldItem as MainEntity).fileId == (newItem as MainEntity).fileId
                && (oldItem as MainEntity).filePath == (newItem as MainEntity).filePath
    }

}) {
    class TestViewHolder(val binding: GalleryitemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {

    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        onBind.invoke(holder.binding, currentList[position], position)
    }
}*/
