package sg.bigo.common

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_1v1_voice.*
import kotlinx.android.synthetic.main.activity_1v1_voice.live_btn_mute_audio
import kotlinx.android.synthetic.main.activity_1v1_voice.tvLiveInfo
import kotlinx.android.synthetic.main.activity_1v1_voice.tv_debug_toggle
import kotlinx.android.synthetic.main.activity_1v1_voice.tv_video_debug_info
import sg.bigo.common.annotation.NonNull
import sg.bigo.common.utils.ToastUtils
import sg.bigo.opensdk.api.AVEngineConstant
import sg.bigo.opensdk.api.IAVEngine
import sg.bigo.opensdk.api.IAVEngineCallback
import sg.bigo.opensdk.api.IDeveloperMock
import sg.bigo.opensdk.api.callback.JoinChannelCallback
import sg.bigo.opensdk.api.callback.OnUserInfoNotifyCallback
import sg.bigo.opensdk.api.impl.ChannelInfo
import sg.bigo.opensdk.api.struct.ChannelMicUser
import sg.bigo.opensdk.api.struct.UserInfo
import kotlin.properties.Delegates

private const val TAG = "One2OneVoiceActivity"

class One2OneVoiceActivity : BaseActivity() {
    var remoteUser: ChannelMicUser? = null

    private val mUserName by lazy {
        intent.getStringExtra(KEY_USER_NAME)
    }

    private val mChannelName: String by lazy {
        intent.getStringExtra(KEY_CHANNEL_NAME)
    }


    private var mMyUid: Long by Delegates.observable(0L, { _, _, newValue ->
        runOnUiThread {
            tvLiveInfo.text = "Livename=$mChannelName \nMyUid=$newValue"
        }
    })

    private val mAVEngine: IAVEngine = LiveApplication.avEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_1v1_voice)
        initDatas()
        initView()
        joinRoom()
    }

    private fun initDatas() {
        if(isRestart) {
            leaveRoom()
        }

        mAVEngine.setChannelProfile(AVEngineConstant.ChannelProfile.COMMUNICATION)
        mAVEngine.addCallback(mLiveCallback)
    }

    //这里就是千篇一律的给每个按钮加响应
    private fun initView() {
        live_btn_mute_audio.isActivated = true
        live_btn_mute_audio.setOnClickListener { view ->
            mAVEngine.muteLocalAudioStream(view.isActivated)
            live_btn_mute_audio.isActivated = !live_btn_mute_audio.isActivated
        }

        live_btn_endcall.setOnClickListener {
            finish()
        }

        live_btn_speaklouder.isActivated = false
        live_btn_speaklouder.setOnClickListener {
            mAVEngine.setEnableSpeakerphone(!live_btn_speaklouder.isActivated)
            live_btn_speaklouder.isActivated = !live_btn_speaklouder.isActivated

            if (live_btn_speaklouder.isActivated) {
                live_btn_speaklouder.setColorFilter(
                    ContextCompat.getColor(this,R.color.blue),
                    PorterDuff.Mode.MULTIPLY
                )
            } else {
                live_btn_speaklouder.clearColorFilter()
            }
        }


        live_audio_quality_switch.isActivated = false
        live_audio_quality_switch.setOnClickListener {
            it.isActivated = !it.isActivated

            (if (it.isActivated) AVEngineConstant.AudioQuality.HD else AVEngineConstant.AudioQuality.FLUENT).let {
                mAVEngine.setAudioQuality(it)
            }
        }

        tv_debug_toggle.setOnClickListener {
            if(tv_video_debug_info.isShown) {
                tv_video_debug_info.visibility = View.GONE
            } else {
                tv_video_debug_info.visibility = View.VISIBLE
            }
        }
    }

    private fun joinRoom() {
        mAVEngine.enableLocalVideo(false)
        mAVEngine.setEnableSpeakerphone(false)
        mAVEngine.setChannelProfile(AVEngineConstant.ChannelProfile.COMMUNICATION)
        mAVEngine.registerLocalUserAccount(mUserName,object :OnUserInfoNotifyCallback{
            override fun onNotifyUserInfo(user: UserInfo?) {
                user?.let {
                    mMyUid = user.uid
                    mAVEngine.developerMock.getToken(mMyUid, mChannelName, mUserName, LiveApplication.cert, TokenCallbackProxy(object : IDeveloperMock.CommonCallback<String?> {
                        override fun onResult(token: String?) {
                            mAVEngine.joinChannel(token, mChannelName, mMyUid, LiveApplication.config.liveExtraInfo, mJoinChannelCallback)
                        }

                        override fun onError(reason: Int) {
                            println("$TAG getToken error $reason")
                        }
                    }))
                } ?: let {
                    ToastUtils.show("获取媒体uid失败")
                }
            }

            override fun onFailed(code: Int) {
                ToastUtils.show("获取媒体uid失败 $code")
            }
        })

    }

    private val mJoinChannelCallback = object : JoinChannelCallback {
        override fun onSuccess(params: ChannelInfo?) {
            mMyUid = params!!.uid
        }

        override fun onFailed(reason: Int) {
            ToastUtils.showJoinChannelErrTips(reason)
            this@One2OneVoiceActivity.finish()
        }

        override fun onTokenVerifyError(token: String?) {
            Toast.makeText(this@One2OneVoiceActivity, getString(R.string.tips_enter_room_token_error), Toast.LENGTH_LONG).show()
            this@One2OneVoiceActivity.finish()
        }
    }

    private val mLiveCallback: IAVEngineCallback = object : IAVEngineCallback() {
        override fun onClientRoleChanged(
            oldRole: Int,
            newRole: Int,
            clientRoleInfo: ChannelMicUser?
        ) {
            println("#onClientRoleChanged oldRole $oldRole newRole $newRole")
        }

        override fun onUserJoined(@NonNull user: ChannelMicUser, elapsed: Int) {
            println("#onUserJoined $user")
            remoteUser = user
        }

        override fun onUserOffline(@NonNull user: ChannelMicUser, reason: Int) {
            println("#onUserOffline $user")
        }

        override fun onError(err: Int) {
            println("$TAG sth error $err")
        }

        override fun onKicked() {
            ToastUtils.show(getString(R.string.tips_be_kicked_suggest))
            this@One2OneVoiceActivity.finish()
        }
    }

    override fun onStop() {
        super.onStop()
        if (isFinishing || isChangingConfigurations) {
            leaveRoom()
        }
    }

    private fun leaveRoom() {
        mAVEngine.enableLocalVideo(true)
        mAVEngine.removeCallback(mLiveCallback)
        mAVEngine.leaveChannel()
    }

    override fun showDebugInfo(msg: String) {
        tv_video_debug_info.setText(msg)
    }
}