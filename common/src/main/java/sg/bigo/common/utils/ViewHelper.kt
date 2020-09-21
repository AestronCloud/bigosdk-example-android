package sg.bigo.common.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText

object ViewHelper {
    /**
     * 就是为了编辑框为空的时候disable掉按钮
     * 这只是一个操作体验，接入者无需关心此函数细节
     */
    fun mutuallyExclusiveEnableView(toWatchEditText: EditText, mutuallyExclusiveViews: List<View>) {

        fun checkNeedEnableView() {
            for (view in mutuallyExclusiveViews) {
                view.isEnabled = toWatchEditText.text.toString().isNotEmpty()
            }
        }

        checkNeedEnableView()

        toWatchEditText.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                checkNeedEnableView()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}