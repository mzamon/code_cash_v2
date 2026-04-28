package com.codecash.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ImageUtils {
    
    fun savePhoto(context: Context, bitmap: Bitmap): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "CODECASH_${timeStamp}.jpg"
        
        val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CodeCash")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        
        val file = File(directory, fileName)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        
        return file.absolutePath
    }
    
    fun loadPhoto(path: String): Bitmap? {
        return try {
            BitmapFactory.decodeFile(path)
        } catch (e: Exception) {
            null
        }
    }
    
    fun deletePhoto(path: String): Boolean {
        return try {
            File(path).delete()
        } catch (e: Exception) {
            false
        }
    }
    
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "JPEG_${timeStamp}_"
        val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CodeCash")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return File.createTempFile(fileName, ".jpg", directory)
    }
    
    fun getUriForFile(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
    
    fun getPhotoDirectory(context: Context): File {
        val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CodeCash")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }
}