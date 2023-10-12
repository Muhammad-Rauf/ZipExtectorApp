package com.example.zipextectorapp.Utills

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(@StringRes id: Int) {
    Toast.makeText(this, id, Toast.LENGTH_SHORT).show()
}

fun debugLog(message: String) {
    Log.d("ZipExtractorDebugLog", message)
}

fun Long.toSize() : String {
    val byteUnits = listOf("B", "KB", "MB", "GB", "TB")
    var fileSize = this.toDouble()
    var index = 0
    while (fileSize >= 1024 && index < byteUnits.size - 1) {
        fileSize /= 1024
        index++
    }
    return "%.2f %s".format(fileSize, byteUnits[index])
}