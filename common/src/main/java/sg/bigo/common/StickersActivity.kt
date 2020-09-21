package sg.bigo.common

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_home.*
import sg.bigo.common.view.*

class StickersActivity : BottomActivity() {
    private val list = mutableListOf<Fragment>()
    private val titles = mutableListOf<String>()
    private lateinit var stickerFragment:StickerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        stickerFragment = StickerFragment()
        iv_compare.setOnTouchListener { _, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    stickerFragment.disable()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    stickerFragment.enable()
                }
                else -> {}
            }
            return@setOnTouchListener true
        }

        seekprogress.visibility = View.GONE
        list.add(stickerFragment)
        titles.add("贴纸")
        val myFragmentAdapter = MyFragmentAdapter(supportFragmentManager, list, titles)
        vp.adapter = myFragmentAdapter
        tabs.setupWithViewPager(vp)
    }
}
