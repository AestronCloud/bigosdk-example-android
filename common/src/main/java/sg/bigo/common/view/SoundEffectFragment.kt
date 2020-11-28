package sg.bigo.common.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.fragment_sound_effect.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import sg.bigo.common.LiveApplication
import sg.bigo.common.R
import sg.bigo.common.data.ModelUtils
import sg.bigo.common.utils.Decompress.copyAssets
import sg.bigo.common.utils.ToastUtils
import sg.bigo.opensdk.api.AVEngineConstant
import sg.bigo.opensdk.api.IAVEngineCallback
import java.io.File


private const val TAG = "TabFragment"
class SoundEffectFragment : BeautifyFragment() {

    val mAVEngine = LiveApplication.avEngine()

    companion object {
        val SOUND_PLAY_MIX = "sound_play_mix"
        val SOUND_PLAY_EFFECT = mutableListOf("sound_play_effect","sound_play_effect1","sound_play_effect2")

        fun soundMixPath(): String {
            val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(LiveApplication.appContext!!)
            return prefs.getString(SOUND_PLAY_MIX,"")
        }

        fun soundEffectPath(pos:Int): String {
            val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(LiveApplication.appContext!!)
            return prefs.getString(SOUND_PLAY_EFFECT[pos],"")
        }

        fun resetMusic(){

            SoundEffectFragment.isPlayingMixSound = false
            for(i in 0 until SoundEffectFragment.isPlayingEffectSounds.size) {
                SoundEffectFragment.isPlayingEffectSounds[i] = false
            }
            LiveApplication.avEngine().stopAllEffects();
            LiveApplication.avEngine().stopAudioMixing();
        }


        var isPlayingMixSound = false;
        var isPlayingEffectSounds = mutableListOf<Boolean>(false,false,false)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GlobalScope.launch(Dispatchers.IO) {
            if(!copyAssets("background.m4a",ModelUtils.soundDir)
                    || !copyAssets("effect1.wav",ModelUtils.soundDir)
                    || !copyAssets("effect2.wav",ModelUtils.soundDir)
                    || !copyAssets("song_yijianmei.mp3",ModelUtils.soundDir)) {
                mUIHandler.post {
                    ToastUtils.show("文件拷贝失败")
                }
                return@launch
            }

            val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(LiveApplication.appContext!!)
            prefs.edit().run {
                putString(SOUND_PLAY_EFFECT[0],"${ModelUtils.soundDir}/background.m4a")
                putString(SOUND_PLAY_EFFECT[1],"${ModelUtils.soundDir}/effect1.wav")
                putString(SOUND_PLAY_EFFECT[2],"${ModelUtils.soundDir}/effect2.wav")
                putString(SOUND_PLAY_MIX,"${ModelUtils.soundDir}/song_yijianmei.mp3")
            }.apply()


            mUIHandler.post {
                onDataInit();
            }
        }

    }

    val liveCallback = object : IAVEngineCallback() {
        override fun onAudioMixingStateChanged(state: Int, value: Int) {
            mUIHandler.post {

                when (state) {
                    AVEngineConstant.AudioEventMixingState.MEDIA_ENGINE_AUDIO_EVENT_MIXING_PLAY -> {
                        isPlayingMixSound = true
                        mix_play_toggle.text = if(isPlayingMixSound) getString(R.string.tips_pause_play_mix) else getString(R.string.tips_start_play_mix)
                    }
                    AVEngineConstant.AudioEventMixingState.MEDIA_ENGINE_AUDIO_EVENT_MIXING_PAUSED -> {
                    }
                    AVEngineConstant.AudioEventMixingState.MEDIA_ENGINE_AUDIO_EVENT_MIXING_STOPPED -> {
                        mix_sound_progress.progress = 0
                        isPlayingMixSound = false
                        mix_play_toggle.text = if(isPlayingMixSound) getString(R.string.tips_pause_play_mix) else getString(R.string.tips_start_play_mix)
                    }
                    AVEngineConstant.AudioEventMixingState.MEDIA_ENGINE_AUDIO_EVENT_MIXING_ERROR -> {

                    }
                    AVEngineConstant.AudioEventMixingState.MEDIA_ENGINE_AUDIO_EVENT_MIXING_PROGRESS -> {
                        mix_sound_progress.max = mAVEngine.audioMixingDuration
                        mix_sound_progress.progress = value
                    }
                }

                when (value) {
                    AVEngineConstant.AudioEventMixingResaonCode.MEDIA_ENGINE_AUDIO_ERROR_MIXING_OPEN -> {
                    }
                    AVEngineConstant.AudioEventMixingResaonCode.MEDIA_ENGINE_AUDIO_ERROR_MIXING_TOO_FREQUENT -> {
                    }
                    AVEngineConstant.AudioEventMixingResaonCode.MEDIA_ENGINE_AUDIO_EVENT_MIXING_INTERRUPTED_EOF -> {
                    }
                }
            }

        }

        override fun onAudioEffectStateChange(state: Int, soundID: Int, value: Int) {
            when (state) {
                AVEngineConstant.AudioEventMixingState.MEDIA_ENGINE_AUDIO_EVENT_MIXING_PLAY -> {
                    isPlayingEffectSounds[soundID] = true
                    effect_play_toggles[soundID].text = if(isPlayingEffectSounds[soundID]) getString(R.string.tips_pause_play_mix) else getString(R.string.tips_start_play_mix)

                }
                AVEngineConstant.AudioEventMixingState.MEDIA_ENGINE_AUDIO_EVENT_MIXING_PAUSED -> {
                }
                AVEngineConstant.AudioEventMixingState.MEDIA_ENGINE_AUDIO_EVENT_MIXING_STOPPED -> {
                    effect_seekBars[soundID].progress = 0
                    isPlayingEffectSounds[soundID] = false
                    effect_play_toggles[soundID].text = if(isPlayingEffectSounds[soundID]) getString(R.string.tips_pause_play_mix) else getString(R.string.tips_start_play_mix)
                }
                AVEngineConstant.AudioEventMixingState.MEDIA_ENGINE_AUDIO_EVENT_MIXING_ERROR -> {

                }
                AVEngineConstant.AudioEventMixingState.MEDIA_ENGINE_AUDIO_EVENT_MIXING_PROGRESS -> {
                    effect_seekBars[soundID].max = mAVEngine.getEffectFileDuration(soundID)
                    effect_seekBars[soundID].progress = value
                }
            }

            when (value) {
                AVEngineConstant.AudioEventMixingResaonCode.MEDIA_ENGINE_AUDIO_ERROR_MIXING_OPEN -> {
                }
                AVEngineConstant.AudioEventMixingResaonCode.MEDIA_ENGINE_AUDIO_ERROR_MIXING_TOO_FREQUENT -> {
                }
                AVEngineConstant.AudioEventMixingResaonCode.MEDIA_ENGINE_AUDIO_EVENT_MIXING_INTERRUPTED_EOF -> {
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun enable() {
    }

    override fun disable() {
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onResume() {
        super.onResume()
        mAVEngine.addCallback(liveCallback)
    }

    override fun onPause() {
        super.onPause()
        mAVEngine.removeCallback(liveCallback)
    }

    private fun onDataInit() {
        if(isStoped) return
        mix_sound_path.text = File(soundMixPath()).name
        for(i in 0 until effect_sound_paths.size) {
            effect_sound_paths[i].text = File(soundEffectPath(i)).name
            effect_play_toggles[i].text = if(isPlayingEffectSounds[i]) getString(R.string.tips_pause_play_mix) else getString(R.string.tips_start_play_mix)
            effect_play_toggles[i].setOnClickListener {
                if(!isPlayingEffectSounds[i]) {
                    mAVEngine.playEffect(i,soundEffectPath(i),1,1.0,0.0,100.0, true,true)
                } else {
                    mAVEngine.stopEffect(i);
                }

            }

            effect_seekBars[i].setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{

                var endProgress = -1
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (!isPlayingEffectSounds[i]) {
                        return
                    }

                    if(fromUser) {
                        endProgress = progress
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    endProgress = -1

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    if(endProgress != -1) {
                        mAVEngine.setCurrentEffectFilePlayPosition(i,endProgress)
                    }
                }
            })
        }

        mix_play_toggle.text = if(isPlayingMixSound) getString(R.string.tips_pause_play_mix) else getString(R.string.tips_start_play_mix)
        mix_play_toggle.setOnClickListener {
            if (!isPlayingMixSound) {
                mAVEngine.startAudioMixing(soundMixPath(),false,false,1)
            } else {
                mAVEngine.stopAudioMixing()
            }
        }

        mix_sound_progress.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{

            var endProgress = -1

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(!isPlayingMixSound) return
                if(fromUser) {
                    endProgress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                endProgress = -1
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if(endProgress != -1) {
                    mAVEngine.setAudioMixingPosition(endProgress)
                }
            }

        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_sound_effect
                , container, false)
        return v
    }


    val effect_sound_paths: MutableList<TextView> by lazy {
        mutableListOf(effect_sound_path,effect_sound_path1,effect_sound_path2)
    }

    val effect_play_toggles: MutableList<Button> by lazy {
        mutableListOf(effect_play_toggle,effect_play_toggle1,effect_play_toggle2)
    }

    val effect_seekBars: MutableList<SeekBar> by lazy {
        mutableListOf(effect_sound_progress,effect_sound_progress1,effect_sound_progress2)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }



    private val mUIHandler = Handler(Looper.getMainLooper())
}