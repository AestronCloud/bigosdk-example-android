package sg.bigo.common.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PremissionProcesser {

    const val BASE_VALUE_PERMISSION = 0X0001
    const val PERMISSION_REQ_ID_RECORD_AUDIO = BASE_VALUE_PERMISSION + 1
    const val PERMISSION_REQ_ID_CAMERA = BASE_VALUE_PERMISSION + 2
    const val PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE = BASE_VALUE_PERMISSION + 3
    const val PERMISSION_REQ_ID_READ_PHONE_STATE = BASE_VALUE_PERMISSION + 4

    fun checkSelfPermissions(activity: Activity): Boolean {
        return checkSelfPermission(activity,Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) &&
                checkSelfPermission(activity,Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA) &&
                checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE) &&
                checkSelfPermission(activity,Manifest.permission.READ_PHONE_STATE, PERMISSION_REQ_ID_READ_PHONE_STATE)
    }

    @Suppress("DEPRECATED_IDENTITY_EQUALS")
    private fun checkSelfPermission(activity: Activity, permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(activity, permission) !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
            return false
        }
        return true
    }

    fun onRequestPermissionsResult(activity: Activity, requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQ_ID_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(activity,Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA
                    )
                } else {
                    activity.finish()
                }
            }
            PERMISSION_REQ_ID_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE)
                } else {
                    activity.finish()
                }
            }
            PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    activity.finish()
                }
            }
        }
    }
}