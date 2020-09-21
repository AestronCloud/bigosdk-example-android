package sg.bigo.common.view

import android.content.Context
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import sg.bigo.common.R

abstract class BeautifyFragment:Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    protected fun getSeekBar(): SeekBar {
        return activity!!.findViewById(R.id.seekprogress)
    }

    protected var isStoped = false
    override fun onStart() {
        isStoped = false
        super.onStart()
    }
    override fun onStop() {
        isStoped = true
        super.onStop()
    }

    abstract fun enable()

    abstract fun disable()

}