package com.example.zipextectorapp.Fragments

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zipextectorapp.Activities.MainActivity
import com.example.zipextectorapp.Adapters.AppsAdapter
import com.example.zipextectorapp.Adapters.CompressFilesAdapter
import com.example.zipextectorapp.Adapters.MainAdapter
import com.example.zipextectorapp.Models.CompressZipModel
import com.example.zipextectorapp.R
import com.example.zipextectorapp.Utills.*
import com.example.zipextractor.model.MainEntity
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class CompressedFragment : Fragment(), CompressFilesAdapter.onSelectionHandling  {
    private var loadingDialog: Dialog? = null
    private var zipRecyclerView: RecyclerView? = null
    private lateinit var compressAdapter: CompressFilesAdapter
    private var compressedFilesList: ArrayList<CompressZipModel> = ArrayList()
    private var bottomsheet: BottomSheetDialog? = null

    var flagSelectAll = false
    var check = 0
    private var mAllSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private lateinit var mContext: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compressed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        zipRecyclerView=view.findViewById(R.id.zipRecyclerview)
        zipRecyclerView?.layoutManager = LinearLayoutManager(requireActivity())

        showloadingDialog()
        fetchAllZipFiles()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )

        {
            compressAdapter= CompressFilesAdapter(requireActivity(), compressedFilesList ,this)
            zipRecyclerView?.adapter=compressAdapter

        }

        compressUnZipDailog()
        unSelectAllZipFragment = {
            cancelAllMultipleSelection()
        }
        checkBoxSelectionZipFragment = {
            toggleAllSelection()
        }
        openZipDialogZipFragment = {
            bottomsheet?.show()
        }

    }


    private fun showloadingDialog() {
        loadingDialog = Dialog(requireContext(), R.style.loadingDialog)
        loadingDialog?.setContentView(R.layout.loading_layout)
        loadingDialog?.window!!.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        loadingDialog?.setCancelable(false)
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

        val cursor = context?.contentResolver?.query(uri, projection, selection, selectionArgs, sortOrder)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME))
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE))
                val dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED))
                val mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE))
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))

                compressedFilesList.add(CompressZipModel(id,name, size.toString(),path,uri,"zip",false))
                // Use the retrieved data as required
                Log.d("TAG", "Id: $id, Name: $name, Size: $size, Date Added: $dateAdded, Mime Type: $mimeType")
            } while (cursor.moveToNext())
        }
        cursor?.close()


    }

    private fun toggleAllSelection() {
        if (mAllSelected) {
            compressAdapter.selectAllItem(false)
        } else {
            compressAdapter.selectAllItem(true)
        }
    }

    override fun onClick(total: Int) {
        Log.d("onClick", "onClick: ")
       // (mContext as? MainActivity)?.changeTitle(CompressFilesAdapter.getItemSelected().size.toString())
        (mContext as? MainActivity)?.changeTitle(compressAdapter.getItemSelected().size.toString())


    }

    override fun onSelectAll(isAllSelected: Boolean, count: Int) {
        Log.d("onClick", "onSelectAll: ")
        mAllSelected = isAllSelected
        // checkboxId.isChecked = mAllSelected
        val c = if (isAllSelected) {
            "$count"
        } else {
            "0"
        }
        (mContext as? MainActivity)?.changeTitle(c)
    }

    fun cancelAllMultipleSelection() {
        flagSelectAll = true
        (mContext as? MainActivity)?.changeTitle("0")
        check = 0
        compressAdapter.clearCount()
        compressAdapter.selectAllItem(false)
        compressAdapter.notifyDataSetChanged()

    }
    fun compressUnZipDailog() {

        bottomsheet = BottomSheetDialog(requireActivity(), R.style.bottom_sheet_style)
        bottomsheet?.setContentView(R.layout.dialog_compresszip_layout)
        bottomsheet?.setCanceledOnTouchOutside(true)
       var zip = bottomsheet?.findViewById<TextView>(R.id.zip_id)
        var unZip = bottomsheet?.findViewById<TextView>(R.id.zip_7z_id)

        zip?.setOnClickListener {
            Toast.makeText(requireContext(), " ZIP Compressed File Successfully", Toast.LENGTH_SHORT).show()
        }



        unZip?.setOnClickListener {
            zipList()
            cancelAllMultipleSelection()
            bottomsheet?.dismiss()
            Toast.makeText(requireContext(), " UnZIP Compressed file Successfully", Toast.LENGTH_SHORT).show()
        }

    }
    fun zipList() {
        var file: Array<File>? = null
        val zipFiles = compressAdapter.getZipSelectedItems()
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
