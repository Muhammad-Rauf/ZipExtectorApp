package com.example.zipextectorapp.Fragments

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.code4rox.medialoaderx.ImageLoaderX
import com.example.zipextectorapp.Adapters.AppsAdapter
import com.example.zipextectorapp.Adapters.MainAdapter
import com.example.zipextectorapp.Models.AppModel
import com.example.zipextectorapp.R
import com.example.zipextectorapp.ViewModels.MainViewModel
import com.example.zipextractor.model.MainEntity
import com.hazel.applocker.utils.PermissionChecker

class AppsApkFragment : Fragment() {
    private var loadingDialog: Dialog? = null
    private var appsRecyclerView: RecyclerView? = null
    private lateinit var appsAdapter: AppsAdapter
    var appDataList: ArrayList<AppModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_apps_apk, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appsRecyclerView=view.findViewById(R.id.appsRecyclerView_id)
        appsRecyclerView?.layoutManager = LinearLayoutManager(requireActivity())

        showloadingDialog()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )

        {

            appDataList = fetchInstalledAppList(requireActivity())
            appsAdapter= AppsAdapter(requireActivity(),appDataList)
            appsRecyclerView?.adapter=appsAdapter



        }


    }

    override fun onResume() {
        super.onResume()
        PermissionChecker.checkUsageAccessPermission(requireActivity())
    }
    fun fetchInstalledAppList(context: Context): ArrayList<AppModel> {
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfoList = context.packageManager.queryIntentActivities(mainIntent, 0)
        resolveInfoList.forEach { resolveInfo ->
            with(resolveInfo) {
                if (activityInfo.packageName != context.packageName) {

                        activityInfo.name.substring(activityInfo.name.lastIndexOf(".") + 1)
                    val appData = AppModel(
                        appName  = loadLabel(context.packageManager) as String,
                        icon = loadIcon(context.packageManager),
                        packages = "${activityInfo.packageName}"
                    )
                    appDataList.add(appData)
                }
            }
        }

        return appDataList
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






}