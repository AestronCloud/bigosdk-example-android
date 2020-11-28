package sg.bigo.common

import android.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import sg.bigo.common.fragment.TranscodingCfgFragment

class TranscodingCfgActivity : AppCompatActivity() {
    private val mTranscodingCfgFragment : TranscodingCfgFragment by lazy {
        TranscodingCfgFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content, mTranscodingCfgFragment)
        fragmentTransaction.commit()
    }

    override fun onPause() {
        super.onPause()
        if(!isChangingConfigurations && isFinishing) {
            mTranscodingCfgFragment.loadTranscodingCfg()
        }
    }

    override fun onStop() {
        super.onStop()
        if(!isChangingConfigurations && isFinishing) {
            mTranscodingCfgFragment.loadTranscodingCfg()
        }
    }
}