package sg.bigo.common.view

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import sg.bigo.common.R

/**
 * Description:
 * @author YangYongwen
 * Created on 2019-08-13
 */
class SelectableImageView : androidx.appcompat.widget.AppCompatImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        if (selected) {
            setColorFilter(resources.getColor(R.color.blue), PorterDuff.Mode.MULTIPLY)
        } else {
            clearColorFilter()
        }
    }

}