package sg.bigo.common

import android.os.Bundle
import android.view.MotionEvent
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_home.*
import sg.bigo.common.view.FilterFragment
import sg.bigo.common.view.MyFragmentAdapter
import sg.bigo.common.view.SkinBeautyFragment

class BasicBeautifySettingActivity: BottomActivity() {

    private val list = mutableListOf<Fragment>()
    private val titles = mutableListOf<String>()
    private lateinit var filterFragment: FilterFragment
    private lateinit var skinBeautyFragment: SkinBeautyFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        seekprogress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    filterFragment.onSeekProgress(progress)
                    skinBeautyFragment.onSeekProgress(progress)
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }


        })

        iv_compare.setOnTouchListener { _, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    filterFragment.disable()
                    skinBeautyFragment.disable()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    filterFragment.enable()
                    skinBeautyFragment.enable()
                }
                else -> {
                }
            }
            return@setOnTouchListener true
        }

        filterFragment = FilterFragment()
        skinBeautyFragment = SkinBeautyFragment()

        list.add(filterFragment)
        list.add(skinBeautyFragment)

        titles.add("滤镜")
        titles.add("美肤")
        val myFragmentAdapter = MyFragmentAdapter(supportFragmentManager, list, titles)
        vp.adapter = myFragmentAdapter
        tabs.setupWithViewPager(vp)
    }
}