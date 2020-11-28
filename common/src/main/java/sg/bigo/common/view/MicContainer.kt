package sg.bigo.common.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import sg.bigo.common.fragment.TranscodingCfgFragment
import sg.bigo.common.utils.WindowUtil
import sg.bigo.opensdk.api.struct.BigoTranscodingUser
import java.util.*
import kotlin.collections.LinkedHashMap


class MicContainer : ViewGroup {
    val TAG = "MicContainer"
    private var onLayoutPolicy:IOnLayoutPolicy = MultiLiveLayoutPolicy()

    fun setOnLayoutPolicy(onLayoutPolicy: IOnLayoutPolicy) {
        this.onLayoutPolicy = onLayoutPolicy
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount == 0) {
            return
        }

        var w = r - l
        var h = b - t

        val rootRect = Rect(l,t,l+w,t+h)
        val subRects =onLayoutPolicy.split(rootRect,childCount)
        Log.e(TAG, "onLayout: $subRects")
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.layout(subRects[i].left, subRects[i].top, subRects[i].left + subRects[i].width(), subRects[i].top + subRects[i].height())
        }


        TranscodingCfgFragment.liveTranscoding.width = 720
        TranscodingCfgFragment.liveTranscoding.height = 1280

        val widthRate = 720.toFloat()/WindowUtil.w.toFloat()
        val heightRate = 1280.toFloat()/WindowUtil.h.toFloat()

        TranscodingCfgFragment.replaceTranscodingUsers(getChildLayoutParams(widthRate,heightRate));
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (childCount == 0) {
            return
        }

        val rootRect = Rect(0,0,measuredWidth,measuredHeight)
        val subRects = onLayoutPolicy.split(rootRect,childCount)

        for (subRect in subRects) {
            Log.e(TAG, "onMeasure: ${subRect}")
        }

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.layoutParams is MarginLayoutParams) {
                measureChildWithMargins(child, MeasureSpec.makeMeasureSpec(subRects[i].width(), MeasureSpec.EXACTLY), 0,
                        MeasureSpec.makeMeasureSpec(subRects[i].height(), MeasureSpec.EXACTLY), 0)
            } else {
                measureChild(child, MeasureSpec.makeMeasureSpec(subRects[i].width(), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(subRects[i].height(), MeasureSpec.EXACTLY))
            }
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    private val mMicUsers = LinkedHashMap<Long, MicSeatView>()

    private fun getChildLayoutParams(): MutableList<BigoTranscodingUser> {
        return mutableListOf<BigoTranscodingUser>().apply {
            for (mic in mMicUsers) {
                var zOrder = 1
                if(mic.value.width == width && mic.value.height == height) {
                    zOrder = 0;
                }
                add(BigoTranscodingUser(mic.value.uid,mic.value.x.toInt(),mic.value.y.toInt(),mic.value.width,mic.value.height,zOrder,1.0f,1))
            }
        }
    }

    private fun getChildLayoutParams(widthRate: Float, heightRate: Float): MutableList<BigoTranscodingUser> {
        return mutableListOf<BigoTranscodingUser>().apply {
            for (mic in mMicUsers) {
                var zOrder = 1
                if(mic.value.width == width && mic.value.height == height) {
                    zOrder = 0;
                }
                add(BigoTranscodingUser(mic.value.uid, (mic.value.x * widthRate).toInt(), (mic.value.y * widthRate).toInt(), (mic.value.width * widthRate).toInt(), (mic.value.height * heightRate).toInt(), zOrder, 1.0f, 1))
            }
        }
    }


    fun hasMicSeatView(uid : Long) : Boolean {
        return mMicUsers.containsKey(uid)
    }

    fun addMicSeatView(context : Context, uid : Long, pos: Int) : MicSeatView? {
        mMicUsers[uid] = MicSeatView(context, uid)
        addView(mMicUsers[uid],pos)
        return mMicUsers[uid]
    }


    fun addMicSeatView(context : Context, uid : Long) : MicSeatView? {
        mMicUsers[uid] = MicSeatView(context, uid)
        addView(mMicUsers[uid])
        return mMicUsers[uid]
    }

    fun removeMicSeatView(uid : Long) : MicSeatView?{
        return mMicUsers.remove(uid)
    }

    fun inFirstSeat(uid: Long): Boolean {
        val i = 0;
        for (mMicUser in mMicUsers) {
            return mMicUser.key == uid
        }

        return true
    }

}