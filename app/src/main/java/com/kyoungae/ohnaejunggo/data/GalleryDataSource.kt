package com.kyoungae.ohnaejunggo.data

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.PermissionChecker
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermissionResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import com.gun0912.tedpermission.normal.TedPermission
import com.kyoungae.ohnaejunggo.activity.MainActivity





class GalleryDataSource @Inject constructor(
    @ApplicationContext private val context: Context

) {

    fun getData(): MutableList<Gallery> {
        val imageList = mutableListOf<Gallery>()

        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATA
        )
// Display videos in alphabetical order based on their display name.
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        val query = context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )
        query?.use { cursor ->
            // Cache column indices.
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val size = cursor.getInt(sizeColumn)
                val path = cursor.getString(dataColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                // Stores column values and the contentUri in a local object
                // that represents the media file.
                imageList += Gallery(path, name, size, false)
            }
        }
        Log.d(TAG, "getData: ${imageList[0].contentUri}")
        return imageList
    }

    suspend fun permissionCheck(result: (Boolean) -> Unit) {

        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                result(true)
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                result(false)
            }
        }

        TedPermission.create()
            .setPermissionListener(permissionlistener)
            .setRationaleMessage("사진을 가져오기 위해서는 사진 접근 권한이 필요해요.")
            .setDeniedMessage("접근 거부하셨습니다.\n[설정] - [권한]에서 권한을 허용해주세요.")
            .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()

    }

    companion object {
        private const val TAG = "GalleryDataSource"
    }
}