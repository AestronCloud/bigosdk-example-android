package sg.bigo.common.data

import android.util.Log
import sg.bigo.common.LiveApplication
import java.io.File

private const val TAG = "StickerInfo"
data class StickerInfo(var using:Boolean = true) {
    var number: Int = 0
    var iconUrl: String = ""
    var effectId:Int = -1
    var name: String = ""
    var desc: String = ""
    var isCopyLike = true
    var resourceUrl = ""
    var materialId = ""
    var stickerType = -1
    var default_shrink_ratio = 0
    var face_recog = -1
    var st_action_ids = ""


    companion object {
        fun prase(msg:String): StickerInfo {
            val items = msg.split("\t")
            val stickerInfo = StickerInfo()
            stickerInfo.using = items[0] == "是"
            stickerInfo.number = items[1].toInt()
            stickerInfo.iconUrl = items[2].trim()
            stickerInfo.effectId = items[3].toInt()
            stickerInfo.name = items[4].trim()
            stickerInfo.desc = items[5].trim()
            stickerInfo.isCopyLike = items[6] == "是"
            stickerInfo.resourceUrl = items[7].trim()
            stickerInfo.materialId = items[8].trim()
            stickerInfo.stickerType = items[9].toInt()
            stickerInfo.default_shrink_ratio = items[10].toInt()
            stickerInfo.face_recog = items[11].toInt()
            stickerInfo.st_action_ids = items[12].trim()
            Log.e(TAG, "prase: ====>>> ${stickerInfo}")
            return stickerInfo
        }

        fun createNone(): StickerInfo {
            return StickerInfo().apply {
                number = -1
            }
        }
    }

    fun isNone(): Boolean {
        return number == -1
    }

    fun getStoreDir(): String {
        return "${LiveApplication.appContext!!.getExternalFilesDir(null)}/stickers/"
    }

    fun getUnZipDir(): String {
        return "${getStoreDir()}${effectId}"
    }

    fun getUnzipedFileDir(): String {
        val firstZipDir = File(getUnZipDir())
        if (!firstZipDir.exists()) return ""
        if (!firstZipDir.isDirectory) return ""
        val subFiles = firstZipDir.listFiles()
        if(subFiles == null || subFiles.isEmpty()) return ""

        return subFiles[0].absolutePath
    }

    fun getResourcePath(): String {
        return "${getStoreDir()}${effectId}.zip"
    }

    fun getIconPath(): String {
        return "${getStoreDir()}${effectId}.png"
    }

    override fun toString(): String {
        return "StickerInfo(using=$using, number=$number, iconUrl='$iconUrl', effectId=$effectId, desc='$name', isCopyLike=$isCopyLike, resourceUrl='$resourceUrl', materialId=$materialId, stickerType=$stickerType, default_shrink_ratio=$default_shrink_ratio, face_recog=$face_recog, st_action_ids='$st_action_ids')"
    }


}