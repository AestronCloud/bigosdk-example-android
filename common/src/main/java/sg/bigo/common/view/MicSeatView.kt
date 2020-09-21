package sg.bigo.common.view

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import sg.bigo.common.LiveApplication
import sg.bigo.common.R
import sg.bigo.opensdk.api.callback.OnUserInfoNotifyCallback
import sg.bigo.opensdk.api.struct.RendererCanvas
import sg.bigo.opensdk.api.struct.UserInfo


class MicSeatView(context: Context, uid: Long) : FrameLayout(context) {

    private var mTvDebugInfo: TextView? = null
    private var mRendererCanvas: RendererCanvas? = null
    private var mDebugInfo: String? = null
    var uid: Long = 0

    init {
        init(context, uid)
    }

    private fun init(context: Context, uid: Long) {
        var params = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )

        mRendererCanvas = RendererCanvas(context)
        addView(mRendererCanvas, params)

        mTvDebugInfo = TextView(context)
        mTvDebugInfo!!.textSize = 14f
        mTvDebugInfo!!.setTextColor(ContextCompat.getColor(context,R.color.white))

        params = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )
        params.setMargins(30, 30, 30, 30)
        addView(mTvDebugInfo, params)

        mRendererCanvas!!.setOnClickListener {
            if (mTvDebugInfo!!.visibility == View.GONE) {
                mTvDebugInfo!!.visibility = View.VISIBLE
            } else {
                mTvDebugInfo!!.visibility = View.GONE
            }
        }

        this.uid = uid
        mTvDebugInfo!!.text = this.mDebugInfo
        mTvDebugInfo!!.visibility = View.GONE

        setInfo("")
        fetchUserInfo()
    }

    fun rendererCanvas(): RendererCanvas? {
        return mRendererCanvas
    }

    private fun fetchUserInfo() {
        var callback =  object : OnUserInfoNotifyCallback {
            override fun onNotifyUserInfo(userInfo: UserInfo) {
                userInfo.let {
                    if(userInfo.uid == this@MicSeatView.uid) {
                        setInfo("[" + userInfo.userAccount + "]");
                    }
                }
            }

            override fun onFailed(rescode: Int) {

            }
        }
        LiveApplication.avEngine().getUserInfoByUid(this.uid, callback);
    }

    private fun setInfo(info: String) {
        this.mDebugInfo = "[$uid]\n"
        this.mDebugInfo += info
        mTvDebugInfo!!.text = this.mDebugInfo
    }
}
