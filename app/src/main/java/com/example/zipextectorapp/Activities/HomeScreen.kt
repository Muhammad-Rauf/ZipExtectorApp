package com.example.zipextectorapp.Activities

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.code4rox.medialoaderx.AudioLoaderX
import com.code4rox.medialoaderx.ImageLoaderX
import com.code4rox.medialoaderx.VideoLoaderX
import com.example.zipextectorapp.Adapters.HomeFoldersAdapter
import com.example.zipextectorapp.R
import com.example.zipextectorapp.ViewModels.MainViewModel
import com.example.zipextectorapp.databinding.ActivityHomeScreenBinding
import com.example.zipextractor.model.MainEntity
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.request.ExplainScope
import com.permissionx.guolindev.request.ForwardScope
import java.io.File


class HomeScreen : AppCompatActivity() {
    private var deniedPermDialog: Dialog? = null
    private var adapter: HomeFoldersAdapter? = null
    private var imagesViewModel: MainViewModel? = null
    private lateinit var binding: ActivityHomeScreenBinding
    private val permIntentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED)
        {
            Log.d("PermissionAndroid11", " IntentLauncher Called ")
            showFolderData()
            saveImagesFilesData()
            saveVideosfilesData()
            saveAudiosfilesData()


        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imagesViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.homeRecyclerViewId.layoutManager = LinearLayoutManager(this)
        showFolderData()

        val text = "<font color=#ffffff>Archive </font> <font color=#000000>Files</font>"
        binding.titleToolbarId.text = Html.fromHtml(text)

        binding.imagesId.setOnClickListener {
            val intent = Intent(this, AllimagesScreen::class.java)
            startActivity(intent)
        }
        binding.videosId.setOnClickListener {
            val intent = Intent(this, AllVideosScreen::class.java)
            startActivity(intent)
        }

        binding.audiosId.setOnClickListener {
            val intent = Intent(this, AllAudiosScreen::class.java)
            startActivity(intent)
        }
        binding.documentsId.setOnClickListener {
            val intent = Intent(this, AllDocumentsScreen::class.java)
            startActivity(intent)
        }

        binding.archivesId.setOnClickListener {
            val intent = Intent(this, AllZipScreen::class.java)
            startActivity(intent)
        }
        binding.apkFilesId.setOnClickListener {
            val intent = Intent(this, AllApkFileScreen::class.java)
            startActivity(intent)
        }

        manageAllExternallStoragePermission()
    }

    private fun manageAllExternallStoragePermission() {
 /*       ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ),
            10
        )*/
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                !Environment.isExternalStorageManager()
            } else {
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED

            }
        )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Environment.isExternalStorageManager()
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                permIntentLauncher.launch(intent)
              //  startActivityForResult(intent,10)
            } else {
                checkPermissions()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==10 && resultCode== RESULT_OK){
            Log.d("PermissionAndroid11", " RESULT_OK AND REQUEST_CODE_OK ")
            showFolderData()
            saveImagesFilesData()
            saveVideosfilesData()
            saveAudiosfilesData()
        }
    }
    private fun checkPermissions() {
        PermissionX.init(this).permissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .onForwardToSettings { scope: ForwardScope?, deniedList: List<String?>? ->
                showDeniedPermissionDialog()
                Log.d("CheckPermission", " Permission denied 1: ")
            }
            .onExplainRequestReason { scope: ExplainScope?, deniedList: List<String?>? ->
                Log.d("CheckPermission", " Permission denied 2: ")
            }
            .request { allGranted: Boolean, grantedList: List<String?>?, deniedList: List<String?>? ->
                if (allGranted) {
                    Log.d("CheckPermission", "Permisssion Granted: ")
                    showFolderData()
                    saveImagesFilesData()
                    saveVideosfilesData()
                   saveAudiosfilesData()

                }

            }

    }

    private fun showDeniedPermissionDialog() {
        deniedPermDialog = Dialog(this, R.style.CustomDialog1)
        deniedPermDialog?.setContentView(R.layout.layout_dialog_nopermission)
        val tvButton = deniedPermDialog?.findViewById<TextView>(R.id.tv_allow_permission)
        // permDialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        deniedPermDialog?.setCancelable(true)
        tvButton?.setOnClickListener { v: View? ->
            val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", this.packageName, null)
            intent.data = uri
         //   permIntentLauncher.launch(intent)
            deniedPermDialog?.dismiss()
        }
        deniedPermDialog?.findViewById<View>(R.id.tv_cancel_permission)
            ?.setOnClickListener { deniedPermDialog?.dismiss() }
        if (!this.isFinishing && !this.isChangingConfigurations) {

            deniedPermDialog?.show()
        }
    }

    fun showFolderData() {
        val path = Environment.getExternalStorageDirectory().path
        val root = File(path)
        val filesAndFolders = root.listFiles()
        if (filesAndFolders == null || filesAndFolders.isEmpty()) {
            return
        }
        binding.homeRecyclerViewId.adapter = HomeFoldersAdapter(this, filesAndFolders, false)
    }

    private fun saveImagesFilesData() {
        ImageLoaderX(this).getAllImages({ images ->
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

    private fun saveVideosfilesData() {
        VideoLoaderX(this).getAllVideos({ videos ->
            for (i in 0 until videos.size) {
                val fileID: Long = videos[i].videoId
                val fileTitle: String = videos[i].title
                val filePath: String = videos[i].path
                val fileSize: Long = videos[i].size
                var videoSize = calculatefileSize(fileSize)
                var videoDuration: Long = videos[i].duration


                val videos = MainEntity(
                    fileID,
                    fileTitle,
                    filePath,
                    videoSize,
                    videoDuration,
                    "videos"
                )
                imagesViewModel?.saveFilesData(videos)
            }
        }, { videoFolders ->

        })


    }

    private fun saveAudiosfilesData() {
        AudioLoaderX(this).getAllAudios({ audios ->
            for (i in 0 until audios.size) {
                val fileID: Long = audios[i].audioId
                val fileTitle: String = audios[i].title
                val filePath: String = audios[i].path
                val fileSize: Long = audios[i].size
                val videoSize = calculatefileSize(fileSize)
                val videoDuration: Long = audios[i].duration


                val audios = MainEntity(
                    fileID,
                    fileTitle,
                    filePath,
                    videoSize,
                    videoDuration,
                    "audios"
                )
                imagesViewModel?.saveFilesData(audios)
            }
        }, { videoFolders ->

        })


    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setIcon(R.drawable.zip_icon)
            .setTitle("   Exit app...! ")
            .setMessage("Are you sure you want to close this App?")
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, which -> finishAffinity() })
            .setNegativeButton("No", null)
            .show()
    }
}