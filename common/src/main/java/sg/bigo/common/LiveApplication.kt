package sg.bigo.common

import android.app.Application
import android.content.Context
import sg.bigo.common.utils.BigoEngineConfig
import sg.bigo.opensdk.api.IAVEngine
import sg.bigo.opensdk.api.IAVEngineCallback
import sg.bigo.opensdk.rtm.TestEnv

class LiveApplication : Application() {

    companion object {

        private var sInstance: IAVEngine? = null

        val config: BigoEngineConfig by lazy {
            BigoEngineConfig()
        }

        var appContext: Context? = null

        val  appId : String by lazy {
            config.appId
        }

        val cert : String by lazy {
            config.cert
        }

        val needCustomUpsideDown : Int by lazy {
            config.isEnableCustomCaptureUpsideDown
        }

        val needCustomMirror : Boolean by lazy {
            config.isEnableCustomCaptureMirror
        }

        fun avEngine(): IAVEngine {
            if(sInstance == null) {
                sInstance = IAVEngine.create(appContext, appId, null, object : IAVEngineCallback() {}, object : TestEnv{
                    override fun isTestMode(): Boolean {
                        return config.isCustomEnvEnabled
                    }
                    override fun getTestLbsIp(): String {
                        return config.lbsIp
                    }
                    override fun isTestEnv(): Boolean {
                        return config.isTestEnv
                    }
                    override fun getTestLbsPort(): Int {
                        return config.lbsPort
                    }
                })

                //接入方无需自定义环境，可以直接使用如下方式创建IAVEngine实例
                //sInstance = IAVEngine.create(appContext, appId, object :IAVEngineCallback() {})
                sInstance!!.enableDebug(true)
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