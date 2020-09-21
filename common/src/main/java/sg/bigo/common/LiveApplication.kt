package sg.bigo.common

import android.app.Application
import android.content.Context
import kotlinx.android.synthetic.main.activity_live_name_input.*
import sg.bigo.common.utils.BigoEngineConfig
import sg.bigo.opensdk.api.IAVEngine
import sg.bigo.opensdk.api.IAVEngineCallback

class LiveApplication : Application() {

    companion object {
        private var sInstance: IAVEngine? = null

        val config: BigoEngineConfig by lazy {
            BigoEngineConfig()
        }

        var appContext: Context? = null

        fun avEngine(): IAVEngine {
            if(sInstance == null) {
                sInstance = IAVEngine.create(appContext, appContext!!.getString(R.string.bigo_app_id), object : IAVEngineCallback() {})
                sInstance!!.enableDebug(false)
            }
            return sInstance!!
        }


        enum class AppType {
            multilive,multivoice,one2onevideo,one2onevoice,sixseatvideolive,basicbeauty,basicbeauty1v1video,all
        }

        fun getAppType(): AppType {
            return when (appContext!!.packageName) {
                "sg.bigo.one2onevideo" -> AppType.one2onevideo
                "sg.bigo.one2onecall" -> AppType.one2onevoice
                "sg.bigo.multilive" -> AppType.multilive
                "sg.bigo.sixseatvoicelive" -> AppType.multivoice
                "sg.bigo.sixseatvideolive" -> AppType.sixseatvideolive
                "sg.bigo.basic_beauty" -> AppType.basicbeauty
                "sg.bigo.basic_beauty_1v1_video" -> AppType.basicbeauty1v1video
                else -> AppType.all
            }
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        appContext = base
    }

    override fun onCreate() {
        super.onCreate()
        BigoEngineConfig.loadAudioConfigs()
    }
}