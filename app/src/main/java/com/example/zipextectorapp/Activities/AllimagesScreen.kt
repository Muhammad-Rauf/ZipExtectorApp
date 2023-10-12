package com.example.zipextectorapp.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.code4rox.medialoaderx.ImageLoaderX
import com.example.zipextectorapp.Adapters.CustomSpinnerAdapter
import com.example.zipextectorapp.Adapters.MainAdapter
import com.example.zipextectorapp.R
import com.example.zipextectorapp.Utills.showToast
import com.example.zipextectorapp.Utills.toSize
import com.example.zipextectorapp.ViewModels.MainViewModel
import com.example.zipextectorapp.databinding.ActivityAllimagesScreenBinding
import com.example.zipextractor.model.MainEntity
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.*
import java.lang.NullPointerException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class AllimagesScreen : AppCompatActivity(), MainAdapter.SelectionHandler {
    private lateinit var binding: ActivityAllimagesScreenBinding
    private var imagesAdapter: MainAdapter? = null
    private var allImagesList: ArrayList<MainEntity> = ArrayList()
    private var imagesViewModel: MainViewModel? = null
    private var bottomsheet: BottomSheetDialog? = null
    val spinnerItems = arrayOf("zip", "rar", "tar", "7z")
    var selecteddirectoryPath:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllimagesScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imagesViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.allImagesRecyclerviewId.layoutManager = LinearLayoutManager(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            // saveFilesData()
            imagesViewModel?.getAllFiles("images")
                ?.observe(this, Observer { list ->
                    allImagesList = list as ArrayList<MainEntity>
                    imagesAdapter = MainAdapter(this, allImagesList, this)
                    binding.allImagesRecyclerviewId.adapter = imagesAdapter

                })

        } else {
            //  checkPermissions()
        }

        binding.crossAllSelectedItems1.setOnClickListener {
            cancelAllMultipleSelection()
        }
        binding.checkBoxImages1.setOnClickListener {
            toggleAllSelection()
        }
        binding.compressLayout.setOnClickListener {
            getZipList()
            cancelAllMultipleSelection()
            showToast("Images  files are Compress  successfully")
        }
        binding.extractLayout.setOnClickListener {
            showToast("its already UnZip file ")
        }
        binding.moreLayout.setOnClickListener {
            compressZipFormatDialog()
        }
        binding.imagesbackId.setOnClickListener {
            onBackPressed()
        }

    }

    private fun updateSelectionView() {
        if (imagesAdapter?.isSelectionMode == true) {
            binding.longPressView.visibility = View.VISIBLE
            binding.imagesBottomsheetLayout.visibility = View.VISIBLE
        } else {
            binding.imagesBottomsheetLayout.visibility = View.GONE
            binding.longPressView.visibility = View.GONE
        }
    }

    fun cancelAllMultipleSelection() {
        imagesAdapter?.selectAllItem(false)

    }

    private fun saveFilesData() {
        ImageLoaderX(this).getAllImages({ images ->
            for (i in 0 until images.size) {
                val fileID: Long = images[i].imageId
                val fileTitle: String = images[i].title
                val filePath: String = images[i].path
                val fileSize: Long = images[i].size
                val imageSize : String = fileSize.toSize()

                val folder = MainEntity(
                    fileID,
                    fileTitle,
                    filePath,
                    imageSize,
                    100,
                    "images"
                )
                imagesViewModel?.saveFilesData(folder)
            }
        }, { imageFolders ->

        })


    }

    private fun toggleAllSelection() {
        if (imagesAdapter?.isAllSelected() == true) {
            imagesAdapter?.selectAllItem(false)
            imagesAdapter?.isSelectionMode = false
            updateSelectionView()
        } else {
            imagesAdapter?.selectAllItem(true)
        }
    }

    override fun onClick(position: Int) {
        binding.tvItemsCounter.text = imagesAdapter?.getSelectedCount().toString()
        binding.checkBoxImages1.isChecked = imagesAdapter?.isAllSelected() ?: false

        if (imagesAdapter?.getSelectedCount() == 0) {
            imagesAdapter?.selectAllItem(false)
            imagesAdapter?.isSelectionMode = false
            updateSelectionView()
        }
    }

    override fun onLongClick() {
        updateSelectionView()
        // binding.tvImagesCount.text = imagesAdapter?.getSelectedC   binding.tvItemsCounter.text = imagesAdapter?.getSelectedCount().toString()
        binding.checkBoxImages1.isChecked = imagesAdapter?.isAllSelected() ?: false
    }

    override fun onSelectAll(isAllSelected: Boolean) {
        binding.checkBoxImages1.isChecked = isAllSelected
        // binding.tvImagesCount.text = "${imagesAdapter?.getSelectedCount() ?: 0}"
        binding.tvItemsCounter.text = imagesAdapter?.getSelectedCount().toString()
    }

    override fun onBackPressed() {
        if (imagesAdapter?.isSelectionMode == true) {
            imagesAdapter?.selectAllItem(false)
            imagesAdapter?.isSelectionMode = false
            updateSelectionView()
        } else {
            super.onBackPressed()
        }
    }


    ////////
    fun getZipList() {
        var file: Array<File>? = null
        val zipFiles = imagesAdapter?.getZipSelectedItems()
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
        val targetPath = File("/storage/emulated/0/DCIM/GeneratesZipFiles.zip")

        convertZipFile(fileList, targetPath)

    }

    private fun convertZipFile(files: List<File>, targetFile: File) {
        val buffer = ByteArray(1024)
        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
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

    fun compressZipFormatDialog() {
        bottomsheet = BottomSheetDialog(this, R.style.bottom_sheet_style)
        bottomsheet?.setContentView(R.layout.dialog_compresszip_new)
        bottomsheet?.setCanceledOnTouchOutside(true)
        val spinner: Spinner? = bottomsheet?.findViewById(R.id.spinnerSelectZipFormat)
        val adapter = CustomSpinnerAdapter(this, spinnerItems)
        spinner?.adapter = adapter
        bottomsheet?.show()

        var editTextFileName = bottomsheet?.findViewById<TextView>(R.id.editTextTextFileName)
        var selectFolderPath = bottomsheet?.findViewById<ConstraintLayout>(R.id.saveFilePath_id)
        selectFolderPath?.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            startActivityForResult(intent, 200)
        }

        var compressFile = bottomsheet?.findViewById<TextView>(R.id.compressBtn_id)

         compressFile?.setOnClickListener {

            val file = File("/storage/emulated/0/ZipFiles")
            if (!file.exists()) {
                file.mkdirs()
            }
            if (TextUtils.isEmpty(editTextFileName.toString())) {
                showToast("Enter Name of zip file")

            } else {
                val userFileName: String = editTextFileName?.text.toString()
                val finalPath = "/storage/emulated/0/OneWood/$userFileName.zip"

                getUserZipList(finalPath)
            }
            cancelAllMultipleSelection()
            bottomsheet?.dismiss()
            showToast("File Compress Successfully")


        }
        var cancelDialog = bottomsheet?.findViewById<TextView>(R.id.cancelBtn_id)
        cancelDialog?.setOnClickListener {
            bottomsheet?.dismiss()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            val document = DocumentFile.fromTreeUri(this, uri) ?: return
             selecteddirectoryPath = document.uri.path ?: return



        }
    }

    fun getUserZipList(tarPath: String) {
        var file: Array<File>? = null
        val zipFiles = imagesAdapter?.getZipSelectedItems()
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
        // val targetPath = File("/storage/emulated/0/DCIM/RaufZip.zip")

        convertUserZipFile(fileList, tarPath)

    }

    private fun convertUserZipFile(files: List<File>, targetFile: String) {
        val buffer = ByteArray(1024)
        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
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

}