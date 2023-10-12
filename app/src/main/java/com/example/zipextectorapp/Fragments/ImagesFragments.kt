package com.example.zipextectorapp.Fragments

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.code4rox.medialoaderx.ImageLoaderX
import com.example.zipextectorapp.Activities.MainActivity
import com.example.zipextectorapp.Adapters.MainAdapter
import com.example.zipextectorapp.R
import com.example.zipextectorapp.Utills.checkBoxSelection
import com.example.zipextectorapp.Utills.debugLog
import com.example.zipextectorapp.Utills.openZipDialog
import com.example.zipextectorapp.Utills.unSelectAll
import com.example.zipextectorapp.ViewModels.MainViewModel
import com.example.zipextractor.model.MainEntity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.request.ExplainScope
import com.permissionx.guolindev.request.ForwardScope
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


class ImagesFragments : Fragment(), MainAdapter.SelectionHandler {
    private var loadingDialog: Dialog? = null
    private var deniedPermDialog: Dialog? = null
    private var checkPermissionDialog: Dialog? = null
    private lateinit var imagesAdapter: MainAdapter
    private var imagesRecycler: RecyclerView? = null
    private var allImagesList: ArrayList<MainEntity> = ArrayList()
    private var imagesViewModel: MainViewModel? = null
    private var bottomsheet: BottomSheetDialog? = null
    private var homeToolBar: androidx.appcompat.widget.Toolbar? = null
    private var toolbarImageRecycler: androidx.appcompat.widget.Toolbar? = null
    private var toolbarHome: androidx.appcompat.widget.Toolbar? = null
    var flagSelectAll = false
    var check = 0
    private var mAllSelected = false

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
        var view = inflater.inflate(R.layout.fragment_images_fragments, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagesRecycler = view.findViewById(R.id.galleryRecyclerView_id)
        // toolbarImageRecycler = view.findViewById(R.id.toolbarImageRecycler)

        homeToolBar = view.findViewById(R.id.toolbarHome)
        (activity as AppCompatActivity).setSupportActionBar(homeToolBar)

        imagesViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        imagesRecycler?.layoutManager = LinearLayoutManager(requireActivity())
        showloadingDialog()
        compressZipDailog()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            loadingDialog?.show()
            savefilesData()
            Handler(Looper.getMainLooper()).postDelayed({
                imagesRecycler?.layoutManager = LinearLayoutManager(requireActivity())
                imagesViewModel?.getAllFiles("images")
                    ?.observe(viewLifecycleOwner, Observer { list ->
                        allImagesList = list as ArrayList<MainEntity>
                        imagesAdapter = MainAdapter(requireActivity(), allImagesList, this)
                        imagesRecycler?.adapter = imagesAdapter

                    })
                loadingDialog?.dismiss()
            }, 1500)


        } else {
            checkPermissions()
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


    ////////////////
    private fun savefilesData() {
        ImageLoaderX(requireActivity()).getAllImages({ images ->
            for (i in 0 until images.size) {
                val fileID: Long = images[i].imageId
                val fileTitle: String = images[i].title
                val filePath: String = images[i].path
                val fileSize: Long = images[i].size
                var imageSize = calculatefileSize(fileSize)


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

    private fun showloadingDialog() {
        loadingDialog = Dialog(requireContext(), R.style.loadingDialog)
        loadingDialog?.setContentView(R.layout.loading_layout)
        loadingDialog?.window!!.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        loadingDialog?.setCancelable(false)
    }

    private fun showDeniedPermissionDialog() {
        deniedPermDialog = Dialog(requireActivity(), R.style.CustomDialog1)
        deniedPermDialog?.setContentView(R.layout.layout_dialog_nopermission)
        val tvButton = deniedPermDialog?.findViewById<TextView>(R.id.tv_allow_permission)
        // permDialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        deniedPermDialog?.setCancelable(true)
        tvButton?.setOnClickListener { v: View? ->
            val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            //   permIntentLauncher.launch(intent)
            deniedPermDialog?.dismiss()
        }
        deniedPermDialog?.findViewById<View>(R.id.tv_cancel_permission)
            ?.setOnClickListener { deniedPermDialog?.dismiss() }
        if (!requireActivity().isFinishing && !requireActivity().isChangingConfigurations) {

            deniedPermDialog?.show()
        }
    }

    private fun checkPermissionDialog() {
        checkPermissionDialog = Dialog(requireActivity(), R.style.CustomDialog1)
        checkPermissionDialog?.setContentView(R.layout.checkpermission_layout)
        val tvButton = checkPermissionDialog?.findViewById<TextView>(R.id.tv_allow_permission)
        tvButton?.setOnClickListener {
            checkPermissions()
            checkPermissionDialog?.dismiss()
        }
        checkPermissionDialog?.setCancelable(true)

        checkPermissionDialog?.findViewById<View>(R.id.tv_cancel_permission)
            ?.setOnClickListener { checkPermissionDialog?.dismiss() }

        if (!requireActivity().isFinishing && !requireActivity().isChangingConfigurations) {

            checkPermissionDialog?.show()
        }
    }

    private fun checkPermissions() {
        PermissionX.init(this).permissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            .onForwardToSettings { scope: ForwardScope?, deniedList: List<String?>? ->
                showDeniedPermissionDialog()
                Log.d("CheckPermission", " Permission denied 1: ")
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    checkPermissionDialog()
                }
            }
            .onExplainRequestReason { scope: ExplainScope?, deniedList: List<String?>? ->
                Log.d("CheckPermission", " Permission denied 2: ")
            }
            .request { allGranted: Boolean, grantedList: List<String?>?, deniedList: List<String?>? ->
                if (allGranted) {
                    Log.d("CheckPermission", "Permisssion Granted: ")
                    loadingDialog?.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        savefilesData()
                        imagesRecycler?.layoutManager = LinearLayoutManager(requireActivity())
                        imagesViewModel?.getAllFiles("images")
                            ?.observe(viewLifecycleOwner, Observer { list ->
                                allImagesList = list as ArrayList<MainEntity>
                                imagesAdapter = MainAdapter(requireActivity(), allImagesList, this)
                                imagesRecycler?.adapter = imagesAdapter

                            })
                        loadingDialog?.dismiss()
                    }, 1500)

                }

            }

    }
    ////////////////////////////////////

    private fun toggleAllSelection() {
        if (mAllSelected) {
            imagesAdapter.selectAllItem(false)
        } else {
            imagesAdapter.selectAllItem(true)
        }
    }

    override fun onClick(position:Int) {
        Log.d("onClick", "onClick: ")
//        (mContext as? MainActivity)?.changeTitle(imagesAdapter.getItemSelected().size.toString())
//        tvCounter_id.text = imagesAdapter.getItemSelected().size.toString()

        // checkboxId.isChecked = imagesAdapter.getItemSelected().size >= imagesAdapter.itemCount

    }

    override fun onSelectAll(isAllSelected: Boolean) {
        Log.d("onClick", "onSelectAll: ")
        mAllSelected = isAllSelected
        // checkboxId.isChecked = mAllSelected
//        val c = if (isAllSelected) {
//            "$count"
//        } else {
//            "0"
//        }
//        (mContext as? MainActivity)?.changeTitle(c)
    }

    override fun onLongClick() {
        debugLog("OnLongClick")
    }

    fun compressZipDailog() {

        bottomsheet = BottomSheetDialog(requireActivity(), R.style.bottom_sheet_style)
        bottomsheet?.setContentView(R.layout.dialog_compresszip_layout)
        bottomsheet?.setCanceledOnTouchOutside(true)
        var zip = bottomsheet?.findViewById<TextView>(R.id.zip_id)
        var zip_7z = bottomsheet?.findViewById<TextView>(R.id.zip_7z_id)


        zip?.setOnClickListener {
            zipList()
            cancelAllMultipleSelection()
            bottomsheet?.dismiss()
        }

    }

    fun cancelAllMultipleSelection() {
        flagSelectAll = true
        (mContext as? MainActivity)?.changeTitle("0")
        check = 0
//        imagesAdapter.clearCount()
        imagesAdapter.selectAllItem(false)
        imagesAdapter.notifyDataSetChanged()

    }

    fun zipList() {
        var file: Array<File>? = null
        val zipFiles = imagesAdapter.getZipSelectedItems()
        val fileList: MutableList<File> = mutableListOf()
        for (i in zipFiles) {
            val f = File(i.filePath)
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
/*fun requestRuntimePermission(){

    val uriList: ArrayList<Uri> = ArrayList()
    uriList.add(
        ContentUris.withAppendedId(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        videos.get(position).getVideoId()));

}*/


    fun convertZip(selectedFiles: List<File>, targetPath: File) {
        try {
            val fos = FileOutputStream(targetPath)
            val bos = BufferedOutputStream(fos)
            val zos = ZipOutputStream(bos)

            for (file in selectedFiles) {
                val fis = FileInputStream(file)
                val bis = BufferedInputStream(fis)
                val entry = ZipEntry(file.name)
                zos.putNextEntry(entry)

                var len: Int
                val buffer = ByteArray(1024)
                while (bis.read(buffer).also { len = it } > 0) {
                    zos.write(buffer, 0, len)
                }

                bis.close()
                zos.closeEntry()
            }

            zos.close()
            Log.d("ConvertZip", "Successfully created zip file at path: ${targetPath.absolutePath}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /*fun zipFiles(files: List<File>, targetFile: File) {
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
    }*/


}