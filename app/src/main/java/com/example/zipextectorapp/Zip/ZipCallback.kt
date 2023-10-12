package com.example.zipextectorapp.Zip

interface ZipCallback {
    fun onStarted()
    fun onZipCompleted()
    fun onUnzipCompleted()
    fun onError(throwable: Throwable?)
}