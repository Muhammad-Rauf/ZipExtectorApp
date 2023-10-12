package com.example.zipextectorapp.Models

import android.net.Uri

data class CompressZipModel(
    val zipId: Long,
    val zipName: String,
    val zipSize: String,
    val zipPath: String,
    val zipUri: Uri,
    val fileType: String,
    var multipleSelection:Boolean = false

    )