package sg.bigo.common.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import sg.bigo.common.LiveApplication
import sg.bigo.common.R
import sg.bigo.opensdk.api.struct.BigoImageConfig

class BigoImageEditFragment: PreferenceFragmentCompat() {
    companion object {
        val key_bigo_image_url = "bigo_image_url"
        val key_bigo_image_x = "bigo_image_x"
        val key_bigo_image_y = "bigo_image_y"
        val key_bigo_image_width = "bigo_image_width"
        val key_bigo_image_height = "bigo_image_height"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.fragment_bigo_image_edit)
    }

    private lateinit var mBigoImage: BigoImageConfig

    fun put(bigoImage: BigoImageConfig) {
        mBigoImage = bigoImage
        saveTranscodingUser(mBigoImage)
    }

    fun loadBigoImage() : BigoImageConfig {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(LiveApplication.appContext!!)
        prefs.run {
            val url = getString(key_bigo_image_url,"")
            val x = getString(key_bigo_image_x,"0").toInt()
            val y = getString(key_bigo_image_y,"0").toInt()
            val width = getString(key_bigo_image_width,"0").toInt()
            val height = getString(key_bigo_image_height,"0").toInt()
            return BigoImageConfig(url, x, y, width, height)
        }
    }

    private fun saveTranscodingUser(bigoImage: BigoImageConfig){
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(LiveApplication.appContext!!)
        prefs.edit().run {
            putString(key_bigo_image_url,"${bigoImage.url}")
            putString(key_bigo_image_x,"${bigoImage.x}")
            putString(key_bigo_image_y,"${bigoImage.y}")
            putString(key_bigo_image_width,"${bigoImage.width}")
            putString(key_bigo_image_height,"${bigoImage.height}")
        }.apply()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

    }
}
