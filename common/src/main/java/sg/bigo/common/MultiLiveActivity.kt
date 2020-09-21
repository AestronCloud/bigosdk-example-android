package sg.bigo.common

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_multi_live.*
import sg.bigo.common.annotation.NonNull
import sg.bigo.common.utils.RoomOperateType
import sg.bigo.common.utils.RoomPricyHelper
import sg.bigo.common.utils.ToastUtils
import sg.bigo.opensdk.api.AVEngineConstant
import sg.bigo.opensdk.api.IAVEngine
import sg.bigo.opensdk.api.IAVEngineCallback
import sg.bigo.opensdk.api.IDeveloperMock
import sg.bigo.opensdk.api.callback.JoinChannelCallback
import sg.bigo.opensdk.api.callback.OnUserInfoNotifyCallback
import sg.bigo.opensdk.api.callback.RoomOperateCallback
import sg.bigo.opensdk.api.impl.JoinChannelCallbackParams
import sg.bigo.opensdk.api.struct.ChannelMicUser
import sg.bigo.opensdk.api.struct.UserInfo
import sg.bigo.opensdk.api.struct.VideoCanvas
import kotlin.properties.Delegates

private const val TAG = "MultiLiveActivity"

open class MultiLiveActivity : BaseActivity() {
    private val mUserName by lazy {
        intent.getStringExtra(KEY_USER_NAME)
    }

    private val mChannelName: String by lazy {
        intent.getStringExtra(KEY_CHANNEL_NAME)
    }


    open fun showBroadCastView() {
        btn_swtich_camera.visibility = View.VISIBLE
        live_btn_beautification.visibility = View.GONE
        live_btn_mute_audio.visibility = View.VISIBLE
        live_btn_mute_video.visibility = View.VISIBLE
        tips_for_bw_container.visibility = View.VISIBLE
        live_btn_up_mic.setImageResource(R.mipmap.icon_mic_down);
        live_beautify_face.visibility = View.VISIBLE
        //为了触发isPrivateRoom驱动UI更新
        isPrivateRoom = isPrivateRoom
    }

    open fun showAudienceView() {
        btn_swtich_camera.visibility = View.GONE
        live_btn_beautification.visibility = View.GONE
        live_btn_mute_audio.visibility = View.GONE
        live_btn_mute_video.visibility = View.GONE

        live_switch_public.visibility = View.GONE
        live_switch_private.visibility = View.GONE
        live_add_bl.visibility = View.GONE
        live_add_wl.visibility = View.GONE
        live_remove_form_bl.visibility = View.GONE
        live_remove_form_wl.visibility = View.GONE
        tips_for_bw_container.visibility = View.GONE
        live_beautify_face.visibility = View.GONE

        live_btn_up_mic.setImageResource(R.mipmap.icon_miclink_black)
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
            }
        })


    private val mAVEngine: IAVEngine = LiveApplication.avEngine()

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

        mAVEngine.addCallback(mLiveCallback)
        mRole = intent.getIntExtra(KEY_CLIENT_ROLE, AVEngineConstant.ClientRole.ROLE_AUDIENCE)
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

        live_btn_mute_video.isActivated = true
        live_btn_mute_video.setOnClickListener { view ->
            mAVEngine.muteLocalVideoStream(view.isActivated)
            view.isActivated = !view.isActivated
        }

        live_btn_up_mic.setOnClickListener {
            val newRole =
                if (isBroadcaster(mRole)) AVEngineConstant.ClientRole.ROLE_AUDIENCE else AVEngineConstant.ClientRole.ROLE_BROADCAST
            mAVEngine.setClientRole(newRole)
            if (isBroadcaster(newRole)) {
                mAVEngine.startPreview()
            } else {
                mAVEngine.stopPreview()
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
    }

    private fun joinRoom() {
        mAVEngine.setClientRole(mRole)
        mAVEngine.attachRendererView(bigoRendererView)
        log(TAG,"joinRoom $mMyUid $mChannelName $mUserName")

        mAVEngine.registerLocalUserAccount(mUserName,object :OnUserInfoNotifyCallback{
            override fun onNotifyUserInfo(user: UserInfo?) {
                user?.let {
                    mMyUid = user.uid
                    mAVEngine.developerMock.getToken(mMyUid, mChannelName, mUserName, getString(R.string.bigo_cert), object : IDeveloperMock.CommonCallback<String> {
                        override fun onResult(token: String?) {
                            log(TAG," joinChannel with token $token ")
                            mAVEngine.joinChannel(token, mChannelName, mMyUid, mJoinChannelCallback)
                        }

                        override fun onError(reason: Int) {
                            log(TAG," getToken error $reason")
                        }
                    })
                } ?: let {
                    ToastUtils.show("换取媒体uid失败")
                }
            }

            override fun onFailed(p0: Int) {
            }

        })

    }

    private val mJoinChannelCallback = object : JoinChannelCallback {
        override fun onSuccess(params: JoinChannelCallbackParams?) {
            log(TAG," joinChannel success role $mRole $mMyUid")
            mMyUid = params!!.uid
        }

        override fun onFailed(reason: Int) {
            log(TAG," joinChannel failed $reason , finish task")
            Toast.makeText(this@MultiLiveActivity, getString(R.string.tips_enter_room_failed), Toast.LENGTH_LONG).show()
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

                    var micSeatView = micViewContainer.addMicSeatView(this@MultiLiveActivity, mMyUid)
                    mAVEngine.setupLocalVideo(VideoCanvas(mMyUid, micSeatView!!.rendererCanvas()))
                } else {
                    val micView = micViewContainer.removeMicSeatView(mMyUid)
                    micView?.let { view ->
                        mAVEngine.setupLocalVideo(VideoCanvas(mMyUid, null))
                        val viewGroup = view.parent as? ViewGroup
                        viewGroup?.removeView(view)
                    }
                }
            }
        }

        override fun onUserJoined(@NonNull user: ChannelMicUser, elapsed: Int) {
            log(TAG,"#onUserJoined $user")
            runOnUiThread {
                if (micViewContainer.hasMicSeatView(user.uid)) {
                    log(TAG,"#already onMic $user")
                    return@runOnUiThread
                }
                var micSeatView = micViewContainer.addMicSeatView(this@MultiLiveActivity, user.uid)
                mAVEngine.setupRemoteVideo(VideoCanvas(user.uid, micSeatView!!.rendererCanvas()))
            }
        }

        override fun onUserOffline(@NonNull user: ChannelMicUser, reason: Int) {
            log(TAG,"#onUserOffline $user")
            runOnUiThread {
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

        override fun onFirstRemoteVideoDecoded(uid: Long, elapsed: Int) {

        }

        override fun onError(err: Int) {
            log(TAG," sth error $err")
        }

        override fun onVideoConfigResolutionTypeChanged() {
            mAVEngine.stopPreview()
            mAVEngine.startPreview()
        }

        override fun onKicked(reason: Int) {
            ToastUtils.show(getString(R.string.tips_be_kicked_suggest))
            this@MultiLiveActivity.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        log(TAG,"onResume")
        if (isBroadcaster(mRole)) {
            mAVEngine.startPreview()
        }
    }

    override fun onPause() {
        log(TAG,"onPause")
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        mAVEngine.stopPreview()
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
        log(TAG,"leaveRoom")
        clearAllBeautyConfig()
        mAVEngine.removeCallback(mLiveCallback)
        mAVEngine.leaveChannel()
    }

    override fun showDebugInfo(msg: String) {
        tv_video_debug_info.text = msg
    }
}