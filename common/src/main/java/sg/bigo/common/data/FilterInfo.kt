package sg.bigo.common.data

import android.util.Log
import org.json.JSONObject
import sg.bigo.common.LiveApplication

private const val TAG = "FilterInfo"
data class FilterInfo(var defaultStrength: Int, val filterType: Int, val iconUrl: String, val id: String, val tobId: Int, val name: String, val resourceUrl: String, val resourceName: String) {

    companion object  {
        fun prase(jsonStr: String): FilterInfo {
            val json = JSONObject(jsonStr)
            val defaultStrength = json.optInt("defaultStrength",0)
            val filterType = json.optInt("filterType",0)
            val iconUrl = json.optString("icon","")
            val id = json.optString("id","")
            val tobId = json.optInt("tob_id",-1)
            val name = json.optString("name","")
            val resourceUrl = json.optString("resource","")
            val resourceName = json.optString("resourceName","")
            val info = FilterInfo(defaultStrength,filterType,iconUrl,id,tobId,name,resourceUrl,resourceName)
            Log.e(TAG, "prase: info " + info)
            return info
        }
    }

    fun getResourcePath(): String {
        return "${LiveApplication.appContext!!.getExternalFilesDir(null)}/filter/${resourceName}"
    }

    fun getIconPath(): String {
        return "${LiveApplication.appContext!!.getExternalFilesDir(null)}/filter/icon_${resourceName}"
    }

    override fun toString(): String {
        return "FilterInfo(defaultStrength=$defaultStrength, filterType=$filterType, iconUrl='$iconUrl', id='$id', tobId=$tobId, name='$name', resourceUrl='$resourceUrl', resourceName='$resourceName')"
    }

}