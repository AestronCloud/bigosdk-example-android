package sg.bigo.common.fragment

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.polly.mobile.mediasdk.*
import sg.bigo.common.BigoImageEditActivity
import sg.bigo.common.LiveApplication
import sg.bigo.common.R
import sg.bigo.common.TranscodingListEditActivity
import sg.bigo.opensdk.api.struct.BigoImageConfig
import sg.bigo.opensdk.api.struct.BigoTranscodingUser
import sg.bigo.opensdk.api.struct.LiveTranscoding


class TranscodingCfgFragment :  PreferenceFragmentCompat(){

    companion object {
        val key_transcoding_cfg_width = "transcoding_cfg_width"
        val key_transcoding_cfg_height = "transcoding_cfg_height"
        val key_transcoding_cfg_video_bitrate = "transcoding_cfg_video_bitrate"
        val key_transcoding_cfg_video_framerate = "transcoding_cfg_video_framerate"
        val key_transcoding_cfg_low_latency = "transcoding_cfg_low_latency"
        val key_transcoding_cfg_video_gop = "transcoding_cfg_video_gop"
        val key_transcoding_cfg_video_codec_profile = "transcoding_cfg_video_codec_profile"
        val key_transcoding_cfg_transcoding_users = "transcoding_cfg_transcoding_users"
        val key_transcoding_cfg_extra_info = "transcoding_cfg_extra_info"
        val key_transcoding_cfg_watermark = "transcoding_cfg_watermark"
        val key_transcoding_cfg_background_img = "transcoding_cfg_background_img"
        val key_transcoding_cfg_background_color = "transcoding_cfg_background_color"
        val key_transcoding_cfg_audio_sample_rate = "transcoding_cfg_audio_sample_rate"
        val key_transcoding_cfg_audio_bitrate = "transcoding_cfg_audio_bitrate"
        val key_transcoding_cfg_audio_channel = "transcoding_cfg_audio_channel"
        val key_transcoding_cfg_audio_codec_profile = "transcoding_cfg_audio_codec_profile"

        fun replaceTranscodingUsers(users: MutableList<BigoTranscodingUser>) {
            liveTranscoding.transcodingUsers.clear()
            for (user in users) {
                liveTranscoding.transcodingUsers[user.uid] = user
            }
        }

        var isEditWatermarkOrBgImage = false

        val liveTranscoding : LiveTranscoding = LiveTranscoding().apply {
            width = 720
            height = 1080
            videoBitrate = 1400
            videoFramerate = 30
            lowLatency = false
            videoGop = 30;
            videoCodecProfile = VideoCodecProfileType.MAIN
            userConfigExtraInfo = LiveApplication.config.liveExtraInfo

            watermark = BigoImageConfig("https://tse2-mm.cn.bing.net/th/id/OIP.x2Uv_SU7eDIjWnotR5_bXAHaEo?w=197&h=123&c=7&o=5&dpr=2&pid=1.7", 500, 500, 100, 100)
            backgroundImage = BigoImageConfig("https://www.cleverfiles.com/howto/wp-content/uploads/2018/03/minion.jpg", 0, 0, 100, 100)
            backgroundColor = "FFB6C1"
            audioSampleRateType = AudioSampleRateType.TYPE_32000
            audioBitrate = 100
            audioChannels = 2
            audioCodecProfile = AudioCodecProfileType.HE_AAC
        }
    }

    var mChangeListener: SharedPreferences.OnSharedPreferenceChangeListener? = null
    var mActivity: Activity? = null

    private fun saveLiveTranscoding() {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(LiveApplication.appContext!!)
        prefs.edit().run {
            putString(key_transcoding_cfg_width,"${liveTranscoding.width}")
            putString(key_transcoding_cfg_height,"${liveTranscoding.height}")
            putString(key_transcoding_cfg_video_bitrate,"${liveTranscoding.videoBitrate}")
            putString(key_transcoding_cfg_video_framerate,"${liveTranscoding.videoFramerate}")
            putBoolean(key_transcoding_cfg_low_latency,liveTranscoding.lowLatency)
            putString(key_transcoding_cfg_video_gop,"${liveTranscoding.videoGop}")
            putString(key_transcoding_cfg_video_codec_profile,"${liveTranscoding.videoCodecProfile}")
//            putString(key_transcoding_cfg_transcoding_users,"${liveTranscoding.uid}")
            putString(key_transcoding_cfg_extra_info,"${liveTranscoding.userConfigExtraInfo}")
//            putString(key_transcoding_cfg_watermark,"${liveTranscoding.uid}")
//            putString(key_transcoding_cfg_background_img,"${liveTranscoding.backgroundImage}")
            putString(key_transcoding_cfg_background_color,"${liveTranscoding.backgroundColor}")
            putString(key_transcoding_cfg_audio_sample_rate,"${liveTranscoding.audioSampleRateType}")
            putString(key_transcoding_cfg_audio_bitrate,"${liveTranscoding.audioBitrate}")
            putString(key_transcoding_cfg_audio_channel,"${liveTranscoding.audioChannels}")
            putString(key_transcoding_cfg_audio_codec_profile,"${liveTranscoding.audioCodecProfile}")
        }.apply()
    }


    fun loadTranscodingCfg() {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(LiveApplication.appContext!!)
        prefs.run {
            liveTranscoding.width = getString(key_transcoding_cfg_width,"${liveTranscoding.width}").toInt()
            liveTranscoding.height = getString(key_transcoding_cfg_height,"${liveTranscoding.height}").toInt()
            liveTranscoding.videoBitrate = getString(key_transcoding_cfg_video_bitrate,"${liveTranscoding.videoBitrate}").toInt()
            liveTranscoding.videoFramerate = getString(key_transcoding_cfg_video_framerate,"${liveTranscoding.videoFramerate}").toInt()
            liveTranscoding.lowLatency = getBoolean(key_transcoding_cfg_low_latency,liveTranscoding.lowLatency)
            liveTranscoding.videoGop = getString(key_transcoding_cfg_video_gop,"${liveTranscoding.videoGop}").toInt()

            liveTranscoding.videoCodecProfile = when (getString(key_transcoding_cfg_video_codec_profile,"${liveTranscoding.videoCodecProfile}")) {
                VideoCodecProfileType.BASELINE.toString() -> {
                    VideoCodecProfileType.BASELINE
                }
                VideoCodecProfileType.MAIN.toString() -> {
                    VideoCodecProfileType.MAIN
                }
                else -> {
                    VideoCodecProfileType.HIGH
                }
            }

            liveTranscoding.userConfigExtraInfo = getString(key_transcoding_cfg_extra_info,"${liveTranscoding.userConfigExtraInfo}")

            liveTranscoding.backgroundColor = getString(key_transcoding_cfg_background_color,"${liveTranscoding.backgroundColor}")
            liveTranscoding.audioSampleRateType = when (getString(key_transcoding_cfg_audio_sample_rate,"${liveTranscoding.audioSampleRateType}")) {
                "TYPE_32000" -> {
                    AudioSampleRateType.TYPE_32000
                }
                "TYPE_44100" -> {
                    AudioSampleRateType.TYPE_44100
                }
                else -> {
                    AudioSampleRateType.TYPE_48000
                }
            }

            liveTranscoding.audioBitrate = getString(key_transcoding_cfg_audio_bitrate,"${liveTranscoding.audioBitrate}").toInt()

            liveTranscoding.audioChannels = getString(key_transcoding_cfg_audio_channel,"${liveTranscoding.audioChannels}").toInt()

            liveTranscoding.audioCodecProfile = when (getString(key_transcoding_cfg_audio_codec_profile,"${liveTranscoding.audioCodecProfile}")) {
                AudioCodecProfileType.LC_AAC.toString() -> {
                    AudioCodecProfileType.LC_AAC
                }
                else -> {
                    AudioCodecProfileType.HE_AAC
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity
        mChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->

            if("transcoding_cfg_transcoding_users" == key) {
                startActivity(Intent(mActivity, TranscodingListEditActivity::class.java))
            }
        }

        saveLiveTranscoding()

        addPreferencesFromResource(R.xml.fragment_transcoding_prefs)

        val tuPreference:Preference? = findPreference("transcoding_cfg_transcoding_users")
        tuPreference?.let {
            it.setOnPreferenceClickListener {
                startActivity(Intent(mActivity, TranscodingListEditActivity::class.java))
                return@setOnPreferenceClickListener true
            }
        }

        val watermarkPreference:Preference? = findPreference("transcoding_cfg_watermark")
        watermarkPreference?.let {
            it.setOnPreferenceClickListener {
                isEditWatermarkOrBgImage = true
                startActivity(Intent(mActivity, BigoImageEditActivity::class.java))
                return@setOnPreferenceClickListener true
            }
        }

        val bgImagePreference:Preference? = findPreference("transcoding_cfg_background_img")
        bgImagePreference?.let {
            it.setOnPreferenceClickListener {
                isEditWatermarkOrBgImage = false
                startActivity(Intent(mActivity, BigoImageEditActivity::class.java))
                return@setOnPreferenceClickListener true
            }
        }


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