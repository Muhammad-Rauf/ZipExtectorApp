package com.example.zipextectorapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zipextectorapp.Adapters.HomeFoldersAdapter
import com.example.zipextectorapp.R
import com.example.zipextectorapp.databinding.ActivityFolderListBinding
import java.io.File

class FolderListScreen : AppCompatActivity() {
var binder:ActivityFolderListBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recycle_view)
        val noFileText = findViewById<TextView>(R.id.nofiles_textview)

        val path = getIntent().getStringExtra("path")

        val root = File(path)
        val filesAndFolders = root.listFiles()

        if (filesAndFolders == null || filesAndFolders.size == 0) {
            noFileText.visibility = View.VISIBLE
            return
        }

        noFileText.visibility = View.INVISIBLE

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = HomeFoldersAdapter(applicationContext,filesAndFolders)
    }
}