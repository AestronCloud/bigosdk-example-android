package sg.bigo.common.utils

import android.os.Handler
import android.widget.Toast
import sg.bigo.common.LiveApplication
import sg.bigo.opensdk.api.AVEngineConstant

object ToastUtils {
    fun show(msg: String) {
        Toast.makeText(LiveApplication.appContext, msg, Toast.LENGTH_LONG).show()
    }

    fun handlerShow(handler: Handler, msg: String) {
        handler.post {
            Toast.makeText(LiveApplication.appContext, msg, Toast.LENGTH_LONG).show()
        }
    }

    fun showJoinChannelErrTips(code: Int) {
        var tips = ""
        when (code) {
            AVEngineConstant.ErrorCode.ERR_TIMEOUT -> {
                tips = "请求超时"
            }
            AVEngineConstant.ErrorCode.ERR_PROTO_FAILE -> {
                tips = "请求失败"
            }
            AVEngineConstant.ErrorCode.ERR_ROOM_INVALID -> {
                tips = "房间不存在"
            }
            AVEngineConstant.ErrorCode.ERR_ROOM_OPERATE -> {
                tips = "错误的房间操作"
            }
            AVEngineConstant.ErrorCode.ERR_IN_BLACKLIST -> {
                tips = "在黑名单"
            }
            AVEngineConstant.ErrorCode.ERR_NOT_IN_WHITELIST -> {
                tips = "是私密房，你不在白名单"
            }
            AVEngineConstant.ErrorCode.ERR_BLOCKED_BY_HOST -> {
                tips = "主播已经将你踢出房间"
            }
            AVEngineConstant.ErrorCode.ERR_NO_WHITELIST -> {
                tips = "切私人房没有携带白名单列表"
            }
            AVEngineConstant.ErrorCode.ERR_REMOVE_SELF -> {
                tips = "房主不能将自己移除白名单"
            }
            AVEngineConstant.ErrorCode.ERR_ALREADY_IN_CHANNEL -> {
                tips = "已经在房间内，不能重复joinChannel"
            }
            AVEngineConstant.ErrorCode.ERR_CHANNEL_STATE_INVALID -> {
                tips = "无效的房间状态"
            }
            AVEngineConstant.ErrorCode.ERR_MEDIA_RECORDER_DEVICE -> {
                tips = "设备采集失败"
            }
            AVEngineConstant.ErrorCode.ERR_MEDIA_AUDIO_RECORD_INIT -> {
                tips = "设备初始化失败"
            }
            AVEngineConstant.ErrorCode.ERR_OPEN_CAMERA -> {
                tips = "打开摄像头失败"
            }
            AVEngineConstant.ErrorCode.ERR_GROUP_NOT_FOUND -> {
                tips = "未找到分组"
            }
            AVEngineConstant.ErrorCode.ERR_TOKEN_INVALIDED -> {
                tips = "token无效"
            }
            AVEngineConstant.ErrorCode.ERR_CHANNEL_PROFILE_NOT_SUPPORT -> {
                tips = "该频道下不支持该操作"
            }
            AVEngineConstant.ErrorCode.ERR_INVALID_ARGUMENT -> {
                tips = "无效参数"
            }
            AVEngineConstant.ErrorCode.ERR_NOT_INCHANNEL -> {
                tips = "已经在房间内"
            }
        }

        Toast.makeText(LiveApplication.appContext,tips,Toast.LENGTH_LONG).show()

    }
}