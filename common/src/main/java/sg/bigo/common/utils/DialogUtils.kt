package sg.bigo.common.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Point
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import sg.bigo.common.LiveApplication
import sg.bigo.common.LiveApplication.Companion.appContext
import sg.bigo.common.R

object DialogUtils {
    fun fitFullScreen(dialog: Dialog) {
        if (isAllScreenDevice) {
            val window = dialog.window
            if (window != null) {
                val decorView = window.decorView
                decorView?.setOnSystemUiVisibilityChangeListener {
                    val uiOptionsx = 1794
                    val uiOptions: Int
                    uiOptions = if (Build.VERSION.SDK_INT >= 19) {
                        uiOptionsx or 4096
                    } else {
                        uiOptionsx or 1
                    }
                    decorView.systemUiVisibility = uiOptions
                }
            }
        }
    }

    @Volatile
    private var mHasCheckAllScreen = false

    @Volatile
    private var mIsAllScreenDevice = false
    val isAllScreenDevice: Boolean
        get() = if (mHasCheckAllScreen) {
            mIsAllScreenDevice
        } else {
            mHasCheckAllScreen = true
            mIsAllScreenDevice = false
            if (Build.VERSION.SDK_INT < 21) {
                false
            } else {
                val windowManager = appContext!!.getSystemService("window") as WindowManager
                if (windowManager != null) {
                    val display = windowManager.defaultDisplay
                    val point = Point()
                    display.getRealSize(point)
                    val width: Float
                    val height: Float
                    if (point.x < point.y) {
                        width = point.x.toFloat()
                        height = point.y.toFloat()
                    } else {
                        width = point.y.toFloat()
                        height = point.x.toFloat()
                    }
                    if (height / width >= 1.97f) {
                        mIsAllScreenDevice = true
                    }
                }
                mIsAllScreenDevice
            }
        }


    private const val TAG = "DialogUtils"
    fun showAddPublishStringUrlDialog(activity: Activity, rtmpUrls: MutableSet<String>) {
        val pkInputView: View = LayoutInflater.from(activity).inflate(R.layout.dialog_input_uid, null)
        val et_black_list = pkInputView.findViewById<EditText>(R.id.et_black_uid)
        val op_tips = pkInputView.findViewById<TextView>(R.id.op_tips)
        op_tips.visibility = View.GONE

        et_black_list.setText("rtmp://169.136.125.28/aab/aac")

        val tv_urls = pkInputView.findViewById<TextView>(R.id.tv_urls)

        var urlsTip = "已添加推流列表\n"
        for (rtmpUrl in rtmpUrls) {
            urlsTip += ":${rtmpUrl}\n"
        }

        tv_urls.text = urlsTip

//        pkInputView.findViewById<TextView>(R.id.textView2).visibility = View.INVISIBLE

        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle("添加rtmp推流地址");
        builder.setView(pkInputView)
        builder.setPositiveButton(R.string.tips_confirm
        ) { _, _ ->
            Log.i(TAG, "showAddPublishStringUrlDialog: ${LiveApplication.avEngine().addPublishStreamUrl(et_black_list.text.toString())}");
        }.setNegativeButton(R.string.tips_cancel) { _, _ -> }
        builder.show()
    }


    fun showRemovePublishStringUrlDialog(activity: Activity, rtmpUrls: MutableSet<String>) {
        val pkInputView: View = LayoutInflater.from(activity).inflate(R.layout.dialog_input_uid, null)
        val et_black_list = pkInputView.findViewById<EditText>(R.id.et_black_uid)
        val op_tips = pkInputView.findViewById<TextView>(R.id.op_tips)
        op_tips.visibility = View.GONE

        val tv_urls = pkInputView.findViewById<TextView>(R.id.tv_urls)

        var urlsTip = "已添加推流列表\n"
        for (rtmpUrl in rtmpUrls) {
            urlsTip += ":${rtmpUrl}\n"
        }

        tv_urls.text = urlsTip

        et_black_list.setText("rtmp://169.136.125.28/aab/aac")

//        pkInputView.findViewById<TextView>(R.id.textView2).visibility = View.INVISIBLE

        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle("移除rtmp推流地址");
        builder.setView(pkInputView)
        builder.setPositiveButton(R.string.tips_confirm
        ) { _, _ ->
            Log.i(TAG, "showAddPublishStringUrlDialog: ${LiveApplication.avEngine().removePublishStreamUrl(et_black_list.text.toString())}");
        }.setNegativeButton(R.string.tips_cancel) { _, _ -> }
        builder.show()
    }

}