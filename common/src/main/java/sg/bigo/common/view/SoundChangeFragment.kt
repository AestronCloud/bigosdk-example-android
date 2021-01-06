package sg.bigo.common.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.fragment_sound_change.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import sg.bigo.common.LiveApplication
import sg.bigo.common.R


private const val TAG = "TabFragment"
class SoundChangeFragment : BeautifyFragment() {

    val mAVEngine = LiveApplication.avEngine()

    companion object {
        val SOUND_TYPE = "sound_type"
        val REVERBERATION_TYPE = "reverberation_type"
        val EQUALIZER_TYPE = "equalizer_type"
        val PROGRESS_POS = "progress_pos"

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GlobalScope.launch(Dispatchers.IO) {
            mUIHandler.post {
                onDataInit();
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
    }

    override fun onPause() {
        super.onPause()
    }

    private fun onDataInit() {
        if(isStoped) return

        //读取用户设置的信息
        val user_sound_type = getUserConfig(SOUND_TYPE)
        val user_reverberation_type = getUserConfig(REVERBERATION_TYPE)
        val user_equalizer_type = getUserConfig(EQUALIZER_TYPE)
        val user_progress_pos = getUserConfig(PROGRESS_POS)


        //变声类型
        val sound_item = resources.getStringArray(R.array.sound_types)
        val sound_type_adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, sound_item)
        sound_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sound_type_sp!!.setAdapter(sound_type_adapter)
        sound_type_sp.setSelection(user_sound_type!!)
        sound_type_sp.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
//                Toast.makeText(context, "点击了" + position, Toast.LENGTH_SHORT).show();
                val res = mAVEngine.setLocalVoiceChanger(position)
                if (res == 0) {
                    saveUserConfig(context!!, SOUND_TYPE, position)
                }
//                Log.d(TAG, "setLocalVoiceChanger " + position + " success = " + (res==0))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        //混响类型
        val reverberation_item = resources.getStringArray(R.array.reverberation_types)
        val reverberation_type_adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, reverberation_item)
        reverberation_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        reverberation_type_sp!!.setAdapter(reverberation_type_adapter)
        reverberation_type_sp.setSelection(user_reverberation_type!!)
        reverberation_type_sp.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
//                Toast.makeText(context, "点击了" + position, Toast.LENGTH_SHORT).show();
                val res = mAVEngine.setLocalVoiceReverbPreset(position)
                if (res == 0) {
                    saveUserConfig(context!!, REVERBERATION_TYPE, position)
                }
//                Log.d(TAG, "setLocalVoiceReverbPreset " + position + " success = " + (res==0))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        //均衡器类型
        val equalizer_item = resources.getStringArray(R.array.equalizer_types)
        val equalizer_type_adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, equalizer_item)
        equalizer_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        equalizer_type_sp!!.setAdapter(equalizer_type_adapter)
        equalizer_type_sp.setSelection(user_equalizer_type!!)
        equalizer_type_sp.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
//                Toast.makeText(context, "点击了" + position, Toast.LENGTH_SHORT).show();
                val res = mAVEngine.setLocalVoiceEqualizerPreset(position)
                if (res == 0) {
                    saveUserConfig(context!!, EQUALIZER_TYPE, position)
                }
//                Log.d(TAG, "setLocalVoiceEqualizerPreset " + position + " success = " + (res==0))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        //音调拖动条
        val maxProgress = 205
        val programeD = (maxProgress - 50) / 5
        mPitchSeekBar.setMax(programeD)
        mPitchSeekBar.setProgress(user_progress_pos!!)
        mPitchSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var mCurProgress = 0
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (mCurProgress > 0) {
                    val pitch = (50 + (mCurProgress - 1) * 5) / 100.0
//                    Toast.makeText(context, "pitch: " + pitch, Toast.LENGTH_SHORT).show();
                    val res = mAVEngine.setLocalVoicePitch(pitch)
                    if (res == 0) {
                        saveUserConfig(context!!, PROGRESS_POS, mCurProgress)
                    }
//                    Log.d(TAG, "setLocalVoicePitch mCurProgress>0  mCurProgress = " + mCurProgress + " success = " + (res==0))
                } else {
                    val res = mAVEngine.setLocalVoicePitch(1.0)
                    if (res == 0) {
                        saveUserConfig(context!!, PROGRESS_POS, 0)
                    }
//                    Log.d(TAG, "setLocalVoicePitch mCurProgress <= 0 mCurProgress = " + mCurProgress + " success = " + (res==0))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                mCurProgress = progress
            }
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_sound_change, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun saveUserConfig(context: Context, key: String, data: Int) {
        val sp = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(key, data.toString())
        editor.commit()
    }

    private fun getUserConfig(key: String): Int? {
        val sp: SharedPreferences = context!!.getSharedPreferences(key, Context.MODE_PRIVATE)
        val userconfig = sp.getString(key, "0")
        return Integer.valueOf(userconfig)
    }

    private val mUIHandler = Handler(Looper.getMainLooper())
}