package com.example.zipextractor.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "Gallery")
data class MainEntity(
    @PrimaryKey(autoGenerate = true)
    val fileId: Long,
    val fileName: String?,
    val filePath: String,
    val fileSize: String,
    val fileDuration: Long,
    val fileType: String,
) {
    @Ignore
    var multipleSelection: Boolean = false

}

