package sg.bigo.common.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.layout_simple_mic.view.*
import sg.bigo.common.LiveApplication
import sg.bigo.common.R
import sg.bigo.opensdk.api.struct.RendererCanvas

/**
 * Description:
 * @author YangYongwen
 * Created on 2019-08-12
 */

class SimpleMicView : ConstraintLayout {

    private var serverAudioMuted = false
    private var serverVideoMuted = false

    private var localVideoMuted = false
    private var localAudioMuted = false

    private var uid = 0L

    private val avEngine = LiveApplication.avEngine()

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.layout_simple_mic, this, true)
    }

    fun getRendererCanvas(): RendererCanvas = renderer_canvas

    fun setUserInfo(uid: Long, micNum: Int, name: String) {
        this.uid = uid
        tv_user_info.text = "$name($micNum)"
        tv_uid.text = "uid: $uid"
    }

    fun setServerVideoMuteStatus(muted: Boolean) {
        serverVideoMuted = muted
        tv_server_mute_status.text = "远端--视频：${if (serverVideoMuted) "关" else "开"}，音频：${if (serverAudioMuted) "关" else "开"}"
    }

    fun setServerAudioMuteStatus(muted: Boolean) {
        serverAudioMuted = muted
        tv_server_mute_status.text = "远端--视频：${if (serverVideoMuted) "关" else "开"}，音频：${if (serverAudioMuted) "关" else "开"}"
    }

    fun enableMuteRemoteBtn() {
        textView3.visibility = View.VISIBLE
        iv_mute_audio.visibility = View.VISIBLE
        iv_mute_audio.setOnClickListener {
            localAudioMuted = !localAudioMuted
            avEngine.muteRemoteAudioStream(uid, localAudioMuted)
            iv_mute_audio.isSelected = localAudioMuted
        }

        iv_mute_video.visibility = View.VISIBLE
        iv_mute_video.setOnClickListener {
            localVideoMuted = !localVideoMuted
            avEngine.muteRemoteVideoStream(uid, localVideoMuted)
            iv_mute_video.isSelected = localVideoMuted
        }

    }
}