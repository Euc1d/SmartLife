package com.example.smartlife.data

import android.content.Context
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class InternalStorageImageFileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val imagesDirection: File  = context.filesDir
    suspend fun saveToInternalStorage(url: String): String {

        val fileName = "InternalImage_${UUID.randomUUID()}.png"

        val file = File(imagesDirection, fileName)

        withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(url.toUri())?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
        return file.absolutePath
    }

    suspend fun deleteFromInternalStorage(uri: String) {
        withContext(Dispatchers.IO) {
            val file = File(uri)
            if (file.exists() && isInternal(file.absolutePath)){
                file.delete()
            }
        }
    }

    fun isInternal(uri: String): Boolean {
        return uri.startsWith(imagesDirection.absolutePath)
    }
}
