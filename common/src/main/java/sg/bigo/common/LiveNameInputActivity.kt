package sg.bigo.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_live_name_input.*
import sg.bigo.common.utils.PremissionProcesser
import sg.bigo.common.utils.ViewHelper
import sg.bigo.opensdk.api.AVEngineConstant
import sg.bigo.common.LiveApplication.Companion.AppType


const val KEY_USER_NAME = "key_user_name"
const val KEY_CHANNEL_NAME = "key_channel_name"
const val KEY_CLIENT_ROLE = "key_client_role"

interface IChooseRoleListener {
    fun onChose(role: Int)
}

open class LiveNameInputActivity : BaseActivity() {
    private fun hideSomeLiveType() {
        val shouldShowEntrances : MutableList<View> = when (LiveApplication.getAppType()) {
            AppType.basicbeauty -> mutableListOf(btnbasicbeauty)
            AppType.one2onevideo -> mutableListOf(btn1v1Live)
            AppType.one2onevoice -> mutableListOf(btn1v1Voice)
            AppType.multilive -> mutableListOf(btnMultiLive,btn_pk)
            AppType.multivoice -> mutableListOf(btnSixVoice)
            AppType.sixseatvideolive -> mutableListOf(btnSixVideo)
            AppType.basicbeauty1v1video -> mutableListOf(btnbasicbeauty1v1)
            else -> mutableListOf()
        }

        for (i in 0 until ll_entrance_container.childCount) {
            val child = ll_entrance_container[i]

            if(shouldShowEntrances.isNotEmpty()) {
                if(shouldShowEntrances.contains(child)) {
                    child.visibility = View.VISIBLE
                } else {
                    child.visibility = View.GONE
                }
            } else {
                child.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        fun go(context: Context, userName: String) {
            val intent = Intent(context,LiveNameInputActivity::class.java)
            intent.putExtra(KEY_USER_NAME,userName)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_name_input)

        var userName = intent.getStringExtra(KEY_USER_NAME)

        if(userName == null) {
            userName = "fake_user_name_" + "${Math.random()}".subSequence(0,8)
        }

        tvUserName.text = getString(R.string.tips_current_user) + userName

        etLiveName.setText(LiveApplication.config.channelName)
        ViewHelper.mutuallyExclusiveEnableView(etLiveName, arrayListOf(btnMultiLive,btn1v1Live,btn1v1Voice,btnSixVoice,btnSixVideo,btnbasicbeauty1v1,btnbasicbeauty))
        etLiveName.requestFocus()



        fun toChooseRole(chooseRoleListener: IChooseRoleListener) {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage(getString(R.string.tips_choose_broadcast_or_audience))
            dialogBuilder.setNegativeButton(getString(R.string.tips_watch_broadcast)) { _, _ ->
                chooseRoleListener.onChose(role = AVEngineConstant.ClientRole.ROLE_AUDIENCE)
            }.setPositiveButton(getString(R.string.tips_start_broadcast)) { _, _ ->
                chooseRoleListener.onChose(role = AVEngineConstant.ClientRole.ROLE_BROADCAST)
            }
            dialogBuilder.show()
        }


        btnMultiLive.setOnClickListener {
            toChooseRole(object :IChooseRoleListener{
                override fun onChose(role: Int) {
                    val intent = Intent(this@LiveNameInputActivity,MultiLiveActivity::class.java)
                    intent.putExtra(KEY_USER_NAME,userName)
                    intent.putExtra(KEY_CHANNEL_NAME,etLiveName.text.toString())
                    intent.putExtra(KEY_CLIENT_ROLE,role)
                    startActivity(intent)
                }
            })
        }

        btnbasicbeauty.setOnClickListener {
            toChooseRole(object :IChooseRoleListener{
                override fun onChose(role: Int) {
                    val intent = Intent(this@LiveNameInputActivity,BasicBeautyLiveActivity::class.java)
                    intent.putExtra(KEY_USER_NAME,userName)
                    intent.putExtra(KEY_CHANNEL_NAME,etLiveName.text.toString())
                    intent.putExtra(KEY_CLIENT_ROLE,role)
                    startActivity(intent)
                }
            })
        }

        btnbasicbeauty1v1.setOnClickListener {
            val intent = Intent(this@LiveNameInputActivity,BasicBeauty1v1VideoActivity::class.java)
            intent.putExtra(KEY_USER_NAME,userName)
            intent.putExtra(KEY_CHANNEL_NAME,etLiveName.text.toString())
            startActivity(intent)
        }

        btn1v1Live.setOnClickListener {
            val intent = Intent(this@LiveNameInputActivity,One2OneLiveActivity::class.java)
            intent.putExtra(KEY_USER_NAME,userName)
            intent.putExtra(KEY_CHANNEL_NAME,etLiveName.text.toString())
            startActivity(intent)

        }

        btn1v1Voice.setOnClickListener {
            val intent = Intent(this@LiveNameInputActivity,One2OneVoiceActivity::class.java)
            intent.putExtra(KEY_USER_NAME,userName)
            intent.putExtra(KEY_CHANNEL_NAME,etLiveName.text.toString())
            startActivity(intent)
        }


        btnSixVoice.setOnClickListener {
            toChooseRole(object :IChooseRoleListener{
                override fun onChose(role: Int) {
                    val intent = Intent(this@LiveNameInputActivity,SixSeatVoiceActivity::class.java)
                    intent.putExtra(KEY_USER_NAME,userName)
                    intent.putExtra(KEY_CHANNEL_NAME,etLiveName.text.toString())
                    intent.putExtra(KEY_CLIENT_ROLE,role)
                    startActivity(intent)
                }
            })
        }

        btnSixVideo.setOnClickListener {
            toChooseRole(object :IChooseRoleListener{
                override fun onChose(role: Int) {
                    val intent = Intent(this@LiveNameInputActivity,SixSeatVideoActivity::class.java)
                    intent.putExtra(KEY_USER_NAME,userName)
                    intent.putExtra(KEY_CHANNEL_NAME,etLiveName.text.toString())
                    intent.putExtra(KEY_CLIENT_ROLE,role)
                    startActivity(intent)
                }
            })
        }

        btn_pk.setOnClickListener {
            toChooseRole(object :IChooseRoleListener{
                override fun onChose(role: Int) {
                    val intent = Intent(this@LiveNameInputActivity,LivePKActivity::class.java)
                    intent.putExtra(KEY_USER_NAME,userName)
                    intent.putExtra(KEY_CHANNEL_NAME,etLiveName.text.toString())
                    intent.putExtra(KEY_CLIENT_ROLE,role)
                    startActivity(intent)
                }
            })
        }


        go_settings.setOnClickListener {
            startActivity(Intent(this,DebugToolCfgActivity::class.java))
        }


        hideSomeLiveType()

        PremissionProcesser.checkSelfPermissions(this)
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        LiveApplication.config.channelName = etLiveName.text.toString()
        super.startActivityForResult(intent, requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PremissionProcesser.onRequestPermissionsResult(this,requestCode, grantResults)
    }
}
