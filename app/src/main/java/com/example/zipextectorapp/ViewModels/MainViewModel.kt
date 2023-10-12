package com.example.zipextectorapp.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.zipextectorapp.Repository.MainRepository
import com.example.zipextractor.database.MainDatabase
import com.example.zipextractor.model.MainEntity
import kotlinx.coroutines.launch

class MainViewModel(application: Application):  (application) {

    private val repository: MainRepository

    init {
        val galleryDao = MainDatabase.getDatabase(application).galleryDao()
        repository = MainRepository(galleryDao)
    }

    fun saveFilesData(galleryEntity: MainEntity) = viewModelScope.launch {
        repository.saveFilesData(galleryEntity)
    }
    fun getAllFiles(fileType: String):LiveData<List<MainEntity>> {
        return repository.getAllFiles(fileType)
    }
}
