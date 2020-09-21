package sg.bigo.common

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat

abstract class BottomActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bottomMe()
        hideBottomUIMenu()
        hideDimAmount()


    }

    private fun hideDimAmount() {
        window.setDimAmount(0f);
    }

    private fun bottomMe() {
        val window: Window = window
        //取消设置Window半透明的Flag
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //设置状态栏为透明/或者需要的颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this,R.color.transparent)
        }
        getWindow().setGravity(Gravity.BOTTOM) //设置显示在底部 默认在中间
        val lp: WindowManager.LayoutParams = getWindow().attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT //设置宽度满屏
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        getWindow().attributes = lp
        setFinishOnTouchOutside(true) //允许点击空白处关闭
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected open fun hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT in 12..18) { // lower api
            val v = this.window.decorView
            v.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            val decorView = window.decorView
            val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN)
            decorView.systemUiVisibility = uiOptions
        }
    }


}