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
import com.code4rox.medialoaderx.AudioLoaderX
import com.example.zipextectorapp.Adapters.MainAdapter
import com.example.zipextectorapp.Utills.showToast
import com.example.zipextectorapp.ViewModels.MainViewModel
import com.example.zipextectorapp.databinding.ActivityAllAudiosScreenBinding
import com.example.zipextractor.model.MainEntity
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class AllAudiosScreen : AppCompatActivity(), MainAdapter.SelectionHandler  {
    private lateinit var binding: ActivityAllAudiosScreenBinding
    private var audiosAdapter: MainAdapter? = null
    private var allAudiosList: ArrayList<MainEntity> = ArrayList()
    private var audiosViewModel: MainViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllAudiosScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        audiosViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.allAudiosRecyclerviewId.layoutManager = LinearLayoutManager(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
           // savefilesData()
            audiosViewModel?.getAllFiles("audios")
                ?.observe(this, Observer { list ->
                    allAudiosList = list as ArrayList<MainEntity>
                    audiosAdapter = MainAdapter(this, allAudiosList, this)
                    binding.allAudiosRecyclerviewId.adapter = audiosAdapter

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
        binding.checkBoxAudios.setOnClickListener {
            toggleAllSelection()
        }
        binding.compressLayout.setOnClickListener {
            getZipList()
            cancelAllMultipleSelection()
            showToast("Audios files  are Compress  successfully")
        }
        binding.extractLayout.setOnClickListener {
            showToast("its already UnZip file ")
        }
    }
    fun cancelAllMultipleSelection() {
        audiosAdapter?.selectAllItem(false)

    }
    private fun updateSelectionView() {
        if (audiosAdapter?.isSelectionMode == true) {
            binding.longPressView.visibility = View.VISIBLE
            binding.audiosBottomsheetLayout.visibility = View.VISIBLE
        } else {
            binding.audiosBottomsheetLayout.visibility = View.GONE
            binding.longPressView.visibility = View.GONE
        }
    }


    private fun toggleAllSelection() {
        if (audiosAdapter?.isAllSelected() == true) {
            audiosAdapter?.selectAllItem(false)
            audiosAdapter?.isSelectionMode = false
            updateSelectionView()
        } else {
            audiosAdapter?.selectAllItem(true)
        }
    }

    override fun onClick(position: Int) {
        binding.tvItemsCounter.text = audiosAdapter?.getSelectedCount().toString()
        binding.checkBoxAudios.isChecked = audiosAdapter?.isAllSelected() ?: false

        if (audiosAdapter?.getSelectedCount() == 0) {
            audiosAdapter?.selectAllItem(false)
            audiosAdapter?.isSelectionMode = false
            updateSelectionView()
        }
    }

    override fun onLongClick() {
         updateSelectionView()
        binding.tvItemsCounter.text = audiosAdapter?.getSelectedCount().toString()
        binding.checkBoxAudios.isChecked = audiosAdapter?.isAllSelected() ?: false
    }

    override fun onSelectAll(isAllSelected: Boolean) {
        binding.checkBoxAudios.isChecked = isAllSelected
        // binding.tvImagesCount.text = "${imagesAdapter?.getSelectedCount() ?: 0}"
        binding.tvItemsCounter.text = audiosAdapter?.getSelectedCount().toString()
    }

    override fun onBackPressed() {
        if (audiosAdapter?.isSelectionMode == true) {
            audiosAdapter?.selectAllItem(false)
            audiosAdapter?.isSelectionMode = false
            updateSelectionView()
        } else {
            super.onBackPressed()
        }
    }
    ////////
    private fun savefilesData() {
        AudioLoaderX(this).getAllAudios({ audios ->
            for (i in 0 until audios.size) {
                val fileID: Long = audios[i].audioId
                val fileTitle: String = audios[i].title
                val filePath: String = audios[i].path
                val fileSize: Long = audios[i].size
                val videoSize=calculatefileSize(fileSize)
                val videoDuration: Long = audios[i].duration


                val audios = MainEntity(
                    fileID,
                    fileTitle,
                    filePath,
                    videoSize,
                    videoDuration,
                    "audios"
                )
                audiosViewModel?.saveFilesData(audios)
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
    fun getZipList() {
        var file: Array<File>? = null
        val zipFiles = audiosAdapter?.getZipSelectedItems()
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