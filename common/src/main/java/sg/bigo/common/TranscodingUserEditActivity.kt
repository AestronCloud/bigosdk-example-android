package sg.bigo.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import sg.bigo.common.fragment.TranscodingUserEditFragment
import sg.bigo.opensdk.api.struct.BigoTranscodingUser

class TranscodingUserEditActivity : BaseActivity() {
    companion object {

        fun go(ctx: Context, user: BigoTranscodingUser) {
            (ctx as Activity).startActivityForResult(Intent(ctx,TranscodingUserEditActivity::class.java).apply {
                putExtra(key_transcoding_user, user)
            },code_start_activity)
        }

        fun praseIntentExtra(intent: Intent):BigoTranscodingUser {
            return intent.getParcelableExtra(key_transcoding_user) ?: TranscodingListEditActivity.emptyTranscodingUser()
        }

        const val key_transcoding_user = "key_transcoding_user"
        val code_start_activity = 0b00000001
    }

    private val mTranscodingUserEditFragment: TranscodingUserEditFragment by lazy {
        TranscodingUserEditFragment().apply {
            put(praseIntentExtra(intent))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(android.R.id.content, mTranscodingUserEditFragment)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        setResult(code_start_activity,Intent().apply {
            putExtra(key_transcoding_user,mTranscodingUserEditFragment.loadTranscodingUser())
        })
        super.onBackPressed()
    }
}