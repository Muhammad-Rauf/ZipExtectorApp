package com.example.zipextectorapp.Adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.recyclerview.widget.RecyclerView
import com.example.zipextectorapp.Activities.FolderListScreen
import com.example.zipextectorapp.R
import com.example.zipextectorapp.databinding.RecyclerItemBinding
import java.io.File

class HomeFoldersAdapter(val context: Context, var filesAndFolders: Array<File>?, private var isFolder: Boolean = false
) : RecyclerView.Adapter<HomeFoldersAdapter.ViewHolder>() {

    private var newposition = 0

    //   var videolist = ArrayList<delete>()
    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    val selectedFile = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = RecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val selectedFile = filesAndFolders!![position]

        holder.bind(filesAndFolders!![position])
       // holder.delete(filesAndFolders!![position])

        holder.itemView.setOnClickListener {

            if (selectedFile.isDirectory) {
                val intent = Intent(context, FolderListScreen::class.java)
                val path = selectedFile.absolutePath
                intent.putExtra("path", path)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } else {
                //open thte file
                try {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    val type = "image/*"
                    intent.setDataAndType(Uri.parse(selectedFile.absolutePath), type)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        context.applicationContext,
                        "Cannot open the file",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return filesAndFolders!!.size
    }

    inner class ViewHolder(private val binding: RecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(filesAndFolders: File) {
            with(binding) {
                if (filesAndFolders.isDirectory) {
                    iconView.setImageResource(R.drawable.ic_baseline_folder_24)
                } else {
                    iconView.setImageResource(R.drawable.ic_baseline_insert_drive_file_24)
                }
                fileNameTextView.text = filesAndFolders.name
            }
        }
    }
}

/*        fun delete(filesAndFolders: File) {
            itemView.setOnLongClickListener { v ->
                val popupMenu = PopupMenu(context, v)
                newposition = position
                popupMenu.menu.add("DELETE")
                popupMenu.menu.add("RENAME")
                popupMenu.setOnMenuItemClickListener { item ->
                    if (item.title == "DELETE") {
                        requestdeleteR(position = position)

//                        val fIS = FileInputStream(File("/Dir/data.txt"))
//                        fIS.close()
//                        val file: File = context.getFileStreamPath(filesAndFolders!!.path)
//                        file.delete()
                    }
                    if (item.title == "RENAME") {
                        val currentfile = File(filesAndFolders!!.path)
                        val newName = binding.fileNameTextView.text
                        if (newName != null && currentfile.exists() && newName.toString()
                                .isNotEmpty()
                        ) {
                            val newFile = File(
                                currentfile.parentFile,
                                newName.toString() + "0" + currentfile.extension
                            )
                            if (currentfile.renameTo(newFile)) {
                                MediaScannerConnection.scanFile(
                                    context, arrayOf(newFile.toString()),
                                    arrayOf("file/*"), null
                                )
                                //        MainActivity.= newName.toString()
                            } else {
                                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        val oldfile: File = context.getFileStreamPath("path")
                        val newfile: File = context.getFileStreamPath("path")
                        oldfile.renameTo(newfile)
                    }
                    true
                }
                popupMenu.show()
                true
        }

    }}
        private fun requestdeleteR(position: Int) {
            val filelist: List<Uri> = listOf(withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videolist!![position].id))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val pi = MediaStore.createDeleteRequest(context.contentResolver, filelist)
                (context as Activity).startIntentSenderForResult(pi.intentSender, 123,
                    null, 0, 0, 0, null )
            }
            else {
                val file = File(videolist!![position].path)
                val builder = MaterialAlertDialogBuilder(context)
                builder.setTitle("Delete File")
                    .setMessage(videolist!![position].title)
                    .setPositiveButton("yes") { self, _ ->
                        if (file.exists() && file.delete()) {
                            MediaScannerConnection.scanFile(context, arrayOf(file.path), null, null)
                            //    MainActivity.filesAndFolders.removeAt(position)
                            //    notifyDataSetChanged()
                            updateDeleteUI(position = position)
                        }
                        self.dismiss()
                    }
                    .setNegativeButton("No") { self, _ -> self.dismiss() }
                val delDialog = builder.create()
                delDialog.show()
                delDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(
                    MaterialColors.getColor(context, R.attr.themeColor, Color.RED)
                )
                delDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(
                    MaterialColors.getColor(context, R.attr.themeColor, Color.RED)
                )
            }
        }


        @SuppressLint("NotifyDataSetChanged")
        private fun updateDeleteUI(position: Int) {
            when {
                MainActivity.search -> {
                   // MainActivity.videolist= getAllVideos(context)
                    MainActivity.dataChanged = true
                    videolist.removeAt(position)
                    notifyDataSetChanged()
                }
                 isFolder -> {
                    MainActivity.dataChanged = true
                    FileListActivity.currentFile.removeAt(position)
                    notifyDataSetChanged()
                }
                else -> {
                    MainActivity.videolist.removeAt(position)
                    notifyDataSetChanged()
                }
            }

        }*/


    fun onResult(requestCode:Int,resultCode:Int){
        when(requestCode){
            123-> {
                if (requestCode==Activity.RESULT_OK) updateDeleteUI(newposition)
            }
        }
    }
}
/*        private fun requestpermissionR(){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                if (!Environment.isExternalStorageManager()){
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data = Uri.parse("package:${context.applicationContext.packageName}")
                    ContextCompat.startActivity(context,intent,null)
                }
            }
        }*/
//    }
//}
 */
