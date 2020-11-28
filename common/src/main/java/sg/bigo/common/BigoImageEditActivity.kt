package sg.bigo.common

import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import sg.bigo.common.fragment.BigoImageEditFragment
import sg.bigo.common.fragment.TranscodingCfgFragment
import sg.bigo.common.fragment.TranscodingUserEditFragment

class BigoImageEditActivity: BaseActivity() {

    private val mBigoImageEditFragment: BigoImageEditFragment by lazy {
        BigoImageEditFragment().apply {
            if (TranscodingCfgFragment.isEditWatermarkOrBgImage) {
                put(TranscodingCfgFragment.liveTranscoding.watermark)
            } else {
                put(TranscodingCfgFragment.liveTranscoding.backgroundImage)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(android.R.id.content, mBigoImageEditFragment)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        if (TranscodingCfgFragment.isEditWatermarkOrBgImage) {
            TranscodingCfgFragment.liveTranscoding.watermark = mBigoImageEditFragment.loadBigoImage()
        } else {
            TranscodingCfgFragment.liveTranscoding.backgroundImage = mBigoImageEditFragment.loadBigoImage()
        }
        super.onBackPressed()
    }
}