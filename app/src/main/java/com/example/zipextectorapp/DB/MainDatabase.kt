package com.example.zipextractor.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.zipextectorapp.Dao.MainDao
import com.example.zipextractor.model.MainEntity

@Database(entities = [MainEntity::class], version = 1, exportSchema = false)
abstract class MainDatabase: RoomDatabase() {
    abstract fun galleryDao(): MainDao

    companion object {
        var dbInstance: MainDatabase? = null

        fun getDatabase(context: Context): MainDatabase {
            synchronized(this) {
                if (dbInstance == null) {
                    dbInstance =
                        Room.databaseBuilder(context, MainDatabase::class.java, "MyDB").fallbackToDestructiveMigration()
                            .build()
                }
            }
            return dbInstance!!
        }
    }
}