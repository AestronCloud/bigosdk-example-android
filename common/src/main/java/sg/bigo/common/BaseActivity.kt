package sg.bigo.common

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import sg.bigo.common.utils.WindowUtil
import sg.bigo.opensdk.api.AVEngineConstant
import sg.bigo.opensdk.api.IDeveloperMock
import sg.bigo.opensdk.utils.Log

abstract class BaseActivity : AppCompatActivity() {
    public val mUIHandler = Handler(Looper.getMainLooper())
    protected var mDestroyed = false

    private val loger: Log.ILog? by lazy {
        LiveApplication.avEngine().logger
    }

    protected var isRestart = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUIHandler.post(mDebugRunnable)
        isRestart = savedInstanceState != null
        if(isNeedHideStatusBar()) {
            WindowUtil.hideWindowStatusBar(window)
        }
    }

    protected fun isBroadcaster(role: Int): Boolean {
        return role == AVEngineConstant.ClientRole.ROLE_BROADCAST
    }

    open fun isNeedHideStatusBar() : Boolean { return true}

    private val mDebugRunnable: Runnable = object : Runnable {
        override fun run() {
            if (isFinishing || mDestroyed) {
                return
            }
            val sb = LiveApplication.avEngine().printDebugInfo()
            showDebugInfo(sb)
            mUIHandler.postDelayed(this, 1000)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        log("BaseActivity", "$this onBackPressed")
    }

    protected fun log(tag: String, msg: String) {
        loger?.let {
            it.i(tag,msg)
        } ?: println("$tag $msg")

    }

    open fun showDebugInfo(msg: String) {

    }

    override fun onStop() {
        mDestroyed = (isFinishing && !isChangingConfigurations)
        super.onStop()
    }

    class TokenCallbackProxy : IDeveloperMock.CommonCallback<String> {
        val callback: IDeveloperMock.CommonCallback<String?>

        constructor(callback: IDeveloperMock.CommonCallback<String?>) {
            this.callback = callback
        }

        override fun onResult(result: String?) {
            callback.onResult(LiveApplication.config.getFinalToken(result ?: ""))
        }

        override fun onError(errCode: Int) {
            callback.onError(errCode)
        }

    }

    fun clearAllBeautyConfig() {
        LiveApplication.avEngine().enableBeautyMode(false)
        LiveApplication.avEngine().setBeautifyFilter("",0)
        LiveApplication.avEngine().setBeautifyWhiteSkin(null,0)
        LiveApplication.avEngine().setSticker(null)
        LiveApplication.avEngine().setBeautifyBigEye(0)
        LiveApplication.avEngine().setBeautifyThinFace(0)
        LiveApplication.avEngine().setBeautifySmoothSkin(0)
    }
}
