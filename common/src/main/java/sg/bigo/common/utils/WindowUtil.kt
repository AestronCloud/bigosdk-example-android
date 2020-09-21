package sg.bigo.common.utils

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import sg.bigo.common.LiveApplication


object WindowUtil {
    fun hideWindowStatusBar(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    fun getSystemStatusBarHeight(context: Context): Int {
        val id = context.resources.getIdentifier(
            "status_bar_height", "dimen", "android"
        )
        return if (id > 0) context.resources.getDimensionPixelSize(id) else id
    }


    val w: Int by lazy {
        LiveApplication.appContext!!.resources.displayMetrics.widthPixels
    }

    val h: Int by lazy {
        LiveApplication.appContext!!.resources.displayMetrics.heightPixels
    }

}