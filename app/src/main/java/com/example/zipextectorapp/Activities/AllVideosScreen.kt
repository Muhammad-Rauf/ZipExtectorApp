package com.example.zipextectorapp.Activities

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.code4rox.medialoaderx.ImageLoaderX
import com.code4rox.medialoaderx.VideoLoaderX
import com.example.zipextectorapp.Adapters.MainAdapter
import com.example.zipextectorapp.Utills.showToast
import com.example.zipextectorapp.ViewModels.MainViewModel
import com.example.zipextectorapp.databinding.ActivityAllVideosScreenBinding
import com.example.zipextractor.model.MainEntity
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class AllVideosScreen : AppCompatActivity(), MainAdapter.SelectionHandler  {
    private lateinit var binding: ActivityAllVideosScreenBinding
    private var videosAdapter: MainAdapter? = null
    private var allVideosList: ArrayList<MainEntity> = ArrayList()
    private var videosViewModel: MainViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllVideosScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        videosViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.allVideosRecyclerviewId.layoutManager = LinearLayoutManager(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
           // savefilesData()
            videosViewModel?.getAllFiles("videos")
                ?.observe(this, Observer { list ->
                    allVideosList = list as ArrayList<MainEntity>
                    videosAdapter = MainAdapter(this, allVideosList, this)
                    binding.allVideosRecyclerviewId.adapter = videosAdapter

                })
        } else {
            //  checkPermissions()
        }
        binding.videosbackId.setOnClickListener {
            onBackPressed()
        }
        binding.crossAllSelectedItems.setOnClickListener {
            cancelAllMultipleSelection()
        }
        binding.checkBoxVideos.setOnClickListener {
            toggleAllSelection()
        }
        binding.compressLayout.setOnClickListener {
            getZipList()
            cancelAllMultipleSelection()
            showToast("Videos files  are Compress  successfully")
        }
        binding.extractLayout.setOnClickListener {
            showToast("its already UnZip file ")
        }
    }
    fun cancelAllMultipleSelection() {
        videosAdapter?.selectAllItem(false)

    }


    private fun updateSelectionView() {
        if (videosAdapter?.isSelectionMode == true) {
            binding.longPressView.visibility = View.VISIBLE
            binding.videosBottomsheetLayout.visibility = View.VISIBLE
        } else {
            binding.videosBottomsheetLayout.visibility = View.GONE
            binding.longPressView.visibility = View.GONE
        }
    }
    private fun savefilesData() {
        VideoLoaderX(this).getAllVideos({ videos ->
            for (i in 0 until videos.size) {
                val fileID: Long = videos[i].videoId
                val fileTitle: String = videos[i].title
                val filePath: String = videos[i].path
                val fileSize: Long = videos[i].size
                var videoSize=calculatefileSize(fileSize)
                var videoDuration: Long = videos[i].duration


                val videos = MainEntity(
                    fileID,
                    fileTitle,
                    filePath,
                    videoSize,
                    videoDuration,
                    "videos"
                )
                videosViewModel?.saveFilesData(videos)
            }
        }, { videoFolders ->

        })


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

    private fun toggleAllSelection() {
        if (videosAdapter?.isAllSelected() == true) {
            videosAdapter?.selectAllItem(false)
            videosAdapter?.isSelectionMode = false
            updateSelectionView()
        } else {
            videosAdapter?.selectAllItem(true)
        }
    }

    override fun onClick(position: Int) {
        binding.tvItemsCounter.text = videosAdapter?.getSelectedCount().toString()
        binding.checkBoxVideos.isChecked = videosAdapter?.isAllSelected() ?: false

        if (videosAdapter?.getSelectedCount() == 0) {
            videosAdapter?.selectAllItem(false)
            videosAdapter?.isSelectionMode = false
            updateSelectionView()
        }
    }


    override fun onLongClick() {
         updateSelectionView()
        // binding.tvImagesCount.text = imagesAdapter?.getSelectedCount().toString()
        binding.tvItemsCounter.text = videosAdapter?.getSelectedCount().toString()
        binding.checkBoxVideos.isChecked = videosAdapter?.isAllSelected() ?: false
    }

    override fun onSelectAll(isAllSelected: Boolean) {
        binding.checkBoxVideos.isChecked = isAllSelected
        // binding.tvImagesCount.text = "${imagesAdapter?.getSelectedCount() ?: 0}"
        binding.tvItemsCounter.text = videosAdapter?.getSelectedCount().toString()
    }

    override fun onBackPressed() {
        if (videosAdapter?.isSelectionMode == true) {
            videosAdapter?.selectAllItem(false)
            videosAdapter?.isSelectionMode = false
            updateSelectionView()
        } else {
            super.onBackPressed()
        }
    }
    ////////
    fun getZipList() {
        var file: Array<File>? = null
        val zipFiles = videosAdapter?.getZipSelectedItems()
        val fileList: MutableList<File> = mutableListOf()
        if (zipFiles != null) {
            for (i in zipFiles) {
                val f = File(i.filePath)
                if (f.exists()) {
                    fileList.add(f)
                }
            }
        }
        if (fileList.isNotEmpty()) {
            file = fileList.toTypedArray()
        }

        //val targetPath = File("/storage/emulated/0/myZip.zip")
        val targetPath = File("/storage/emulated/0/DCIM/RaufZip.zip")

        convertZipFile(fileList,targetPath)

    }
    private fun convertZipFile(files: List<File>, targetFile: File) {
        val buffer = ByteArray(1024)
        val fos = FileOutputStream(targetFile)
        val bos = BufferedOutputStream(fos)
        val zos = ZipOutputStream(bos)

        // loop through each file and add it to the zip file
        for (file in files) {
            val fis = FileInputStream(file)
            val bis = BufferedInputStream(fis)
            val entry = ZipEntry(file.name)
            zos.putNextEntry(entry)

            // write the file contents to the zip file
            var len: Int
            while (bis.read(buffer).also { len = it } > 0) {
                zos.write(buffer, 0, len)
            }

            bis.close()
            zos.closeEntry()
        }

        zos.close()
    }
}