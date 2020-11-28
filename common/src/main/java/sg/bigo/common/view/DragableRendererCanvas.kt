package sg.bigo.common.view

import android.content.Context
import android.util.AttributeSet
import sg.bigo.opensdk.api.struct.RendererCanvas

class DragableRendererCanvas: RendererCanvas {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)



}