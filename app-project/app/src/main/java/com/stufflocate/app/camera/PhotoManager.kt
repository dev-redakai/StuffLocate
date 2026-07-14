package com.stufflocate.app.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object PhotoManager {

  private const val MAX_PHOTO_SIZE_BYTES = 2 * 1024 * 1024L // 2MB
  private const val JPEG_QUALITY = 80
  private const val ITEM_PHOTOS_DIR = "item_photos"
  private const val LOCATION_PHOTOS_DIR = "location_photos"

  private fun getDir(context: Context, dirName: String): File {
    val dir = File(context.filesDir, dirName)
    if (!dir.exists()) dir.mkdirs()
    return dir
  }

  fun getItemPhotosDir(context: Context): File = getDir(context, ITEM_PHOTOS_DIR)

  fun getLocationPhotosDir(context: Context): File = getDir(context, LOCATION_PHOTOS_DIR)

  fun savePhoto(context: Context, sourceUri: Uri, dirName: String): String? {
    return try {
      val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
      val bitmap = BitmapFactory.decodeStream(inputStream)
      inputStream.close()
      saveCompressedBitmap(context, bitmap, dirName)
    } catch (e: Exception) {
      null
    }
  }

  fun savePhotoFromBitmap(context: Context, bitmap: Bitmap, dirName: String): String? {
    return try {
      saveCompressedBitmap(context, bitmap, dirName)
    } catch (e: Exception) {
      null
    }
  }

  private fun saveCompressedBitmap(context: Context, bitmap: Bitmap, dirName: String): String {
    val dir = getDir(context, dirName)
    val fileName = "${UUID.randomUUID()}.jpg"
    val file = File(dir, fileName)

    var quality = JPEG_QUALITY
    var outputStream: FileOutputStream

    do {
      outputStream = FileOutputStream(file)
      bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
      outputStream.flush()
      outputStream.close()
      quality -= 10
    } while (file.length() > MAX_PHOTO_SIZE_BYTES && quality > 10)

    return file.absolutePath
  }

  fun deletePhoto(context: Context, filePath: String): Boolean {
    return try {
      File(filePath).delete()
    } catch (e: Exception) {
      false
    }
  }

  fun deletePhotos(context: Context, filePaths: List<String>) {
    filePaths.forEach { deletePhoto(context, it) }
  }

  fun getPhotoFile(filePath: String): File? {
    val file = File(filePath)
    return if (file.exists()) file else null
  }

  fun getPhotoUri(filePath: String): Uri? {
    return getPhotoFile(filePath)?.let { Uri.fromFile(it) }
  }

  fun parsePhotoPaths(commaSeparated: String?): List<String> {
    if (commaSeparated.isNullOrBlank()) return emptyList()
    return commaSeparated.split(",").map { it.trim() }.filter { it.isNotBlank() }
  }

  fun toCommaSeparated(paths: List<String>): String? {
    return if (paths.isEmpty()) null else paths.joinToString(",")
  }
}
