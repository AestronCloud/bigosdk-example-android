package sg.bigo.common

import android.content.Intent
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.polly.mobile.mediasdk.*
import kotlinx.android.synthetic.main.activity_multi_live.*
import sg.bigo.common.annotation.NonNull
import sg.bigo.common.customcapture.CameraController
import sg.bigo.common.fragment.TranscodingCfgFragment
import sg.bigo.common.rawdata.MediaPreProcessing
import sg.bigo.common.utils.DialogUtils
import sg.bigo.common.utils.RoomOperateType
import sg.bigo.common.utils.RoomPricyHelper
import sg.bigo.common.utils.ToastUtils
import sg.bigo.common.view.SoundEffectFragment
import sg.bigo.opensdk.api.AVEngineConstant
import sg.bigo.opensdk.api.IAVEngine
import sg.bigo.opensdk.api.IAVEngineCallback
import sg.bigo.opensdk.api.IDeveloperMock
import sg.bigo.opensdk.api.callback.BigoMediaSideCallback
import sg.bigo.opensdk.api.callback.JoinChannelCallback
import sg.bigo.opensdk.api.callback.OnUserInfoNotifyCallback
import sg.bigo.opensdk.api.callback.RoomOperateCallback
import sg.bigo.opensdk.api.impl.ChannelInfo
import sg.bigo.opensdk.api.struct.*
import sg.bigo.opensdk.api.struct.LiveTranscoding
import java.nio.charset.StandardCharsets
import kotlin.properties.Delegates

private const val TAG = "MultiLiveActivity"

open class MultiLiveActivity : BaseActivity() {
    private val mUserName by lazy {
        if(Intent.ACTION_VIEW.equals(intent.action)) {
            val uri: Uri = intent.data
            uri.getQueryParameter("userName")
        } else {
            intent.getStringExtra(KEY_USER_NAME)
        }
    }

    private val mChannelName: String by lazy {
        if(Intent.ACTION_VIEW.equals(intent.action)) {
            val uri: Uri = intent.data
            uri.getQueryParameter("channelName")
        } else {
            intent.getStringExtra(KEY_CHANNEL_NAME)
        }
    }

    private val isCustomCapture
        get() = LiveApplication.config.isCustomCaptureEnabled

    private val isEnableMultiView
        get() = LiveApplication.config.isEnableMultiView

    private val autoPublishUrl
        get() = LiveApplication.config.autoPublishAndLiveTranscodingUrl


    open fun showBroadCastView() {
        btn_swtich_camera.visibility = View.VISIBLE
        live_btn_beautification.visibility = View.GONE
        live_btn_mute_audio.visibility = View.VISIBLE
        live_btn_mute_video.visibility = View.VISIBLE
        btn_live_earback.visibility = View.VISIBLE
        btn_live_volume_type.visibility = View.VISIBLE
        tips_for_bw_container.visibility = View.VISIBLE
        live_btn_up_mic.setImageResource(R.mipmap.icon_mic_down);
        live_beautify_face.visibility = View.VISIBLE
        live_stickers.visibility = View.VISIBLE
        live_transcoding_stop.visibility = View.VISIBLE
        live_transcoding_start.visibility = View.VISIBLE
        live_transcoding_cfg.visibility = View.VISIBLE
        live_audio_op.visibility = View.VISIBLE
        live_audio_change.visibility = View.VISIBLE
        live_remove_publish_stream_url.visibility = View.VISIBLE
        live_add_publish_stream_url.visibility = View.VISIBLE

        if(isCustomCapture) {
            btn_swtich_camera.visibility = View.GONE
        }
        //为了触发isPrivateRoom驱动UI更新
        isPrivateRoom = isPrivateRoom
    }

    open fun showAudienceView() {
        btn_swtich_camera.visibility = View.GONE
        live_btn_beautification.visibility = View.GONE
        live_btn_mute_audio.visibility = View.GONE
        live_btn_mute_video.visibility = View.GONE
        btn_live_earback.visibility = View.GONE
        btn_live_volume_type.visibility = View.GONE

        live_switch_public.visibility = View.GONE
        live_switch_private.visibility = View.GONE
        live_add_bl.visibility = View.GONE
        live_add_wl.visibility = View.GONE
        live_remove_form_bl.visibility = View.GONE
        live_remove_form_wl.visibility = View.GONE
        tips_for_bw_container.visibility = View.GONE
        live_beautify_face.visibility = View.GONE
        live_stickers.visibility = View.GONE
        live_transcoding_stop.visibility = View.GONE
        live_transcoding_start.visibility = View.GONE
        live_transcoding_cfg.visibility = View.GONE
        live_audio_op.visibility = View.GONE
        live_audio_change.visibility = View.GONE
        live_remove_publish_stream_url.visibility = View.GONE
        live_add_publish_stream_url.visibility = View.GONE

        live_btn_up_mic.setImageResource(R.mipmap.icon_miclink_black)

        if(isCustomCapture) {
            btn_swtich_camera.visibility = View.GONE
        }

        isPrivateRoom = isPrivateRoom
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

    private var isPrivateRoom: Boolean by Delegates.observable(false, { _, _, newValue ->
            runOnUiThread {
                if(isBroadcaster(mRole)) {
                    if (newValue) {
                        live_switch_public.visibility = View.VISIBLE
                        live_switch_private.visibility = View.GONE
                        live_add_bl.visibility = View.GONE
                        live_add_wl.visibility = View.VISIBLE
                        live_remove_form_bl.visibility = View.GONE
                        live_remove_form_wl.visibility = View.VISIBLE
                        tips_for_bw_list.text = resources.getText(R.string.tips_for_white_list)
                    } else {
                        live_switch_public.visibility = View.GONE
                        live_switch_private.visibility = View.VISIBLE
                        live_add_bl.visibility = View.VISIBLE
                        live_add_wl.visibility = View.GONE
                        live_remove_form_bl.visibility = View.VISIBLE
                        live_remove_form_wl.visibility = View.GONE
                        tips_for_bw_list.text = resources.getText(R.string.tips_for_black_list)
                    }
                } else {
                    live_switch_public.visibility = View.GONE
                    live_switch_private.visibility = View.GONE
                    live_add_bl.visibility = View.GONE
                    live_add_wl.visibility = View.GONE
                    live_remove_form_bl.visibility = View.GONE
                    live_remove_form_wl.visibility = View.GONE

                    tips_for_bw_list.text = if (newValue) resources.getText(R.string.tips_for_white_list) else resources.getText(R.string.tips_for_black_list)
                }
            }
        })


    private val mAVEngine: IAVEngine = LiveApplication.avEngine()

    private val rtmpUrls = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_live)
        log(TAG,"is Restart ${savedInstanceState != null}")
        initDatas()
        initView()
        joinRoom()
    }

    private fun initDatas() {
        //主要是处理一下页面重启重新退房进房，demo暂时没有支持数据的保存和恢复
        if (isRestart) {
            leaveRoom()
        }

        mAVEngine.enableCustomVideoCapture(isCustomCapture)
        mAVEngine.addCallback(mLiveCallback)

        mRole = if (Intent.ACTION_VIEW.equals(intent.action)) {
            val uri: Uri = intent.data
            uri.getQueryParameter("role").toInt()
        } else {
            intent.getIntExtra(KEY_CLIENT_ROLE, AVEngineConstant.ClientRole.ROLE_AUDIENCE)
        }

        isPrivateRoom = false
    }

    //这里就是千篇一律的给每个按钮加响应
    open fun initView() {
        btn_swtich_camera.setOnClickListener { mAVEngine.switchCamera() }
        live_btn_beautification.visibility = View.GONE
        live_btn_beautification.isActivated = false
        live_btn_beautification.setOnClickListener { view ->
            mAVEngine.enableBeautyMode(!view.isActivated)
            mAVEngine.setBeautifySmoothSkin(100)
            view.isActivated = !view.isActivated
        }

        live_btn_mute_audio.isActivated = true
        live_btn_mute_audio.setOnClickListener { view ->
            mAVEngine.muteLocalAudioStream(view.isActivated)
            view.isActivated = !view.isActivated
        }

        btn_live_earback.isActivated = true
        btn_live_earback.setOnClickListener { view ->
            mAVEngine.enableInEarMonitoring(view.isActivated)
            view.isActivated = !view.isActivated
        }

        btn_live_volume_type.isActivated = false
        btn_live_volume_type.setOnClickListener { view ->
            mAVEngine.setUserCallMode(!mAVEngine.isUseCommunicationMode())
            view.isActivated = !view.isActivated
        }

        live_btn_mute_video.isActivated = true
        live_btn_mute_video.setOnClickListener { view ->
            mAVEngine.muteLocalVideoStream(view.isActivated)
            view.isActivated = !view.isActivated
        }

        live_btn_up_mic.setOnClickListener {
            val newRole =
                if (isBroadcaster(mRole)) AVEngineConstant.ClientRole.ROLE_AUDIENCE else AVEngineConstant.ClientRole.ROLE_BROADCAST
            mAVEngine.setClientRole(newRole)
            if(!isCustomCapture) {
                if (isBroadcaster(newRole)) {
                    mAVEngine.startPreview()
                } else {
                    mAVEngine.stopPreview()
                }
            } else {
                if (isBroadcaster(newRole)) {
                    mAVEngine.enableCustomVideoCapture(isCustomCapture)
                    CameraController.getInstance().startCamera()
                } else {
                    CameraController.getInstance().stopCamera()
                }
            }
        }

        live_switch_private.setOnClickListener {
            RoomPricyHelper.switchToPrivacyRoom(this@MultiLiveActivity, mutableSetOf(mMyUid),object: RoomOperateCallback{
                override fun onSuccess() {
                    isPrivateRoom = true
                }

                override fun onFailed(code: Int) {
                    isPrivateRoom = false
                }
            })
        }

        live_switch_public.setOnClickListener {
            RoomPricyHelper.switchToPublicRoom(this@MultiLiveActivity, object :RoomOperateCallback{
                override fun onSuccess() {
                    isPrivateRoom = false
                }

                override fun onFailed(p0: Int) {
                    isPrivateRoom = true
                }
            })
        }

        live_beautify_face.setOnClickListener {
            startActivity(Intent(this,BeautifyActivity::class.java))
        }

        live_stickers.setOnClickListener {
            startActivity(Intent(this,StickersActivity::class.java))
        }


        live_add_bl.setOnClickListener {
            RoomPricyHelper.showRoomOperateDialog(this, RoomOperateType.ADD_TO_BLACK_LIST)
        }

        live_add_wl.setOnClickListener {
            RoomPricyHelper.showRoomOperateDialog(this,RoomOperateType.ADD_TO_WHITE_LIST)
        }

        live_remove_form_bl.setOnClickListener {
            RoomPricyHelper.showRoomOperateDialog(this,RoomOperateType.REMOVE_FORM_BLACKLIST)
        }

        live_remove_form_wl.setOnClickListener {
            RoomPricyHelper.showRoomOperateDialog(this,RoomOperateType.REMOVE_FORM_WHITELIST)
        }
        
        tv_debug_toggle.setOnClickListener {
            if(tv_video_debug_info.isShown) {
                tv_video_debug_info.visibility = View.GONE
            } else {
                tv_video_debug_info.visibility = View.VISIBLE
            }
        }

        live_add_publish_stream_url.setOnClickListener {
            DialogUtils.showAddPublishStringUrlDialog(this,rtmpUrls)
        }

        live_remove_publish_stream_url.setOnClickListener {
            DialogUtils.showRemovePublishStringUrlDialog(this,rtmpUrls)
        }

        live_transcoding_cfg.setOnClickListener {
            startActivity(Intent(this,TranscodingCfgActivity::class.java))
        }

        live_transcoding_start.setOnClickListener {
            mAVEngine.setLiveTranscoding(TranscodingCfgFragment.liveTranscoding);
            ToastUtils.show(getString(R.string.tips_stop_live_transcoding))
        }

        live_transcoding_stop.setOnClickListener {
            mAVEngine.stopLiveTranscoding()
            ToastUtils.show("停止合流")
        }

        live_audio_op.setOnClickListener {
            startActivity(Intent(this,SoundEffectSettingActivity::class.java))
        }

        live_audio_change.setOnClickListener{
            startActivity(Intent(this,SoundChangeActivity::class.java))
        }

        if(!isCustomCapture) {
            aux_view.visibility = View.GONE
        }
    }

    private var previewView: GLSurfaceView? = null
    private var remoteShowViewMap = mutableMapOf<Long,GLSurfaceView>()

    private fun setupPreviewSurfaceView() {
        if(previewView == null) {
            previewView = GLSurfaceView(this)
            mAVEngine.setupLocalView(BigoVideoCanvas(0,previewView))
            micViewContainer.addView(previewView)
        }
    }

    private fun joinRoom() {
        mAVEngine.setClientRole(mRole)
        mAVEngine.enableLocalAudio(false);
        mAVEngine.setAudioProfile(LiveApplication.config.profile, LiveApplication.config.scenario)

        if(isEnableMultiView) {
            if(isBroadcaster(mRole)) {
                setupPreviewSurfaceView()
            }
        } else {
            mAVEngine.attachRendererView(bigoRendererView)
        }


        log(TAG,"joinRoom $mMyUid $mChannelName $mUserName")
        mAVEngine.setAllVideoMaxEncodeParams(LiveApplication.config.maxResolution,LiveApplication.config.frameRate);

        LiveApplication.config.run {
            Log.e(TAG, "joinRoomjoinRoom: ${lbsIp} ${lbsPort} ${appId} ${cert} ${liveExtraInfo} ${frameRate}")
        }

        mAVEngine.setAppConfig(AppConfig().apply {
            mChannelCountryCode = LiveApplication.config.channelCC
        })

        Log.d(TAG, "joinRoom() called maxResolution ${LiveApplication.config.maxResolution} frameRate ${LiveApplication.config.frameRate}")
        mAVEngine.registerLocalUserAccount(mUserName,object :OnUserInfoNotifyCallback{
            override fun onNotifyUserInfo(user: UserInfo?) {
                user?.let {
                    mMyUid = if(LiveApplication.config.isEnableCustomUid) LiveApplication.config.customUid else it.uid
                    mAVEngine.developerMock.getToken(mMyUid, mChannelName, mUserName, LiveApplication.cert, TokenCallbackProxy(object : IDeveloperMock.CommonCallback<String?> {
                        override fun onResult(token: String?) {
                            log(TAG," joinChannel with token $token ")
                            mAVEngine.joinChannel(token, mChannelName, mMyUid, LiveApplication.config.liveExtraInfo, mJoinChannelCallback)
                            mAVEngine.registerNativeAudioFrameObserver(MediaPreProcessing.createNativeAudioFrameObserver());
                        }

                        override fun onError(reason: Int) {
                            log(TAG," getToken error $reason")
                        }
                    }))
                } ?: let {
                    ToastUtils.show("换取媒体uid失败")
                }
            }

            override fun onFailed(p0: Int) {
            }

        })

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private val mseiCallback = BigoMediaSideCallback { streamID, inData, dataLen ->
        val s: String = StandardCharsets.UTF_8.decode(inData).toString()
        ToastUtils.handlerShow(mUIHandler,"onRecvMediaSideInfo: streamID:${streamID} ,inData:${s} ,dataLen:${dataLen}")
    }

    private val mJoinChannelCallback = object : JoinChannelCallback {
        @RequiresApi(Build.VERSION_CODES.KITKAT)
        override fun onSuccess(params: ChannelInfo?) {
            log(TAG," joinChannel success role $mRole $mMyUid")
            mMyUid = params!!.uid

            mAVEngine.setMediaSideFlags(true,false,0)
            mAVEngine.setBigoMediaSideCallback(mseiCallback)

            if(autoPublishUrl.isNotEmpty()) {
                mAVEngine.setLiveTranscoding(LiveTranscoding().apply {
                    width = 720
                    height = 1280
                    videoBitrate = 1400
                    videoFramerate = 24
                    lowLatency = false
                    videoGop = 30;
                    videoCodecProfile = VideoCodecProfileType.MAIN
                    userConfigExtraInfo = LiveApplication.config.liveExtraInfo

                    watermark = BigoImageConfig("https://tse2-mm.cn.bing.net/th/id/OIP.x2Uv_SU7eDIjWnotR5_bXAHaEo?w=197&h=123&c=7&o=5&dpr=2&pid=1.7", 500, 500, 100, 100)
                    backgroundImage = BigoImageConfig("https://www.cleverfiles.com/howto/wp-content/uploads/2018/03/minion.jpg", 0, 0, 100, 100)
                    backgroundColor = "FFB6C1"
                    audioSampleRateType = AudioSampleRateType.TYPE_32000
                    audioBitrate = 100
                    audioChannels = 2
                    audioCodecProfile = AudioCodecProfileType.HE_AAC
                    transcodingUsers[mMyUid] = BigoTranscodingUser(mMyUid, 0, 0, 720, 1280, 0, 1f, 1);
                });
            } else {
                log(TAG,"not enable auto publish and livetranscoding")
            }



            runOnUiThread {
                if(isCustomCapture)
                    CameraController.getInstance().openCamera(this@MultiLiveActivity,bigoRendererView.surfaceView,aux_view)
            }

        }

        override fun onFailed(reason: Int) {
            log(TAG," joinChannel failed $reason , finish task")
            ToastUtils.showJoinChannelErrTips(reason)
            this@MultiLiveActivity.finish()
        }

        override fun onTokenVerifyError(token: String?) {
            log(TAG,"token verify error")
            Toast.makeText(this@MultiLiveActivity, getString(R.string.tips_enter_room_token_error), Toast.LENGTH_LONG).show()
            this@MultiLiveActivity.finish()
        }
    }




    private val mLiveCallback: IAVEngineCallback = object : IAVEngineCallback() {
        override fun onClientRoleChanged(
            oldRole: Int,
            newRole: Int,
            clientRoleInfo: ChannelMicUser?
        ) {
            log(TAG,"#onClientRoleChanged oldRole $oldRole newRole $newRole  ${clientRoleInfo}")
            mRole = newRole
            runOnUiThread {
                if (isBroadcaster(mRole)) {
                    clientRoleInfo?.let {
                        if (micViewContainer.hasMicSeatView(it.uid)) {
                            return@runOnUiThread
                        }

                        live_btn_mute_audio.isActivated = !it.audioMuted
                        live_btn_mute_video.isActivated = !it.videoMuted
                    }


                    if(isEnableMultiView) {
                        mAVEngine.setAllVideoMaxEncodeParams(AVEngineConstant.MaxResolutionTypes.MR_1280x720,LiveApplication.config.frameRate);
                        //no need call , because call setupPreviewSurfaceView already
                        if(previewView == null) {
                            previewView = GLSurfaceView(this@MultiLiveActivity)
                            mAVEngine.setupLocalView(BigoVideoCanvas(mMyUid,previewView))
                            micViewContainer.addView(previewView)
                        }
                    } else {

                        val onfirstSeat = micViewContainer.inFirstSeat(mMyUid)
                        if(onfirstSeat) {
                            mAVEngine.setAllVideoMaxEncodeParams(AVEngineConstant.MaxResolutionTypes.MR_1280x720,LiveApplication.config.frameRate);
                        } else {
                            mAVEngine.setAllVideoMaxEncodeParams(AVEngineConstant.MaxResolutionTypes.MR_640x360,LiveApplication.config.frameRate);
                        }

                        var micSeatView = micViewContainer.addMicSeatView(this@MultiLiveActivity, mMyUid)
                        mAVEngine.setupLocalVideo(VideoCanvas(mMyUid, micSeatView!!.rendererCanvas()))
                    }
                } else {

                    if(isEnableMultiView) {
                        previewView?.let {
                            (it.parent as ViewGroup?)?.run {
                                removeView(it)
                            }
                            previewView = null
                        }
                        mAVEngine.setupLocalView(BigoVideoCanvas(mMyUid, null))
                    } else {
                        val micView = micViewContainer.removeMicSeatView(mMyUid)
                        micView?.let { view ->
                            mAVEngine.setupLocalVideo(VideoCanvas(mMyUid, null))
                            val viewGroup = view.parent as? ViewGroup
                            viewGroup?.removeView(view)
                        }
                    }

                    SoundEffectFragment.resetMusic()
                }
            }
        }

        override fun onUserJoined(@NonNull user: ChannelMicUser, elapsed: Int) {
            log(TAG,"#onUserJoined $user")
            runOnUiThread {


                if(isEnableMultiView) {
                    if(remoteShowViewMap.containsKey(user.uid)) {
                        //already exist this uid
                        return@runOnUiThread
                    }

                    remoteShowViewMap.put(user.uid,GLSurfaceView(this@MultiLiveActivity).apply {
                        mAVEngine.setupRemoteView(BigoVideoCanvas(user.uid,this))
                        micViewContainer.addView(this)
                    })

                } else {
                    if (micViewContainer.hasMicSeatView(user.uid)) {
                        log(TAG,"#already onMic $user")
                        return@runOnUiThread
                    }

                    val onfirstSeat = micViewContainer.inFirstSeat(mMyUid)
                    if(onfirstSeat) {
                        mAVEngine.setAllVideoMaxEncodeParams(AVEngineConstant.MaxResolutionTypes.MR_1280x720,LiveApplication.config.frameRate);
                    } else {
                        mAVEngine.setAllVideoMaxEncodeParams(AVEngineConstant.MaxResolutionTypes.MR_640x360,LiveApplication.config.frameRate);
                    }

                    var micSeatView = micViewContainer.addMicSeatView(this@MultiLiveActivity, user.uid)
                    mAVEngine.setupRemoteVideo(VideoCanvas(user.uid, micSeatView!!.rendererCanvas()))
                }

                ToastUtils.show("${user.uid} 上麦携带数据 ${user.extrainfo}")
            }
        }

        override fun onUserOffline(@NonNull user: ChannelMicUser, reason: Int) {
            log(TAG,"#onUserOffline $user")
            runOnUiThread {
                if(isEnableMultiView) {
                    remoteShowViewMap.remove(user.uid)?.let {
                        (it.parent as ViewGroup?)?.run {
                            removeView(it)
                        }
                    }
                    mAVEngine.setupRemoteView(BigoVideoCanvas(user.uid,null))

                } else {
                    val micView = micViewContainer.removeMicSeatView(user.uid)
                    micView?.let { view ->
                        if (user.uid == mMyUid) {
                            mAVEngine.setupLocalVideo(VideoCanvas(user.uid, null))
                        } else {
                            mAVEngine.setupRemoteVideo(VideoCanvas(user.uid, null))
                        }
                        val viewGroup = view.parent as? ViewGroup
                        viewGroup?.removeView(view)
                    } ?: let {
                        log(TAG,"#not on Mic $user")
                    }
                }
            }
        }

        override fun onFirstRemoteVideoDecoded(uid: Long, elapsed: Int) {

        }


        override fun onTranscodingUpdated() {
            if(autoPublishUrl.isNotEmpty() && !rtmpUrls.contains(autoPublishUrl)) {
                mAVEngine.addPublishStreamUrl(autoPublishUrl)
            }
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


        override fun onError(err: Int) {
            log(TAG," sth error $err")
        }

        override fun onKicked() {
            ToastUtils.show(getString(R.string.tips_be_kicked_suggest))
            this@MultiLiveActivity.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        log(TAG,"onResume")

        if(!isCustomCapture) {
            if (isBroadcaster(mRole)) {
                mAVEngine.startPreview()
            }
        } else {
            if (isBroadcaster(mRole)) {
                CameraController.getInstance().startCamera()
            }
        }
    }

    override fun onPause() {
        log(TAG,"onPause")
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        if(!isCustomCapture) {
            mAVEngine.stopPreview()
        } else {
            CameraController.getInstance().stopCamera()
        }
        log(TAG,"onStop $isFinishing $isChangingConfigurations")
        if (isFinishing || isChangingConfigurations) {
            leaveRoom()
        }
    }

    override fun onDestroy() {
        log(TAG,"onDestroy $isFinishing $isChangingConfigurations")
        super.onDestroy()
    }

    private fun leaveRoom() {

        mAVEngine.enableCustomVideoCapture(false)
        log(TAG,"leaveRoom")
        clearAllBeautyConfig()

        SoundEffectFragment.isPlayingMixSound = false
        for(i in 0 until SoundEffectFragment.isPlayingEffectSounds.size) {
            SoundEffectFragment.isPlayingEffectSounds[i] = false
        }
        mAVEngine.removeCallback(mLiveCallback)
        mAVEngine.leaveChannel()
    }

    override fun showDebugInfo(msg: String) {
        tv_video_debug_info.text = msg
    }
}