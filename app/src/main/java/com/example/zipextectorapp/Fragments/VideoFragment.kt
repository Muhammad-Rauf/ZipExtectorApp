package com.example.zipextectorapp.Fragments

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.code4rox.medialoaderx.VideoLoaderX
import com.example.zipextectorapp.Adapters.MainAdapter
import com.example.zipextectorapp.R
import com.example.zipextectorapp.Utills.debugLog
import com.example.zipextectorapp.ViewModels.MainViewModel
import com.example.zipextractor.model.MainEntity
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.request.ExplainScope
import com.permissionx.guolindev.request.ForwardScope

class VideoFragment : Fragment(),MainAdapter.SelectionHandler {
    private var loadingDialog: Dialog? = null
    private var deniedPermDialog: Dialog? = null
    private var checkPermissionDialog: Dialog? = null
    private lateinit var videoAdapter: MainAdapter
    private var videoRecycler: RecyclerView? = null
    private var videoList: ArrayList<MainEntity> = ArrayList()
    private  var videoViewModel: MainViewModel?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoRecycler=view.findViewById(R.id.videosRecyclerView_id)
        videoViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        videoRecycler?.layoutManager = LinearLayoutManager(requireActivity())
        showloadingDialog()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            loadingDialog?.show()

            savefilesData()
            Handler(Looper.getMainLooper()).postDelayed({

                videoRecycler?.layoutManager = LinearLayoutManager(requireActivity())
                videoViewModel?.getAllFiles("videos")?.observe(viewLifecycleOwner, Observer { list ->
                    videoList=list as ArrayList<MainEntity>
                    videoAdapter= MainAdapter(requireActivity(),videoList,this)
                    videoRecycler?.adapter=videoAdapter

                })
                loadingDialog?.dismiss()
            }, 1500)




        }
        else{
            checkPermissions()
        }
    }
    private fun checkPermissions() {
        PermissionX.init(this).permissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            .onForwardToSettings { scope: ForwardScope?, deniedList: List<String?>? ->
                showDeniedPermissionDialog()
                Log.d("CheckPermission", " Permission denied 1: ")
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                )
                {
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

                        videoRecycler?.layoutManager = LinearLayoutManager(requireActivity())
                        videoViewModel?.getAllFiles("videos")?.observe(viewLifecycleOwner, Observer { list ->
                            videoList=list as ArrayList<MainEntity>
                            videoAdapter= MainAdapter(requireActivity(),videoList,this)
                            videoRecycler?.adapter=videoAdapter

                        })
                        loadingDialog?.dismiss()
                    }, 1500)

                }

            }

    }
    ////////////////

    private fun savefilesData() {
        VideoLoaderX(requireActivity()).getAllVideos({ videos ->
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
                videoViewModel?.saveFilesData(videos)
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
    private fun showloadingDialog() {
        loadingDialog = Dialog(requireContext(), R.style.loadingDialog)
        loadingDialog?.setContentView(R.layout.loading_layout)
        loadingDialog?.window!!.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        loadingDialog?.setCancelable(true)
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
            val uri = Uri.fromParts("package", requireActivity().getPackageName(), null)
            intent.data = uri
           // permIntentLauncher.launch(intent)
            deniedPermDialog?.dismiss()
        }
        deniedPermDialog?.findViewById<View>(R.id.tv_cancel_permission)
            ?.setOnClickListener { deniedPermDialog?.dismiss() }
        if ( !requireActivity() .isFinishing && !requireActivity().isChangingConfigurations) {

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

        if ( !requireActivity() .isFinishing && !requireActivity().isChangingConfigurations) {

            checkPermissionDialog?.show()
        }
    }

    override fun onClick(position:Int) {
        debugLog("onClick")
    }

    override fun onSelectAll(isAllSelected : Boolean) {
        debugLog("onSelectAll")
    }

    override fun onLongClick() {
        debugLog("OnLongClick")
    }



}