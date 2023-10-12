package com.example.zipextectorapp.Activities

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zipextectorapp.Adapters.CompressFilesAdapter
import com.example.zipextectorapp.Models.CompressZipModel
import com.example.zipextectorapp.Utills.showToast
import com.example.zipextectorapp.databinding.ActivityAllZipScreenBinding
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class AllZipScreen : AppCompatActivity() , CompressFilesAdapter.onSelectionHandling {
    private lateinit var binding: ActivityAllZipScreenBinding
    private lateinit var zipAdapter: CompressFilesAdapter
    private var allZipList: ArrayList<CompressZipModel> = ArrayList()
    var flagSelectAll = false
    var check = 0
    private var mAllSelected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllZipScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.allZipRecyclerviewId.layoutManager = LinearLayoutManager(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )

        {
            fetchAllZipFiles()
            zipAdapter =CompressFilesAdapter(this, allZipList ,this)
            binding.allZipRecyclerviewId.adapter = zipAdapter
        }
        binding.checkBoxImages.setOnClickListener {
            toggleAllSelection()
        }
        binding.crossAllSelectedItems.setOnClickListener {
            cancelAllMultipleSelection()
        }
        binding.compressLayout.setOnClickListener {
            cancelAllMultipleSelection()
            showToast("it file already in zip format")
        }
        binding.extractLayout.setOnClickListener {
            showToast("UnZip file successfully")
            unZipList()
            cancelAllMultipleSelection()
        }
    }
    fun fetchAllZipFiles(){
        val uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATA
        )
        val selection = "${MediaStore.Files.FileColumns.MIME_TYPE} = ?"
        val selectionArgs = arrayOf("application/zip")
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        val cursor = this.contentResolver?.query(uri, projection, selection, selectionArgs, sortOrder)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME))
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE))
                val dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED))
                val mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE))
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))

                allZipList.add(CompressZipModel(id,name, size.toString(),path,uri,"zip",false))
                // Use the retrieved data as required
                Log.d("TAG", "Id: $id, Name: $name, Size: $size, Date Added: $dateAdded, Mime Type: $mimeType")
            } while (cursor.moveToNext())
        }
        cursor?.close()


    }

    fun cancelAllMultipleSelection() {
        flagSelectAll = true
        binding.tvItemsCounter.text = "0"
        check = 0
        zipAdapter.clearCount()
        zipAdapter.selectAllItem(false)
        zipAdapter.notifyDataSetChanged()

    }
    private fun toggleAllSelection() {
        if (mAllSelected) {
            zipAdapter.selectAllItem(false)
        } else {
            zipAdapter.selectAllItem(true)
        }
    }
    override fun onClick(total: Int) {
        Log.d("onClick", "onClick: ")
        binding.tvItemsCounter.text = zipAdapter.getItemSelected().size.toString()
        binding.checkBoxImages.isChecked = zipAdapter.getItemSelected().size >= zipAdapter.itemCount

    }

    override fun onSelectAll(isAllSelected: Boolean, count: Int) {
        Log.d("onClick", "onSelectAll: ")
        mAllSelected = isAllSelected
        binding.checkBoxImages.isChecked = mAllSelected
        if (isAllSelected) {
            binding.tvItemsCounter.text = "$count"

        }
        else{
            binding.tvItemsCounter.text = "0"
        }
    }

    fun unZipList() {
        var file: Array<File>? = null
        val zipFiles = zipAdapter.getZipSelectedItems()
        val fileList: MutableList<File> = mutableListOf()
        for (i in zipFiles) {
            val f = File(i.zipPath)
            if (f.exists()) {
                fileList.add(f)
            }
        }
        if (fileList.isNotEmpty()) {
            file = fileList.toTypedArray()
        }

        //val targetPath = File("/storage/emulated/0/myZip.zip")
        // val targetPath = File("/storage/emulated/0/DCIM/myZip.zip")
        var destinationpath=  "/storage/emulated/0/myUnZip"

        unzipFiles(zipFiles[0].zipPath,destinationpath)

    }
    private fun unzipFiles(zipFilePath: String, destDir: String) {
        val bufferSize = 2048
        val buffer = ByteArray(bufferSize)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val zipInputStream = ZipInputStream(BufferedInputStream(FileInputStream(zipFilePath)))
            var zipEntry: ZipEntry? = zipInputStream.nextEntry
            while (zipEntry != null) {
                val outputFile = File(destDir, zipEntry.name)
                if (zipEntry.isDirectory) {
                    outputFile.mkdirs()
                } else {
                    val outputStream = FileOutputStream(outputFile)
                    val bufferedOutputStream = BufferedOutputStream(outputStream, bufferSize)
                    var count = zipInputStream.read(buffer, 0, bufferSize)
                    while (count != -1) {
                        bufferedOutputStream.write(buffer, 0, count)
                        count = zipInputStream.read(buffer, 0, bufferSize)
                    }
                    bufferedOutputStream.flush()
                    bufferedOutputStream.close()
                }
                zipEntry = zipInputStream.nextEntry
            }
            zipInputStream.close()
        }
    }
}