package sg.bigo.common.utils

import android.content.Context
import android.content.res.AssetManager
import sg.bigo.common.LiveApplication

object ResourceUtils {
    public val assetManager: AssetManager by lazy {
        LiveApplication.appContext!!.assets
    }

    public val ctx: Context by lazy {
        LiveApplication.appContext!!
    }
}