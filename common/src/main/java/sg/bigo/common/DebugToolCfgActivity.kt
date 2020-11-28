package sg.bigo.common

import android.R
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentTransaction
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceClickListener
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import sg.bigo.common.utils.BigoEngineConfig.*

class DebugToolCfgActivity: BaseActivity() {

    private val mDebugToolCfgFragment : DebugToolCfgFragment by lazy {
        DebugToolCfgFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content, mDebugToolCfgFragment)
        fragmentTransaction.commit()
    }


    class DebugToolCfgFragment : PreferenceFragmentCompat() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(sg.bigo.common.R.xml.fragment_debug_tool_prefs)
            setShouldRestartListener(key_debug_cfg_lbs_ip)
            setShouldRestartListener(key_debug_cfg_lbs_port)
            setShouldRestartListener(key_debug_cfg_env_enabled)
            setShouldRestartListener(key_debug_cfg_is_test_env)
            setShouldRestartListener(key_debug_cfg_appid)
            setShouldRestartListener(key_debug_cfg_cert)
        }

        private fun setShouldRestartListener(key: String) {
            findPreference<Preference>(key)?.run {
                onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
                    toChooseRestart()
                    true
                }
            }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        }


        private fun toChooseRestart() {
            val dialogBuilder = AlertDialog.Builder(activity!!)
            dialogBuilder.setMessage("重启生效，是否重启?")
            dialogBuilder.setNegativeButton("是") { _, _ ->
                val a:String? = null
                a!!
            }.setPositiveButton("否") { _, _ ->

            }
            dialogBuilder.show()
        }

    }
}
