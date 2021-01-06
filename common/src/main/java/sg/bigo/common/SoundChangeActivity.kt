package sg.bigo.common

import android.R
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import sg.bigo.common.view.FilterFragment
import sg.bigo.common.view.SkinBeautyFragment
import sg.bigo.common.view.SoundChangeFragment

class SoundChangeActivity: BottomActivity() {

    private val soundChangeFragment: SoundChangeFragment by lazy {
        SoundChangeFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content, soundChangeFragment)
        fragmentTransaction.commit()
    }

}
