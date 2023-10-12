package com.example.zipextectorapp.Fragments

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zipextectorapp.Activities.MainActivity
import com.example.zipextectorapp.Adapters.CompressFilesAdapter
import com.example.zipextectorapp.Models.CompressZipModel
import com.example.zipextectorapp.R
import com.example.zipextectorapp.Utills.checkBoxSelection
import com.example.zipextectorapp.Utills.openZipDialog
import com.example.zipextectorapp.Utills.unSelectAll
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class DocumentsFragment : Fragment(),CompressFilesAdapter.onSelectionHandling {
    private var loadingDialog: Dialog? = null
    private var zipRecyclerView: RecyclerView? = null
    private lateinit var documnetsAdapter: CompressFilesAdapter
    private var documentsList: ArrayList<CompressZipModel> = ArrayList()
    var flagSelectAll = false
    var check = 0
    private var mAllSelected = false
    private var bottomsheet: BottomSheetDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_documents, container, false)
    }

    private lateinit var mContext: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        zipRecyclerView=view.findViewById(R.id.documentRecyclerview_id)
        zipRecyclerView?.layoutManager = LinearLayoutManager(requireActivity())

        showloadingDialog()
        fetchAllDocumentsFiles()
        compressZipDailog()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )

        {
            documnetsAdapter =CompressFilesAdapter(requireActivity(), documentsList ,this)
            zipRecyclerView?.adapter = documnetsAdapter
        }

        unSelectAll = {
            cancelAllMultipleSelection()
        }
        checkBoxSelection = {
            toggleAllSelection()
        }
        openZipDialog = {
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

        val cursor = context?.contentResolver?.query(uri, projection, selection, selectionArgs, sortOrder)

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


////////////////
    private fun toggleAllSelection() {
        if (mAllSelected) {
            documnetsAdapter.selectAllItem(false)
        } else {
            documnetsAdapter.selectAllItem(true)
        }
    }

    override fun onClick(total: Int) {
        Log.d("onClick", "onClick: ")
        (mContext as? MainActivity)?.changeTitle(documnetsAdapter.getItemSelected().size.toString())

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

    fun compressZipDailog() {

        bottomsheet = BottomSheetDialog(requireActivity(), R.style.bottom_sheet_style)
        bottomsheet?.setContentView(R.layout.dialog_compresszip_layout)
        bottomsheet?.setCanceledOnTouchOutside(true)
        var zip = bottomsheet?.findViewById<TextView>(R.id.zip_id)
        var unZip = bottomsheet?.findViewById<TextView>(R.id.zip_7z_id)
        unZip?.setOnClickListener {
            Toast.makeText(requireContext(), " UNZIP Documents", Toast.LENGTH_SHORT).show()
        }



        zip?.setOnClickListener {
            zipList()
            cancelAllMultipleSelection()
            bottomsheet?.dismiss()
            Toast.makeText(requireContext(), " ZIP Documents Successfully", Toast.LENGTH_SHORT).show()
        }

    }

    fun cancelAllMultipleSelection() {
        flagSelectAll = true
        (mContext as? MainActivity)?.changeTitle("0")
        check = 0
        documnetsAdapter.clearCount()
        documnetsAdapter.selectAllItem(false)
        documnetsAdapter.notifyDataSetChanged()

    }

    fun zipList() {
        var file: Array<File>? = null
        val zipFiles = documnetsAdapter.getZipSelectedItems()
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
        val targetPath = File("/storage/emulated/0/DCIM/myZip.zip")

        zipFiles(fileList,targetPath)


        /*   ZipManager(object :ZipCallback{
                   override fun onStarted() {
                       Log.i("checkZip", "onStarted: ")
                   }

                   override fun onZipCompleted() {
                       Log.i("checkZip", "onZipCompleted: ")
                   }

                   override fun onUnzipCompleted() {

                   }

                   override fun onError(throwable: Throwable?) {

                       Log.e("checkZip", "onError: ",throwable )
                   }

               }).zipFiles(fileList,targetPath,requireActivity())*/




    }
    private fun zipFiles(files: List<File>, targetFile: File) {
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