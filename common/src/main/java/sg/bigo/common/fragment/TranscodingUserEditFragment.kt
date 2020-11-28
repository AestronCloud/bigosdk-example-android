package sg.bigo.common.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import sg.bigo.common.LiveApplication
import sg.bigo.common.R
import sg.bigo.opensdk.api.struct.BigoTranscodingUser

class TranscodingUserEditFragment: PreferenceFragmentCompat() {
    companion object {
        val key_tue_uid = "key_tue_uid"
        val key_tue_x = "key_tue_x"
        val key_tue_y = "key_tue_y"
        val key_tue_width = "key_tue_width"
        val key_tue_height = "key_tue_height"
        val key_tue_zorder = "key_tue_zorder"
        val key_tue_alpha = "key_tue_alpha"
        val key_tue_audio_channel = "key_tue_audio_channel"
    }


    fun put(user: BigoTranscodingUser) {
        mTranscodingUser = user
        saveTranscodingUser(mTranscodingUser)
//        intent.putExtra(key_tue_uid,"${user.uid}")
//        intent.putExtra(key_tue_x ,"${user.x}")
//        intent.putExtra(key_tue_y ,"${user.y}")
//        intent.putExtra(key_tue_width,"${user.width}")
//        intent.putExtra(key_tue_height,"${user.height}")
//        intent.putExtra(key_tue_zorder,"${user.zOrder}")
//        intent.putExtra(key_tue_alpha,"${user.alpha}")
//        intent.putExtra(key_tue_audio_channel,"${user.audioChannel}")
    }

    fun loadTranscodingUser() : BigoTranscodingUser {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(LiveApplication.appContext!!)
        prefs.run {
            val mUid = getString(key_tue_uid, "0").toLong()
            val mX = getString(key_tue_x, "0").toInt()
            val mY = getString(key_tue_y, "0").toInt()
            val mWidth = getString(key_tue_width, "0").toInt()
            val mHeight = getString(key_tue_height, "0").toInt()
            val mZOrder = getString(key_tue_zorder, "0").toInt()
            val mAlpha = getString(key_tue_alpha, "0").toFloat()
            val mAudioChannel = getString(key_tue_audio_channel, "0").toInt()
            return BigoTranscodingUser(mUid, mX, mY, mWidth, mHeight, mZOrder, mAlpha, mAudioChannel)
        }
    }


    private fun saveTranscodingUser(user: BigoTranscodingUser){
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(LiveApplication.appContext!!)
        prefs.edit().run {
            putString(key_tue_uid,"${user.uid}")
            putString(key_tue_x,"${user.x}")
            putString(key_tue_y,"${user.y}")
            putString(key_tue_width,"${user.width}")
            putString(key_tue_height,"${user.height}")
            putString(key_tue_zorder,"${user.zOrder}")
            putString(key_tue_alpha,"${user.alpha}")
            putString(key_tue_audio_channel,"${user.audioChannel}")
        }.apply()
    }

    var mChangeListener: SharedPreferences.OnSharedPreferenceChangeListener? = null
    lateinit var mTranscodingUser: BigoTranscodingUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if ("setting_wifi" == key || "setting_bluetouh" == key || "charge_lock_screen" == key || "never_sleep" == key) {

            } else if ("setting_timezone" == key) {

            }

            if("setting_wifi" == key) {
//                startActivity(Intent(activity, TranscodingListEditActivity::class.java).apply {
//                    putParcelableArrayListExtra(TranscodingListEditActivity.intent_key_user_list,ArrayList())
//                })
            }
        }

        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        if (preferences.getBoolean("vibrate", false)) {  //第二个参数指找不到该key的preference时，返回的默认值

        }
        if (preferences.getBoolean("ring", false)) {

        }

//        println("=====>>>>>${preferences.getLong(key_tue_uid, 0L)}")

        addPreferencesFromResource(R.xml.fragment_transcoding_user_edit)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

    }

    override fun onResume() {
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(mChangeListener);
        super.onResume()
    }

    override fun onPause() {
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(mChangeListener);
        super.onPause()
    }

}