package com.example.zipextectorapp.Activities

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zipextectorapp.Adapters.CompressFilesAdapter
import com.example.zipextectorapp.Models.CompressZipModel
import com.example.zipextectorapp.R
import com.example.zipextectorapp.Utills.showToast
import com.example.zipextectorapp.databinding.ActivityAllAudiosScreenBinding
import com.example.zipextectorapp.databinding.ActivityAllDocumentsScreenBinding
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class AllDocumentsScreen : AppCompatActivity(),CompressFilesAdapter.onSelectionHandling {
    private lateinit var binding: ActivityAllDocumentsScreenBinding
    var documnetsAdapter: CompressFilesAdapter?=null
    private var documentsList: ArrayList<CompressZipModel> = ArrayList()
    var flagSelectAll = false
    var check = 0
    private var mAllSelected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllDocumentsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.allDocumentsRecyclerviewId.layoutManager = LinearLayoutManager(this)

        if (ContextCompat.checkSelfPermission(
             this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )
        {
            fetchAllDocumentsFiles()

            documnetsAdapter =CompressFilesAdapter(this, documentsList ,this)
            binding.allDocumentsRecyclerviewId.adapter = documnetsAdapter
        }
        binding.checkBoxImages.setOnClickListener {
          toggleAllSelection()
        }
        binding.crossAllSelectedItems.setOnClickListener {
         cancelAllMultipleSelection()
        }
        binding.compressLayout.setOnClickListener {
            getZipList()
            cancelAllMultipleSelection()
            showToast("Documents files are Compress  successfully")
        }

        binding.extractLayout.setOnClickListener {
            showToast("its already UnZip file ")
        }
    }
    fun fetchAllDocumentsFiles(){
        val uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATA
        )
        val selection = "${MediaStore.Files.FileColumns.MIME_TYPE} IN (?, ?)"
        val selectionArgs = arrayOf("application/pdf", "application/docx")
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        val cursor = this.contentResolver?.query(uri, projection, selection, selectionArgs, sortOrder)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME))
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE))
                val dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED))
                val mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE))
                val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
                documentsList.add(CompressZipModel(id,name, size.toString(),data,uri,"docs",false))
                // Use the retrieved data as required
                Log.d("TAG", "Id: $id, Name: $name, Size: $size, Date Added: $dateAdded, Mime Type: $mimeType, Data: $data")
            } while (cursor.moveToNext())
        }
        cursor?.close()


    }
    fun cancelAllMultipleSelection() {
        flagSelectAll = true
        binding.tvItemsCounter.text = "0"
        check = 0
        documnetsAdapter?.clearCount()
        documnetsAdapter?.selectAllItem(false)
        documnetsAdapter?.notifyDataSetChanged()

    }
    private fun toggleAllSelection() {
        if (mAllSelected) {
            documnetsAdapter?.selectAllItem(false)
        } else {
            documnetsAdapter?.selectAllItem(true)
        }
    }
    override fun onClick(total: Int) {
        Log.d("onClick", "onClick: ")
        binding.tvItemsCounter.text = documnetsAdapter?.getItemSelected()?.size.toString()
        binding.checkBoxImages.isChecked = documnetsAdapter?.getItemSelected()?.size!! >= documnetsAdapter!!.itemCount

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
    fun getZipList() {
        var file: Array<File>? = null
        val zipFiles = documnetsAdapter?.getZipSelectedItems()
        val fileList: MutableList<File> = mutableListOf()
        if (zipFiles != null) {
            for (i in zipFiles) {
                val f = File(i.zipPath)
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