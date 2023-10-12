package com.example.zipextectorapp.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.zipextractor.model.MainEntity

@Dao
interface MainDao {

    @Query("SELECT * FROM Gallery where fileType=:fileType ORDER BY fileId DESC")
    fun getAllFiles(fileType:String): LiveData<List<MainEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFilesData(images: MainEntity)


}