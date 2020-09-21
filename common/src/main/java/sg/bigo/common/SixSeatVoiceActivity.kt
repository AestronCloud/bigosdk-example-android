package sg.bigo.common

import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_1v1_voice.*
import kotlinx.android.synthetic.main.activity_six_seats.*
import kotlinx.android.synthetic.main.activity_six_seats.live_audio_quality_switch
import kotlinx.android.synthetic.main.seat_item.view.*
import sg.bigo.common.LiveApplication.Companion.avEngine
import sg.bigo.common.utils.ToastUtils
import sg.bigo.common.utils.WindowUtil
import sg.bigo.opensdk.api.AVEngineConstant
import sg.bigo.opensdk.api.IAVEngineCallback
import sg.bigo.opensdk.api.IDeveloperMock.CommonCallback
import sg.bigo.opensdk.api.callback.JoinChannelCallback
import sg.bigo.opensdk.api.callback.OnUserInfoNotifyCallback
import sg.bigo.opensdk.api.impl.JoinChannelCallbackParams
import sg.bigo.opensdk.api.struct.ChannelMicUser
import sg.bigo.opensdk.api.struct.UserInfo
import kotlin.properties.Delegates


class SixSeatVoiceActivity : BaseActivity() {
    private val mIAVEngine = avEngine()
    private val mUIHandler = Handler(Looper.getMainLooper());
    private var mMyUid: Long = 0

    private var mRole: Int = AVEngineConstant.ClientRole.ROLE_AUDIENCE

    private fun showMicOpView() {
        if(isBroadcaster(mRole)) {
            up_mic.setImageResource(R.mipmap.icon_mic_down)
        } else {
            up_mic.setImageResource(R.mipmap.icon_miclink_black)
        }
    }

    private val mUserName by lazy {
        intent.getStringExtra(KEY_USER_NAME)
    }

    private val mChannelName: String by lazy {
        intent.getStringExtra(KEY_CHANNEL_NAME)
    }

    val mics = arrayListOf(MicInfo(), MicInfo(), MicInfo(), MicInfo(), MicInfo(), MicInfo())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_six_seats)
        initDatas()
        initViews()
        startVoiceLive()
    }

    data class MicInfo(
        var uid: Long = 0,
        var speaking: Boolean = false,
        var muted: Boolean = false,
        var mutedBySelf: Boolean = false
    ) {
        override fun toString(): String {
            return "MicInfo(uid=$uid, speaking=$speaking, muted=$muted, mutedBySelf=$mutedBySelf)"
        }
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    private fun initViews() {
        channel_name.text = mChannelName

        up_mic.setOnClickListener {
            if (mRole == AVEngineConstant.ClientRole.ROLE_BROADCAST) {
                mRole = AVEngineConstant.ClientRole.ROLE_AUDIENCE
            } else {
                mRole = AVEngineConstant.ClientRole.ROLE_BROADCAST
            }
            mIAVEngine.setClientRole(mRole)
            showMicOpView()
        }
        showMicOpView()

        live_audio_quality_switch.isActivated = false
        live_audio_quality_switch.setOnClickListener {
            it.isActivated = !it.isActivated

            (if (it.isActivated) AVEngineConstant.AudioQuality.HD else AVEngineConstant.AudioQuality.FLUENT).let {
                mIAVEngine.setAudioQuality(it)
            }
        }

        mic_list.layoutManager = GridLayoutManager(this, 3)

        val itemW = WindowUtil.w / 3
        mic_list.adapter = object : RecyclerView.Adapter<VH>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                return VH(
                    LayoutInflater.from(parent.context).inflate(R.layout.seat_item, parent, false)
                        .apply {
                            layoutParams.height = itemW - 10
                            layoutParams.width = itemW - 10
                        }
                )

            }

            override fun getItemCount(): Int {
                return mics.size
            }

            override fun onBindViewHolder(holder: VH, position: Int) {
                val micInfo = mics[position]
                if (micInfo.uid == 0L) {
                    holder.itemView.tv_uid.text = ""
                    holder.itemView.iv_speaking.visibility = View.GONE
                    holder.itemView.iv_muted.visibility = View.GONE
                } else {
                    val me = if(micInfo.uid == mMyUid) "我" else ""
                    holder.itemView.tv_uid.text = "${me} ${micInfo.uid}"

                    holder.itemView.iv_speaking.visibility = View.VISIBLE
                    holder.itemView.iv_muted.visibility = View.VISIBLE
                    if (micInfo.muted) {
                        holder.itemView.iv_muted.background = ContextCompat.getDrawable(holder.itemView.context,R.drawable.btn_audio_disabled)
                    } else {
                        holder.itemView.iv_muted.background = ContextCompat.getDrawable(holder.itemView.context,R.drawable.btn_audio_enabled)
                    }

                    holder.itemView.iv_muted.setOnClickListener {
                        //是主播禁了自己的麦位，观众端不能操作只能主播自己操作
                        if(mMyUid != micInfo.uid && micInfo.mutedBySelf) return@setOnClickListener

                        if(micInfo.uid == mMyUid) {
                            mIAVEngine.muteLocalAudioStream(!micInfo.muted)
                        } else {
                            mIAVEngine.muteRemoteAudioStream(micInfo.uid,!micInfo.muted)
                        }

                        micInfo.muted = !micInfo.muted
                        mic_list.adapter!!.notifyDataSetChanged()
                    }

                    if (micInfo.speaking) {
                        if (holder.itemView.iv_speaking.tag == null) {
                            holder.itemView.iv_speaking.tag = Runnable {
                                holder.itemView.iv_speaking.visibility = View.GONE
                            }
                        }

                        val hideRunnable = holder.itemView.iv_speaking.tag as Runnable
                        mUIHandler.removeCallbacks(hideRunnable)
                        mUIHandler.postDelayed(hideRunnable,650)
                        holder.itemView.iv_speaking.visibility = View.VISIBLE
                    } else {
                        holder.itemView.iv_speaking.visibility = View.GONE
                    }

                    if (micInfo.muted) {

                        holder.itemView.iv_muted.setColorFilter(
                            ContextCompat.getColor(this@SixSeatVoiceActivity,R.color.blue),
                            PorterDuff.Mode.MULTIPLY
                        )
                    } else {
                        holder.itemView.iv_muted.clearColorFilter()
                    }
                }
            }
        }
    }

    private fun initDatas() {
        if(isRestart) {
            leaveRoom()
        }
        mRole = intent.getIntExtra(KEY_CLIENT_ROLE, AVEngineConstant.ClientRole.ROLE_AUDIENCE)
        mIAVEngine.addCallback(mVoiceLiveCallback)
    }

    private fun startVoiceLive() {
        mIAVEngine.enableLocalVideo(false)
        mIAVEngine.setClientRole(mRole)
        mIAVEngine.registerLocalUserAccount(mUserName,object :OnUserInfoNotifyCallback {
            override fun onNotifyUserInfo(user: UserInfo?) {
                user?.let {
                    mMyUid = user.uid
                    mIAVEngine.developerMock.getToken(
                        mMyUid,
                        mChannelName,
                        mUserName,
                        getString(R.string.bigo_cert),
                        object : CommonCallback<String?> {
                            override fun onResult(token: String?) {
                                mIAVEngine.joinChannel(
                                    token,
                                    mChannelName,
                                    mMyUid,
                                    mJoinChannelCallback
                                )
                            }

                            override fun onError(i: Int) {}
                        })
                } ?: let {
                    ToastUtils.show("获取媒体uid失败")
                }
            }

            override fun onFailed(code: Int) {
                ToastUtils.show("获取媒体uid失败 $code")
            }

        })

    }

    private val mJoinChannelCallback: JoinChannelCallback = object : JoinChannelCallback {
        override fun onSuccess(joinChannelCallbackParams: JoinChannelCallbackParams) {
            mMyUid = joinChannelCallbackParams.uid
        }

        override fun onTokenVerifyError(s: String) {
            ToastUtils.show("进房失败，token 校验失败")
            finish()
        }
        override fun onFailed(code: Int) {
            ToastUtils.show("进房失败，错误码 $code")
            finish()
        }
    }

    private val mVoiceLiveCallback: IAVEngineCallback = object : IAVEngineCallback() {

        override fun onClientRoleChanged(oldRole: Int, newRole: Int, user: ChannelMicUser?) {
            Log.d(TAG, "onClientRoleChanged() called ${mics.toString()}")

            runOnUiThread {
                if (newRole == AVEngineConstant.ClientRole.ROLE_BROADCAST) {
                    user?.let {
                        Log.d(TAG, "onClientRoleChanged() called ${mics.toString()}")
                        val result = findExistMic(user.uid)
                        if (result != null) return@runOnUiThread
                        val firstMicSeat = findFirstEmptyMicSeat() ?: return@runOnUiThread
                        firstMicSeat.uid = user.uid
                        firstMicSeat.muted = user.audioMuted
                        firstMicSeat.mutedBySelf = user.audioMuted
                        firstMicSeat.speaking = false
                        Log.d(TAG, "onClientRoleChanged() called ${mics.toString()}")
                        mic_list.adapter!!.notifyDataSetChanged()
                    }

                } else {
                    Log.d(TAG, "onClientRoleChanged() called ${mics.toString()}")
                    val result = findExistMic(mMyUid) ?: return@runOnUiThread
                    result.uid = 0
                    result.muted = false
                    result.speaking = false
                    mic_list.adapter!!.notifyDataSetChanged()
                }
            }
        }

        override fun onUserJoined(
            channelMicUser: ChannelMicUser,
            elaspedTime: Int
        ) {
            runOnUiThread {
                val result = findExistMic(channelMicUser.uid)
                if (result != null) return@runOnUiThread
                val firstMicSeat = findFirstEmptyMicSeat() ?: return@runOnUiThread

                firstMicSeat.uid = channelMicUser.uid
                firstMicSeat.muted = channelMicUser.audioMuted
                firstMicSeat.speaking = false

                mic_list.adapter!!.notifyDataSetChanged()
            }

        }

        override fun onUserOffline(channelMicUser: ChannelMicUser, i: Int) {
            runOnUiThread {
                val result = findExistMic(channelMicUser.uid) ?: return@runOnUiThread
                result.uid = 0
                result.muted = false
                result.speaking = false
                result.mutedBySelf = false
                mic_list.adapter!!.notifyDataSetChanged()
            }
        }

        override fun onUserMuteAudio(uid: Long, muted: Boolean) {
            runOnUiThread {
                val result = findExistMic(uid) ?: return@runOnUiThread
                result.mutedBySelf = muted
                result.muted = muted
                result.speaking = false
                mic_list.adapter!!.notifyDataSetChanged()
            }
        }

        override fun onSpeakerChange(speakingUids: LongArray?) {
            runOnUiThread {
                speakingUids?.let {
                    for (uid in it) {
                        val result = findExistMic(uid)
                        result?.let {
                            result.speaking = true
                        }
                    }
                }
                mic_list.adapter!!.notifyDataSetChanged()
            }
        }
    }

    fun findExistMic(uid: Long): MicInfo? {
        var result: MicInfo? = null
        for (mic in mics) {
            if (mic.uid == uid) {
                result = mic
                break
            }
        }
        return result
    }

    fun findFirstEmptyMicSeat(): MicInfo? {
        val mic = mics.find {
            it.uid == 0L
        }

        return mic
    }

    fun onEndCallClicked(view: View?) {
        println("click view $view")
        finish()
    }

    override fun onStop() {
        super.onStop()
        if (isFinishing || isChangingConfigurations) {
            leaveRoom()
        }
    }

    private fun leaveRoom() {
        mIAVEngine.enableLocalVideo(true)
        mIAVEngine.removeCallback(mVoiceLiveCallback)
        mIAVEngine.leaveChannel()
    }

    companion object {
        private const val TAG = "SixSeatVoiceActivity"
    }
}