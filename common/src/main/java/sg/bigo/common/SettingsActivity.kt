package sg.bigo.common

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import kotlinx.android.synthetic.main.activity_settings.*
import sg.bigo.common.utils.BigoEngineConfig
import sg.bigo.common.utils.Constants
import sg.bigo.common.utils.PrefManager
import sg.bigo.common.utils.WindowUtil.getSystemStatusBarHeight
import sg.bigo.common.view.FpsAdapter
import sg.bigo.common.view.ResolutionAdapter
import java.util.*

open class SettingsActivity : BaseActivity() {
    private var mStatusBarHeight = -1
    private lateinit var mVideoStatCheck: TextView
    private lateinit var mMirrorLocalText: TextView
    private lateinit var mMirrorRemoteText: TextView
    private lateinit var mMirrorEncodeText: TextView
    private lateinit var mSeekprogress: TextView
    private lateinit var mSeekBar: AppCompatSeekBar
    private val audioProfileConfigs: MutableMap<Int, Int> = HashMap()
    private val audioScenarioConfigs: MutableMap<Int, Int> = HashMap()
    private var mItemPadding = 0
    private var mResolutionAdapter: ResolutionAdapter? = null
    private var mFpsAdapter: FpsAdapter? = null
    private val mItemDecoration: ItemDecoration = object : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            outRect.top = mItemPadding
            outRect.bottom = mItemPadding
            outRect.left = mItemPadding
            outRect.right = mItemPadding
            val pos = parent.getChildAdapterPosition(view)
            if (pos < DEFAULT_SPAN) {
                outRect.top = 0
            }
            if (pos % DEFAULT_SPAN == 0) outRect.left =
                0 else if (pos % DEFAULT_SPAN == DEFAULT_SPAN - 1) outRect.right =
                0
        }
    }

    private var mPref: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        mStatusBarHeight = getSystemStatusBarHeight(this)
        mPref = PrefManager.getPreferences(applicationContext)
        initData()
        initUI()
    }

    private fun initData() {
        audioProfileConfigs[R.id.profile_DEFAULT] = 0
        audioProfileConfigs[R.id.SPEECH_STANDARD] = 1
        audioProfileConfigs[R.id.MUSIC_STANDARD] = 2
        audioProfileConfigs[R.id.MUSIC_STANDARD_STEREO] = 3
        audioProfileConfigs[R.id.MUSIC_HIGH_QUALITY] = 4
        audioProfileConfigs[R.id.MUSIC_HIGH_QUALITY_STEREO] = 5


        audioScenarioConfigs[R.id.DEFAULT] = 0
        audioScenarioConfigs[R.id.CHATROOM_ENTERTAINMENT] = 1
        audioScenarioConfigs[R.id.EDUCATION] = 2
        audioScenarioConfigs[R.id.GAME_STREAMING] = 3
        audioScenarioConfigs[R.id.SHOWROOM] = 4
        audioScenarioConfigs[R.id.CHATROOM_GAMING] = 5
    }

    private fun initUI() {
        val resolutionList =
            findViewById<RecyclerView>(R.id.resolution_list)
        resolutionList.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(this, DEFAULT_SPAN)
        resolutionList.layoutManager = layoutManager
        mResolutionAdapter =
            ResolutionAdapter(this, config().videoDimenIndex)
        resolutionList.adapter = mResolutionAdapter
        resolutionList.addItemDecoration(mItemDecoration)
        mItemPadding =
            resources.getDimensionPixelSize(R.dimen.setting_resolution_item_padding)
        val framerateList =
            findViewById<RecyclerView>(R.id.framerate_list)
        framerateList.setHasFixedSize(true)
        val frameLayoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(this, DEFAULT_SPAN)
        framerateList.layoutManager = frameLayoutManager
        mFpsAdapter = FpsAdapter(this, config().fpsIndex)
        framerateList.adapter = mFpsAdapter
        framerateList.addItemDecoration(mItemDecoration)
        mVideoStatCheck =
            findViewById(R.id.setting_stats_checkbox)
        mVideoStatCheck.isActivated = config().ifShowVideoStats()
        mMirrorLocalText =
            findViewById(R.id.setting_mirror_local_value)
        resetText(mMirrorLocalText, config().mirrorLocalIndex)
        mMirrorRemoteText =
            findViewById(R.id.setting_mirror_remote_value)
        resetText(mMirrorRemoteText, config().mirrorRemoteIndex)
        mMirrorEncodeText =
            findViewById(R.id.setting_mirror_encode_value)
        resetText(mMirrorEncodeText, config().mirrorEncodeIndex)
        mSeekBar = findViewById(R.id.bitrateSeekBar)
        mSeekBar.progress = config().bitrate
        mSeekprogress = findViewById(R.id.seekprogress)
        setSeekprogress(if (config().bitrate == 0) mSeekBar.progress else config().bitrate)
        mSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                setSeekprogress(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        loadAudioConfigs()
    }

    @SuppressLint("SetTextI18n")
    private fun setSeekprogress(progress: Int) {
        mSeekprogress.text = "码率 $progress Kbps"
    }

    private fun resetText(view: TextView?, index: Int) {
        if (view == null) {
            return
        }
        val strings =
            resources.getStringArray(R.array.mirror_modes)
        view.text = strings[index]
    }

    override fun onResume() {
        super.onResume()
        // Adjust for status bar height
        val titleLayout =
            findViewById<RelativeLayout>(R.id.role_title_layout)
        val params =
            titleLayout.layoutParams as LinearLayout.LayoutParams
        params.height += mStatusBarHeight
        titleLayout.layoutParams = params
    }

    override fun onBackPressed() {
        onBackArrowPressed(null)
    }

    fun onBackArrowPressed(view: View?) {
        println("click view $view")
        saveResolution()
        saveShowStats()
        saveBitrate()
        saveFpsIdx()
        finish()
    }

    private fun saveResolution() {
        val profileIndex = mResolutionAdapter!!.selected
        config().videoDimenIndex = profileIndex
        mPref!!.edit().putInt(Constants.PREF_RESOLUTION_IDX, profileIndex).apply()
    }

    private fun saveFpsIdx() {
        val fpsIdx = mFpsAdapter!!.selected
        config().fpsIndex = fpsIdx
        mPref!!.edit().putInt(Constants.PREF_FPS_IDX, fpsIdx).apply()
    }

    private fun saveShowStats() {
        config().setIfShowVideoStats(mVideoStatCheck.isActivated)
        mPref!!.edit().putBoolean(
            Constants.PREF_ENABLE_STATS,
            mVideoStatCheck.isActivated
        ).apply()
    }

    private fun saveBitrate() {
        config().bitrate = mSeekBar.progress
        mPref!!.edit().putInt(Constants.PREF_BITRATE, mSeekBar.progress).apply()
    }

    private fun saveVideoMirrorMode(key: String?, value: Int) {
        if (TextUtils.isEmpty(key)) return
        when (key) {
            Constants.PREF_MIRROR_LOCAL -> config().mirrorLocalIndex = value
            Constants.PREF_MIRROR_REMOTE -> config().mirrorRemoteIndex = value
            Constants.PREF_MIRROR_ENCODE -> config().mirrorEncodeIndex = value
        }
        mPref!!.edit().putInt(key, value).apply()
    }

    fun onStatsChecked(view: View) {
        view.isActivated = !view.isActivated
        //        statsManager().enableStats(view.isActivated());
    }

    fun onClick(view: View) {
        var key: String? = null
        var textView: TextView? = null
        when (view.id) {
            R.id.setting_mirror_local_view -> {
                key = Constants.PREF_MIRROR_LOCAL
                textView = mMirrorLocalText
            }
            R.id.setting_mirror_remote_view -> {
                key = Constants.PREF_MIRROR_REMOTE
                textView = mMirrorRemoteText
            }
            R.id.setting_mirror_encode_view -> {
                key = Constants.PREF_MIRROR_ENCODE
                textView = mMirrorEncodeText
            }
        }
        textView?.let { showDialog(key, it) }
    }

    private fun showDialog(key: String?, view: TextView) {
        val builder = AlertDialog.Builder(this)
        val strings =
            resources.getStringArray(R.array.mirror_modes)
        val checkedItem =
            listOf(*strings).indexOf(view.text.toString())
        builder.setSingleChoiceItems(
            strings,
            checkedItem
        ) { dialog, which ->
            saveVideoMirrorMode(key, which)
            resetText(view, which)
            dialog.dismiss()
        }
        builder.create().show()
    }

    override fun onStop() {
        super.onStop()
        if (isFinishing && !isChangingConfigurations) {
            saveAudioConfigs()
        }
    }

    private fun saveAudioConfigs() {
        config().profile = audioProfileConfigs[audioprofileTypeRG.checkedRadioButtonId]!!
        config().scenario = audioScenarioConfigs[audioScenarioTypeRG.checkedRadioButtonId]!!
        mPref!!.edit().putInt(Constants.PREF_AUDIO_PROFILE, config().profile).apply()
        mPref!!.edit().putInt(Constants.PREF_AUDIO_SCENARIO, config().scenario).apply()
    }

    private fun loadAudioConfigs() {
        config().profile = mPref!!.getInt(Constants.PREF_AUDIO_PROFILE,0)
        config().scenario = mPref!!.getInt(Constants.PREF_AUDIO_SCENARIO,0)

        val profileIdMap = audioProfileConfigs.filterValues {
            it == config().profile
        }

        val scenarioMap = audioScenarioConfigs.filterValues {
            it == config().scenario
        }

        if(profileIdMap.isNotEmpty()) {
            audioprofileTypeRG.check(profileIdMap.keys.first())
        } else {
            audioprofileTypeRG.check(0)
        }

        if(scenarioMap.isNotEmpty()) {
            audioScenarioTypeRG.check(scenarioMap.keys.first())
        } else {
            audioScenarioTypeRG.check(0)

        }
    }

    private fun config(): BigoEngineConfig {
        return LiveApplication.config
    }

    companion object {
        private const val DEFAULT_SPAN = 3
        fun go(ctx: Context) {
            ctx.startActivity(Intent(ctx, SettingsActivity::class.java))
        }

        private const val TAG = "SettingsActivity"
    }
}