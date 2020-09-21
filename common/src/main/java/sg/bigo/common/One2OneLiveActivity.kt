package sg.bigo.common

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_1v1_live2.*
import sg.bigo.common.annotation.NonNull
import sg.bigo.common.utils.ToastUtils
import sg.bigo.opensdk.api.AVEngineConstant
import sg.bigo.opensdk.api.IAVEngine
import sg.bigo.opensdk.api.IAVEngineCallback
import sg.bigo.opensdk.api.IDeveloperMock
import sg.bigo.opensdk.api.callback.JoinChannelCallback
import sg.bigo.opensdk.api.callback.OnUserInfoNotifyCallback
import sg.bigo.opensdk.api.impl.JoinChannelCallbackParams
import sg.bigo.opensdk.api.struct.ChannelMicUser
import sg.bigo.opensdk.api.struct.UserInfo
import sg.bigo.opensdk.api.struct.VideoCanvas
import kotlin.properties.Delegates

private const val TAG = "One2OneLiveActivity"

open class One2OneLiveActivity : BaseActivity() {
    private val mUserName by lazy {
        intent.getStringExtra(KEY_USER_NAME)
    }

    private val mChannelName: String by lazy {
        intent.getStringExtra(KEY_CHANNEL_NAME)
    }

    val micUserMap = mutableMapOf<Long,ChannelMicUser?>()

    private var isBigMe: Boolean by Delegates.observable(false,{ _, _, new ->

        if(new) {
            //远端设小画布，自己设大画布
            mAVEngine.setupLocalVideo(VideoCanvas(mMyUid,canvas_big));

            if(micUserMap.contains(remoteUid)) {
                mAVEngine.setupRemoteVideo(VideoCanvas(remoteUid,canvas_small))
            }
        } else {
            //远端设大画布，自己设小画布
            if (micUserMap.contains(remoteUid)) {
                mAVEngine.setupRemoteVideo(VideoCanvas(remoteUid,canvas_big))
            }
            mAVEngine.setupLocalVideo(VideoCanvas(mMyUid,canvas_small));
        }
    })

    private fun showBroadCastView() {
        bottom_preview_area.visibility = View.GONE
        bottom_area.visibility = View.VISIBLE

        btn_swtich_camera.visibility = View.VISIBLE
        live_btn_mute_audio.visibility = View.VISIBLE
        live_btn_mute_video.visibility = View.VISIBLE
    }

    private fun showAudienceView() {
        bottom_preview_area.visibility = View.GONE
        bottom_area.visibility = View.VISIBLE

        btn_swtich_camera.visibility = View.GONE
        live_btn_mute_audio.visibility = View.GONE
        live_btn_mute_video.visibility = View.GONE
    }


    private fun showPreviewMode() {
        bottom_preview_area.visibility = View.VISIBLE
        bottom_area.visibility = View.GONE
    }



    private var mRole: Int by Delegates.observable(-1, { _, _, newValue ->
        runOnUiThread {
            if (isBroadcaster(newValue)) {
                showBroadCastView()
            } else {
                showAudienceView()
            }
        }
    })

    private var mMyUid: Long by Delegates.observable(0L, { _, _, newValue ->
        runOnUiThread {
            tvLiveInfo.text = "Livename=$mChannelName \nMyUid=$newValue"
        }
    })

    private var remoteUid: Long by Delegates.observable(0L, { _, _, _ ->

    })


    private val mAVEngine: IAVEngine = LiveApplication.avEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_1v1_live2)
        initDatas()
        initView()
        joinRoom()
    }

    private fun initDatas() {
        if(isRestart) {
            leaveChannel()
        }
        mAVEngine.addCallback(mLiveCallback)
        mAVEngine.setChannelProfile(AVEngineConstant.ChannelProfile.CHANNEL_PROFILE_COMMUNICATION)
        mRole = intent.getIntExtra(KEY_CLIENT_ROLE, AVEngineConstant.ClientRole.ROLE_BROADCAST)
    }

    open fun initView() {
        btn_swtich_camera.setOnClickListener { mAVEngine.switchCamera() }

        live_btn_mute_audio.isActivated = true
        live_btn_mute_audio.setOnClickListener { view ->
            mAVEngine.muteLocalAudioStream(view.isActivated)
            view.isActivated = !view.isActivated
        }

        live_btn_mute_video.isActivated = true
        live_btn_mute_video.setOnClickListener { view ->
            mAVEngine.muteLocalVideoStream(view.isActivated)
            view.isActivated = !view.isActivated
        }

        live_btn_up_mic.setOnClickListener {
            isBigMe = !isBigMe
        }

        live_btn_endcall.setOnClickListener {
            finish()
        }

        tv_debug_toggle.setOnClickListener {
            if(tv_video_debug_info.isShown) {
                tv_video_debug_info.visibility = View.GONE
            } else {
                tv_video_debug_info.visibility = View.VISIBLE
            }
        }

        btn_beauty.setOnClickListener {
            startActivity(Intent(this,BeautifyActivity::class.java))
        }

        btn_prew_beauty.setOnClickListener {
            startActivity(Intent(this,BeautifyActivity::class.java))
        }

        canvas_big.post {
            val l = canvas_big.width*2/3
            val r = canvas_big.width

            val t = canvas_big.height/5
            val b = canvas_big.height/5 + canvas_big.height/4

            val w = r - l
            val h = b - t

            canvas_small.layoutParams = (canvas_small.layoutParams as FrameLayout.LayoutParams).apply {
                width = w
                height = h
                setMargins(0,t,0,0)
            }
        }

        showPreviewMode()
    }

    private fun joinRoom() {
        mAVEngine.setChannelProfile(AVEngineConstant.ChannelProfile.CHANNEL_PROFILE_COMMUNICATION)
        mAVEngine.attachRendererView(bigoRendererView)

        start_call.setOnClickListener {
            mAVEngine.registerLocalUserAccount(mUserName,object :OnUserInfoNotifyCallback{
                override fun onNotifyUserInfo(user: UserInfo?) {
                    user?.let {
                        mMyUid = user.uid
                        mAVEngine.developerMock.getToken(mMyUid, mChannelName, mUserName, getString(R.string.bigo_cert), object : IDeveloperMock.CommonCallback<String> {
                            override fun onResult(token: String?) {
                                mAVEngine.joinChannel(token, mChannelName, mMyUid, mJoinChannelCallback)
                            }

                            override fun onError(reason: Int) {
                                println("$TAG getToken error $reason")
                            }
                        })
                    } ?: let {
                        ToastUtils.show("换取媒体uid失败")
                    }
                }

                override fun onFailed(code: Int) {
                    ToastUtils.show("换取媒体uid失败 $code")
                }
            })
        }

    }

    private val mJoinChannelCallback = object : JoinChannelCallback {
        override fun onSuccess(params: JoinChannelCallbackParams?) {
            mMyUid = params!!.uid
        }

        override fun onFailed(reason: Int) {
            Toast.makeText(this@One2OneLiveActivity, getString(R.string.tips_enter_room_failed), Toast.LENGTH_LONG).show()
            this@One2OneLiveActivity.finish()
        }

        override fun onTokenVerifyError(token: String?) {
            Toast.makeText(this@One2OneLiveActivity, getString(R.string.tips_enter_room_token_error), Toast.LENGTH_LONG).show()
            this@One2OneLiveActivity.finish()
        }
    }

    private val mLiveCallback: IAVEngineCallback = object : IAVEngineCallback() {
        override fun onClientRoleChanged(
            oldRole: Int,
            newRole: Int,
            clientRoleInfo: ChannelMicUser?
        ) {
            println("#onClientRoleChanged oldRole $oldRole newRole $newRole")
            mRole = newRole
            runOnUiThread {
                if(micUserMap.size >= 2 || micUserMap.containsKey(mMyUid)) {
                    return@runOnUiThread
                }

                clientRoleInfo?.let {
                    if (isBroadcaster(mRole)) {
                        if(micUserMap.isNotEmpty()) {
                            mAVEngine.setupLocalVideo(VideoCanvas(mMyUid, canvas_small))
                        } else {
                            mAVEngine.setupLocalVideo(VideoCanvas(mMyUid, canvas_big))
                        }
                        micUserMap[clientRoleInfo.uid] = clientRoleInfo
                    } else {
                        mAVEngine.setupLocalVideo(VideoCanvas(mMyUid, null))
                    }
                }
            }
        }

        override fun onUserJoined(@NonNull user: ChannelMicUser, elapsed: Int) {
            println("#onUserJoined $user")
            runOnUiThread {
                if(micUserMap.size >= 2 || micUserMap.containsKey(user.uid)) return@runOnUiThread
                println("#onUserJoined2 $user")
                if(micUserMap.containsKey(mMyUid)) {
                    mAVEngine.setupLocalVideo(VideoCanvas(mMyUid, canvas_small))
                }
                mAVEngine.setupRemoteVideo(VideoCanvas(user.uid, canvas_big))
                micUserMap[user.uid] = user
                remoteUid = user.uid
            }
        }

        override fun onUserOffline(@NonNull user: ChannelMicUser, reason: Int) {
            println("#onUserOffline $user")
            runOnUiThread {
                if (!micUserMap.contains(user.uid)) {
                    return@runOnUiThread
                }

                if (user.uid == mMyUid) {
                    mAVEngine.setupLocalVideo(VideoCanvas(user.uid, null))
                } else {
                    remoteUid = 0
                    micUserMap.remove(user.uid)
                    mAVEngine.setupRemoteVideo(VideoCanvas(user.uid, null))
                    if(micUserMap.containsKey(mMyUid)) {
                        mAVEngine.setupLocalVideo(VideoCanvas(mMyUid,canvas_big))
                    }
                }
            }

        }

        override fun onError(err: Int) {
            println("$TAG sth error $err")
        }

        override fun onKicked(reason: Int) {
            ToastUtils.show(getString(R.string.tips_be_kicked_suggest))
            this@One2OneLiveActivity.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isBroadcaster(mRole)) {
            mAVEngine.startPreview()
        }
    }

    override fun onStop() {
        mAVEngine.stopPreview()
        super.onStop()
        if (isFinishing || isChangingConfigurations) {
            leaveChannel()
        }
    }

    private fun leaveChannel() {
        clearAllBeautyConfig()
        mAVEngine.removeCallback(mLiveCallback)
        mAVEngine.leaveChannel()
    }

    override fun showDebugInfo(msg: String) {
        tv_video_debug_info.text = msg
    }
}