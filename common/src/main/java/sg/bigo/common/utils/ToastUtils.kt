package sg.bigo.common.utils

import android.widget.Toast
import sg.bigo.common.LiveApplication

object ToastUtils {
    fun show(msg:String) {
        Toast.makeText(LiveApplication.appContext,msg,Toast.LENGTH_LONG).show()
    }
}