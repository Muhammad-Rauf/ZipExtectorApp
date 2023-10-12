package com.example.zipextectorapp.Repository

import androidx.lifecycle.LiveData
import com.example.zipextectorapp.Dao.MainDao
import com.example.zipextractor.model.MainEntity

class MainRepository(private val galleryDao: MainDao) {

     fun getAllFiles(fileType: String):LiveData<List<MainEntity>> {
       return galleryDao.getAllFiles(fileType)
    }
    suspend fun saveFilesData(galleryEntity: MainEntity) {
        galleryDao.saveFilesData(galleryEntity)
    }


}