package com.example.hazelfilemanager.zipmanager

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log

import com.example.zipextectorapp.Zip.ZipCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

private const val TAG = "ZipManager"

class ZipManager(private val callback: ZipCallback) {
    companion object {
        private const val BUFFER_SIZE = 6 * 1024
    }

    fun zipFiles(list: List<File>, zipFile: File, context: Context) {
        callback.onStarted()
        CoroutineScope(Dispatchers.IO).launch {
            try {
              //  val out =context.contentResolver.openOutputStream(Uri.fromFile(zipFile))
                val out =context.contentResolver.openOutputStream(Uri.fromFile(File(zipFile.path)))
                val zos = ZipOutputStream(out)
                list.forEach {
                    if (it.isDirectory) {
                        zipFolder(zos, it, zipFile)
                    } else {
                        zipFile(it, zipFile, zos)
                    }
                }
                withContext(Dispatchers.Main) {
                    callback.onZipCompleted()
                }
                try {
                    zos.close()
                } catch (e: java.lang.Exception) {
                    Log.e(TAG, "zipFiles: stream close error: ", e)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onError(e)
                }
            }
        }
    }

    private var count = 0
    private fun zipFolder(zos: ZipOutputStream, inputFile: File, zipFile: File) {
        val fileList = inputFile.listFiles()
        for (file in fileList) {
// If it is a file
            Log.i(TAG, "zip: ${file.path}")
            if (file.isFile) {
                zipFile(file, zipFile, zos)
            } else if (file.isDirectory) {
                count++
                zipFolder(zos, file, zipFile)
            }
        }
    }

    private fun zipFile(file: File, zipFile: File, zos: ZipOutputStream) {
        val parentPath = zipFile.path.getParentPathFromFilePath()
        val pathForEntry = file.path.replace(parentPath, "")
        val ze = ZipEntry(pathForEntry)
        Log.i(TAG, "zip: pathEntry $pathForEntry")
        // Add the ZipEntry to the ZipOutputStream
        zos.putNextEntry(ze)
        // Write the contents of the file to the ZipOutputStream
        val fis = FileInputStream(file.path)
        val buffer = ByteArray(BUFFER_SIZE)
        var len: Int
        while (fis.read(buffer).also { len = it } > 0) {
            zos.write(buffer, 0, len)
        }
        // Close the ZipEntry
        zos.closeEntry()
        fis.close()
    }

    fun String.getParentPathFromFilePath(): String {
        return this.subSequence(0, this.lastIndexOf("/")).toString()
    }
}