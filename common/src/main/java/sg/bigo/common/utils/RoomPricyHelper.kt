package sg.bigo.common.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import sg.bigo.common.LiveApplication
import sg.bigo.common.R
import sg.bigo.opensdk.api.callback.RoomOperateCallback
import sg.bigo.opensdk.utils.PrintUtils


enum class RoomOperateType {
    ADD_TO_BLACK_LIST, REMOVE_FORM_BLACKLIST, ADD_TO_WHITE_LIST, REMOVE_FORM_WHITELIST;

    companion object {
        fun getTitleByOperateType(context: Context, operateType: RoomOperateType?): String {
            when (operateType) {
                ADD_TO_BLACK_LIST -> return context.getString(R.string.tips_add_black_uid)
                REMOVE_FORM_BLACKLIST -> return context.getString(R.string.tips_remove_black_uid)
                ADD_TO_WHITE_LIST -> return context.getString(R.string.tips_add_white_uid)
                REMOVE_FORM_WHITELIST -> return context.getString(R.string.tips_remove_white_uid)
            }
            return context.getString(R.string.tips_input_uid)
        }
    }
}

object RoomPricyHelper {

    fun switchToPrivacyRoom(context: Context, uids: Set<Long>,callback: RoomOperateCallback) {
        LiveApplication.avEngine().switchToPrivacyRoom(0,"",uids, object :RoomOperateCallback{
            override fun onSuccess() {
                callback.onSuccess()
                ToastUtils.show(context.getString(R.string.tips_switch_private_room_success))
            }

            override fun onFailed(code: Int) {
                callback.onFailed(code)
                ToastUtils.show(context.getString(R.string.tips_switch_private_room_failed) + "$code")
            }
        })
    }

    fun switchToPublicRoom(context: Context, callback: RoomOperateCallback) {
        LiveApplication.avEngine().switchToPublicRoom(emptySet(),0, emptySet(),object :RoomOperateCallback{
            override fun onSuccess() {
                ToastUtils.show(context.getString(R.string.tips_switch_public_room_success))
                callback.onSuccess()
            }

            override fun onFailed(code: Int) {
                ToastUtils.show(context.getString(R.string.tips_switch_public_room_failed) + "$code")
                callback.onFailed(code)
            }
        })
    }


    private fun addToBlackList(context: Context, uids: Set<Long>) {
        LiveApplication.avEngine().updateBlackUids(0, "", uids, emptySet(), object : RoomOperateCallback {
            override fun onSuccess() {
                LiveApplication.avEngine().kickUser(0, "", uids, 1, null)

                ToastUtils.show(PrintUtils.toString(uids) + context.getString(R.string.tips_be_added_black_success))
            }

            override fun onFailed(code: Int) {
                ToastUtils.show(PrintUtils.toString(uids) + context.getString(R.string.tips_be_added_black_failed) + code)
            }
        })
    }

    private fun addToWhiteList(context: Context, uid: Set<Long>) {
        LiveApplication.avEngine().updateWhiteList(0, "", uid, emptySet(), object : RoomOperateCallback {
            override fun onSuccess() {
                ToastUtils.show(PrintUtils.toString(uid) + context.getString(R.string.tips_be_added_white_success))
            }
            override fun onFailed(code: Int) {
                ToastUtils.show(PrintUtils.toString(uid) + context.getString(R.string.tips_be_added_white_failed) + code)
            }
        })
    }

    private fun removeFromBackList(context: Context, uids: Set<Long>) {
        LiveApplication.avEngine().updateBlackUids(0, "", emptySet(), uids, object : RoomOperateCallback {
            override fun onSuccess() {
                ToastUtils.show(PrintUtils.toString(uids) + context.getString(R.string.tips_be_removed_black_success))
            }

            override fun onFailed(code: Int) {
                ToastUtils.show(PrintUtils.toString(uids) + context.getString(R.string.tips_be_removed_black_failed) + code)
            }
        })
    }

    private fun removeFromWhiteList(context: Context, uids: Set<Long>) {
        LiveApplication.avEngine().updateWhiteList(0, "", emptySet(), uids, object : RoomOperateCallback {
            override fun onSuccess() {
                ToastUtils.show(PrintUtils.toString(uids) + context.getString(R.string.tips_be_removed_white_success))
            }

            override fun onFailed(code: Int) {
                ToastUtils.show(PrintUtils.toString(uids) + context.getString(R.string.tips_be_removed_white_failed) + code)
            }
        })
    }

    private fun kickUser(context: Context, uids: Set<Long>) {
        LiveApplication.avEngine().kickUser(0, "", uids, 60, object : RoomOperateCallback {
            override fun onSuccess() {
                ToastUtils.show(PrintUtils.toString(uids) + context.getString(R.string.tips_kick_success))
            }

            override fun onFailed(code: Int) {
                ToastUtils.show(PrintUtils.toString(uids) + context.getString(R.string.tips_kick_failed) + code)
            }
        })
    }


    fun showRoomOperateDialog(activity: Activity,operateType: RoomOperateType) {
        val pkInputView: View = LayoutInflater.from(activity).inflate(R.layout.dialog_input_uid, null)
        val et_black_list = pkInputView.findViewById<EditText>(R.id.et_black_uid)
        et_black_list.setText("4611686018427427919")

        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle(RoomOperateType.getTitleByOperateType(activity, operateType))
        builder.setView(pkInputView)
        builder.setPositiveButton(R.string.tips_confirm
        ) { _, _ ->
            val uidStr = et_black_list.text.toString()
            try {
                val uidList = mutableSetOf<Long>(java.lang.Long.valueOf(uidStr))
                when (operateType) {
                    RoomOperateType.ADD_TO_BLACK_LIST -> addToBlackList(activity, uidList)
                    RoomOperateType.ADD_TO_WHITE_LIST -> addToWhiteList(activity, uidList)
                    RoomOperateType.REMOVE_FORM_BLACKLIST -> removeFromBackList(activity, uidList)
                    RoomOperateType.REMOVE_FORM_WHITELIST -> removeFromWhiteList(activity, uidList)
                }
            } catch (e: Throwable) {
                ToastUtils.show(activity.getString(R.string.tips_uid_format_invailded))
            }
        }.setNegativeButton(R.string.tips_cancel) { _, _ -> }
        builder.show()
    }

}