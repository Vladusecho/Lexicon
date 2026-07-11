package com.vladusecho.lexicon.data.local

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject


class FileManagerHelper @Inject constructor(
    @field:ApplicationContext private val context: Context
) {

    fun saveImageToInternalStorage(imageUri: Uri): String? {

        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val fileName = "image_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)

            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}