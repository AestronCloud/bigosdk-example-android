package sg.bigo.common

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.polly.mobile.mediasdk.KMediaRtmpStreamErrCode
import com.polly.mobile.mediasdk.KMediaRtmpStreamState
import kotlinx.android.synthetic.main.activity_live_pk.*
import kotlinx.android.synthetic.main.activity_live_pk.bottom_action_container
import kotlinx.android.synthetic.main.activity_live_pk.btn_swtich_camera
import kotlinx.android.synthetic.main.activity_live_pk.live_add_publish_stream_url
import kotlinx.android.synthetic.main.activity_live_pk.live_btn_mute_audio
import kotlinx.android.synthetic.main.activity_live_pk.live_btn_mute_video
import kotlinx.android.synthetic.main.activity_live_pk.live_remove_publish_stream_url
import kotlinx.android.synthetic.main.activity_live_pk.live_transcoding_cfg
import kotlinx.android.synthetic.main.activity_live_pk.live_transcoding_start
import kotlinx.android.synthetic.main.activity_live_pk.live_transcoding_stop
import kotlinx.android.synthetic.main.activity_live_pk.tv_debug_toggle
import kotlinx.android.synthetic.main.activity_live_pk.tv_video_debug_info
import kotlinx.android.synthetic.main.dialog_pk.view.*
import sg.bigo.common.fragment.TranscodingCfgFragment
import sg.bigo.common.utils.DialogUtils
import sg.bigo.common.utils.ToastUtils
import sg.bigo.common.utils.WindowUtil
import sg.bigo.common.view.SimpleMicView
import sg.bigo.opensdk.api.AVEngineConstant
import sg.bigo.opensdk.api.IAVEngineCallback
import sg.bigo.opensdk.api.IDeveloperMock
import sg.bigo.opensdk.api.callback.JoinChannelCallback
import sg.bigo.opensdk.api.callback.OnUserInfoNotifyCallback
import sg.bigo.opensdk.api.impl.ChannelInfo
import sg.bigo.opensdk.api.struct.*
import sg.bigo.opensdk.utils.Log
import kotlin.properties.Delegates

class LivePKActivity : BaseActivity() {

    companion object {
        private const val TAG = "LivePKActivity"
    }

    private val mUserName by lazy {
        intent.getStringExtra(KEY_USER_NAME)
    }

    private val mChannelName: String by lazy {
        intent.getStringExtra(KEY_CHANNEL_NAME)
    }

    private var mRole: Int by Delegates.observable(-1, { _, _, newValue ->

        runOnUiThread {
            if (isBroadcaster(newValue)) {
//                showBroadCastView()
            } else {
//                showAudienceView()
            }
        }
    })

    private var mMyUid: Long by Delegates.observable(0L, { _, _, newValue ->
        runOnUiThread {
            tv_video_debug_info.text = "Livename=$mChannelName \nMyUid=$newValue"
        }
    })


    private val mAVEngine = LiveApplication.avEngine()

    private var pkChannelName: String = ""
    private var joiningPk = false
    private val micUsers = mutableMapOf<Long, ChannelMicUser>()

    private val rtmpUrls = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_pk)
        initView()
        setupBackGround()
        joinChannel()
    }

    private fun initView() {
        tv_live_name.text = mChannelName
        tv_debug_toggle.setOnClickListener {
            tv_video_debug_info.visibility = if (tv_video_debug_info.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }


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

        icon_pk.setOnClickListener {
            if (!joiningPk) {
                showPKDialog()
            } else {
                exitPK()
            }
        }

        live_add_publish_stream_url.setOnClickListener {
            DialogUtils.showAddPublishStringUrlDialog(this, rtmpUrls)
        }

        live_remove_publish_stream_url.setOnClickListener {
            DialogUtils.showRemovePublishStringUrlDialog(this, rtmpUrls)
        }

        live_transcoding_cfg.setOnClickListener {
            startActivity(Intent(this,TranscodingCfgActivity::class.java))
        }

        live_transcoding_start.setOnClickListener {
//            if(!joiningPk) {
//                ToastUtils.show("未在PK状态,不用合流")
//                return@setOnClickListener
//            }
            mAVEngine.setLiveTranscoding(TranscodingCfgFragment.liveTranscoding);
            ToastUtils.show("触发合流")
        }

        live_transcoding_stop.setOnClickListener {
            mAVEngine.stopLiveTranscoding()
            ToastUtils.show("停止合流")
        }
    }

    private fun joinChannel() {
        if(isRestart) {
            mAVEngine.leavePKChannel()
            mAVEngine.leaveChannel()
        }

        mAVEngine.addCallback(mLiveCallback)
        mRole = intent.getIntExtra(KEY_CLIENT_ROLE, AVEngineConstant.ClientRole.ROLE_AUDIENCE)

        switchClientRole(mRole)
        mAVEngine.attachRendererView(sv_live_video)
        mAVEngine.registerLocalUserAccount(mUserName,object :OnUserInfoNotifyCallback{

            override fun onNotifyUserInfo(userInfo: UserInfo?) {
                Log.e(TAG,"onNotifyUserInfo startup failed $userInfo")
                userInfo?.let {
                    mMyUid = userInfo.uid
                    mAVEngine.developerMock.getToken(mMyUid,mChannelName,mUserName,LiveApplication.cert,TokenCallbackProxy(object: IDeveloperMock.CommonCallback<String?> {
                        override fun onResult(result: String?) {
                            result?.let {
                                mAVEngine.joinChannel(result, mChannelName, mMyUid, "extra info", object : JoinChannelCallback {
                                    override fun onSuccess(params: ChannelInfo) {

                                    }

                                    override fun onTokenVerifyError(token: String) {
                                    }

                                    override fun onFailed(code: Int) {

                                    }
                                })
                            } ?: ToastUtils.show("token获取失败")
                        }

                        override fun onError(errCode: Int) {
                            ToastUtils.show("token获取失败,错误码 ${errCode}")
                        }

                    }))
                }
            }

            override fun onFailed(code: Int) {
                ToastUtils.show("媒体uid注册失败,错误码 $code")
            }

        })
        mAVEngine.setAllVideoMaxEncodeParams(AVEngineConstant.MaxResolutionTypes.MR_1280x720,AVEngineConstant.FrameRate.FPS_24);
    }

    private fun switchClientRole(clientRole: Int) {
        mRole = clientRole
        mAVEngine.setClientRole(mRole)

    }

    private fun setupBackGround() {
        val drawable = ContextCompat.getDrawable(this, R.mipmap.backgournd) ?: return
        var width = drawable.intrinsicWidth
        var height = drawable.intrinsicHeight
        width = if (width > 0) width else 10
        height = if (height > 0) height else 10

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        sv_live_video.setBackgroundBitmap(bitmap, width, height)
    }

    private fun showPKDialog() {
        val pkInputView = LayoutInflater.from(this).inflate(R.layout.dialog_pk, null)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("输入PK信息")
        builder.setView(pkInputView)
        builder.setPositiveButton("确认") { _, _ ->
            pkChannelName = pkInputView.et_channel_name.text.toString()

            if (TextUtils.equals(pkChannelName,mChannelName)) {
                ToastUtils.show("无法与自己进行pk")
                return@setPositiveButton
            }

            mAVEngine.developerMock.getToken(mMyUid,pkChannelName,mUserName,LiveApplication.cert,TokenCallbackProxy(object:IDeveloperMock.CommonCallback<String?> {
                override fun onResult(result: String?) {
                    result?.let {
                        mAVEngine.joinPKChannel(result, pkChannelName, "", mMyUid, joinPkCallback)
                    } ?: ToastUtils.show("token获取失败")
                }

                override fun onError(errCode: Int) {
                    ToastUtils.show("token获取失败，错误码 $errCode")
                }
            }))
        }.setNegativeButton("取消") { _, _ -> }
        builder.show()
    }

    private fun exitPK() {
        mAVEngine.leavePKChannel()
        pkChannelName = ""
        joiningPk = false
    }

    override fun onResume() {
        super.onResume()
        if (isBroadcaster(mRole)) {
            mAVEngine.startPreview()
            bottom_action_container.visibility = View.VISIBLE
        } else {
            bottom_action_container.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        mAVEngine.stopPreview()
        log(TAG,"onStop $isFinishing $isChangingConfigurations")
        if (isFinishing || isChangingConfigurations) {
            leaveRoom()
        }
    }

    private fun leaveRoom() {
        log(TAG,"leaveRoom")
        clearAllBeautyConfig()
        mAVEngine.removeCallback(mLiveCallback)
        mAVEngine.leaveChannel()
    }




    private val joinPkCallback: JoinChannelCallback = object : JoinChannelCallback {

        override fun onSuccess(params: ChannelInfo) {
            runOnUiThread {
                handleJoinPK(params.uid)
            }
        }

        override fun onTokenVerifyError(_token: String) {

        }

        override fun onFailed(code: Int) {
            ToastUtils.show("加入跨房间pk失败 $code")
        }
    }


    private val mLiveCallback = object: IAVEngineCallback() {

        fun handleUserJoined(user: ChannelMicUser) {
            if (micUsers.containsKey(user.uid)) return
            micUsers[user.uid] = user
            updateVideo()
        }


        override fun onClientRoleChanged(oldRole: Int, newRole: Int, clientRoleInfo: ChannelMicUser?) {
            if (newRole == AVEngineConstant.ClientRole.ROLE_BROADCAST && clientRoleInfo != null) {
                handleUserJoined(clientRoleInfo)
            }
        }

        override fun onUserJoined(user: ChannelMicUser?, elapsed: Int) {
            user?.let {
                handleUserJoined(user)
            }
        }

        override fun onUserOffline(user: ChannelMicUser?, reason: Int) {
            user?.let {
                micUsers.remove(user.uid)
                setupVideo(user.uid, null)
                updateVideo()
            }
        }

        override fun onVideoConfigResolutionTypeChanged() {
            mAVEngine.stopPreview();
            mAVEngine.startPreview();
        }

        override fun onRtmpStreamingStateChanged(url: String?, state: KMediaRtmpStreamState?, errCode: KMediaRtmpStreamErrCode?) {
            val tips = when {
                KMediaRtmpStreamErrCode.OK == errCode -> {
                    "成功"
                }
                errCode == KMediaRtmpStreamErrCode.INTERNAL_SERVER_ERROR -> {
                    "服务器发生异常"
                }
                errCode == KMediaRtmpStreamErrCode.STREAM_NOT_EXIST -> {
                    "服务器未找到这个流"
                }
                errCode == KMediaRtmpStreamErrCode.CONNECT_RTMP_FAIL -> {
                    "连接rtmp服务器失败"
                }
                errCode == KMediaRtmpStreamErrCode.RTMP_TIMEOUT -> {
                    "与rtmp服务器交互超时"
                }
                errCode == KMediaRtmpStreamErrCode.OCCUPIED_BY_OTHERCHANNEL -> {
                    "${url}被其它频道占用"
                }
                else -> {
                    "参数错误"
                }
            }
//            ToastUtils.show(tips)

            val tips2 = when (state) {
                KMediaRtmpStreamState.CONNECTING -> {
                    rtmpUrls.add(url!!)
                    "正在连接rtmp服务器"
                }
                KMediaRtmpStreamState.RUNNING -> {
                    rtmpUrls.add(url!!)
                    "正在往${url}推流"
                }
                KMediaRtmpStreamState.FAILURE -> {
                    rtmpUrls.remove(url!!)
                    "往${url}推流失败"
                }
                else -> {
                    rtmpUrls.remove(url!!)
                    "往${url}推流结束"
                }
            }

            if (tips2.isNotEmpty()) {
                ToastUtils.show(tips2)
            }
        }
    }

    private fun updateVideo() {
        when (micUsers.size) {
            1 -> {

                val transcodingUsers = mutableListOf<BigoTranscodingUser>();
                TranscodingCfgFragment.liveTranscoding.width = 720
                TranscodingCfgFragment.liveTranscoding.height = 1280


                val widthRate = 720.toFloat()/WindowUtil.w.toFloat()
                val heightRate = 1280.toFloat()/WindowUtil.h.toFloat()

                for (user in micUsers.values) {
                    setupVideo(user.uid, canvas_full.getRendererCanvas())
                    val v = canvas_full
                    android.util.Log.e(TAG, "updateVideo: ===>> ${v.x} ${v.y} ${v.width * widthRate} ${v.height * widthRate}")
                    transcodingUsers.add(BigoTranscodingUser(user.uid, (v.x * widthRate).toInt(), (v.y * heightRate).toInt(),(v.width * widthRate).toInt(), (v.height * heightRate).toInt(),0,1.0f,1))
                    loadUserInfo(user, canvas_full)
                }
                TranscodingCfgFragment.replaceTranscodingUsers(transcodingUsers)
            }
            2 -> {
                val transcodingUsers = mutableListOf<BigoTranscodingUser>();
                TranscodingCfgFragment.liveTranscoding.width = 720
                TranscodingCfgFragment.liveTranscoding.height = 1280

                val widthRate = 720.toFloat()/WindowUtil.w.toFloat()
                val heightRate = 1280.toFloat()/WindowUtil.h.toFloat()

                for ((i, user) in micUsers.values.withIndex()) {
                    val canvas = if (i == 0) canvas_left else canvas_right
                    setupVideo(user.uid, canvas.getRendererCanvas())

                    val v = canvas

                    android.util.Log.e(TAG, "updateVideo: ===>>$i ${v.x} ${v.y} ${v.width * widthRate} ${v.height * widthRate}")
                    transcodingUsers.add(BigoTranscodingUser(user.uid, (v.x * widthRate).toInt(), (v.y * heightRate).toInt(),(v.width * widthRate).toInt(), (v.height * heightRate).toInt(),0,1.0f,1))
                    loadUserInfo(user, canvas)
                }

                TranscodingCfgFragment.replaceTranscodingUsers(transcodingUsers)
            }
            else -> Log.e(TAG, "pk live can not has more than 2 mic user")
        }
    }

    private fun loadUserInfo(user: ChannelMicUser, micView: SimpleMicView) {
        micView.setUserInfo(user.uid, user.micNum, "")
        mAVEngine.getUserInfoByUid(user.uid, object : OnUserInfoNotifyCallback {
            override fun onNotifyUserInfo(userInfo: UserInfo) {
                runOnUiThread { micView.setUserInfo(user.uid, user.micNum, userInfo.userAccount) }
            }

            override fun onFailed(code: Int) {

            }
        })
    }

    private fun setupVideo(uid: Long, canvas: RendererCanvas?) {
        if (uid == mMyUid) {
            mAVEngine.setupLocalVideo(VideoCanvas(uid, canvas))
        } else {
            mAVEngine.setupRemoteVideo(VideoCanvas(uid, canvas))
        }
    }

    private fun handleJoinPK(uid: Long) {
        joiningPk = true
    }

    override fun showDebugInfo(debugInfo: String) {
        tv_video_debug_info.text = debugInfo
    }
}