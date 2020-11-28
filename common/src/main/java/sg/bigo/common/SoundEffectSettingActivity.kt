package sg.bigo.common

import android.R
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import sg.bigo.common.view.FilterFragment
import sg.bigo.common.view.SkinBeautyFragment
import sg.bigo.common.view.SoundEffectFragment

class SoundEffectSettingActivity: BottomActivity() {

    private val soundEffectFragment: SoundEffectFragment by lazy {
        SoundEffectFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content, soundEffectFragment)
        fragmentTransaction.commit()
    }

}
