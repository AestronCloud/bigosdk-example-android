package sg.bigo.common

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_1v1_live2.*
import sg.bigo.common.annotation.NonNull
import sg.bigo.common.utils.ScreenUtil
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
        mAVEngine.setChannelProfile(AVEngineConstant.ChannelProfile.COMMUNICATION)
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

        btn_stickers.setOnClickListener {
            startActivity(Intent(this,StickersActivity::class.java))
        }

        btn_prew_beauty.setOnClickListener {
            startActivity(Intent(this,BeautifyActivity::class.java))
        }
        
        btn_prew_stickers.setOnClickListener {
            startActivity(Intent(this,StickersActivity::class.java))
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

        initDragView()

        showPreviewMode()
    }



    private var screenWidth = ScreenUtil.getScreenWidth(LiveApplication.appContext)
    private var screenHeight = ScreenUtil.getScreenHeight(LiveApplication.appContext)
    private var downX = 0f
    private var downY = 0f
    var isDrag = false
        private set

    private fun initDragView() {
        canvas_small.setOnTouchListener(object :View.OnTouchListener{
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (canvas_small.isEnabled) {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            isDrag = false
                            downX = event.x
                            downY = event.y
                            Log.e("kid", "ACTION_DOWN ${downX} ${downY}")
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val xDistance = event.x - downX
                            val yDistance = event.y - downY
                            var l: Int
                            var r: Int
                            var t: Int
                            var b: Int
                            //当水平或者垂直滑动距离大于10,才算拖动事件
                            if (Math.abs(xDistance) > 10 || Math.abs(yDistance) > 10) {
                                isDrag = true
                                l = (v.left + xDistance).toInt()
                                r = l + v.width
                                t = (v.top + yDistance).toInt()
                                b = t + v.height
                                //不划出边界判断,此处应按照项目实际情况,因为本项目需求移动的位置是手机全屏,
                                // 所以才能这么写,如果是固定区域,要得到父控件的宽高位置后再做处理
                                if (l < 0) {
                                    l = 0
                                    r = l + v.width
                                } else if (r > screenWidth) {
                                    r = screenWidth
                                    l = r - v.height
                                }
                                if (t < 0) {
                                    t = 0
                                    b = t + v.width
                                } else if (b > screenHeight) {
                                    b = screenHeight
                                    t = b - v.height
                                }
                                canvas_small.layoutParams = (canvas_small.layoutParams as FrameLayout.LayoutParams).apply {
                                    setMargins(0,t,screenWidth - r,0)
                                }
                            }
                        }
                        MotionEvent.ACTION_UP -> v.isPressed = false
                        MotionEvent.ACTION_CANCEL -> v.isPressed = false
                    }
                    return true
                }
                return false
            }
        })
    }

    private fun joinRoom() {
        mAVEngine.setChannelProfile(AVEngineConstant.ChannelProfile.COMMUNICATION)
        mAVEngine.attachRendererView(bigoRendererView)

        start_call.setOnClickListener {
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
        override fun onSuccess(params: ChannelInfo?) {
            mMyUid = params!!.uid
        }

        override fun onFailed(reason: Int) {
            ToastUtils.showJoinChannelErrTips(reason)
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
                            mAVEngine.setupRemoteVideo(VideoCanvas(remoteUid,canvas_big))
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
                if(remoteUid > 0) return@runOnUiThread
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

        override fun onKicked() {
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
        remoteUid = 0
        mAVEngine.removeCallback(mLiveCallback)
        mAVEngine.leaveChannel()
    }

    override fun showDebugInfo(msg: String) {
        tv_video_debug_info.text = msg
    }
}