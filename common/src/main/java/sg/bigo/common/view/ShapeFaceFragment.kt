package sg.bigo.common.view

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_item.*
import kotlinx.android.synthetic.main.shape_face_item.view.*
import kotlinx.android.synthetic.main.skin_beauty_item.view.*
import sg.bigo.common.LiveApplication
import sg.bigo.common.R
import kotlin.math.ceil

data class ShapeFaceInfo(val tips:String, val iconResId:Int, var strength:Int) {
    fun isNone(): Boolean {
        return iconResId == R.mipmap.sticker_none
    }
}

class ShapeFaceFragment : BeautifyFragment() {
    private val TYPE_BIG_EYE = 1
    private val TYPE_THIN_FACE = 2

    companion object {
        val allShapeFaceInfo = mutableListOf(ShapeFaceInfo("关闭",R.mipmap.sticker_none,-1), ShapeFaceInfo("大眼",R.mipmap.icon_big_eye,2), ShapeFaceInfo("瘦脸",R.mipmap.icon_thin_face,2))
        var selectPos = -1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_item
                , container, false)
        return v
    }


    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    fun onSeekProgress(progress: Int) {
        if(!userVisibleHint) return
        if(selectPos == TYPE_BIG_EYE) {
            val shapeFaceInfo = allShapeFaceInfo[TYPE_BIG_EYE]
            shapeFaceInfo.strength = progress;//ceil((progress.toDouble()/20)).toInt()
            LiveApplication.avEngine().setBeautifyBigEye(shapeFaceInfo.strength)
        } else if(selectPos == TYPE_THIN_FACE){
            val shapeFaceInfo = allShapeFaceInfo[TYPE_THIN_FACE]
            shapeFaceInfo.strength = progress;//ceil((progress.toDouble()/20)).toInt()
            LiveApplication.avEngine().setBeautifyThinFace(shapeFaceInfo.strength)
        }
    }

    override fun enable() {
        if(selectPos > 0) {
            val eyeInfo = allShapeFaceInfo[TYPE_BIG_EYE]
            val thinFaceInfo = allShapeFaceInfo[TYPE_THIN_FACE]
            LiveApplication.avEngine().setBeautifyBigEye(eyeInfo.strength)
            LiveApplication.avEngine().setBeautifyThinFace(thinFaceInfo.strength)
        }
    }

    override fun disable() {
        LiveApplication.avEngine().setBeautifyBigEye(0)
        LiveApplication.avEngine().setBeautifyThinFace(0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_list.layoutManager = LinearLayoutManager(view.context).apply {
            orientation = RecyclerView.HORIZONTAL
        }
        rv_list.adapter = object : RecyclerView.Adapter<VH>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.shape_face_item,parent,false)
                return VH(v)
            }

            override fun getItemCount(): Int {
                return allShapeFaceInfo.size
            }

            override fun onBindViewHolder(holder: VH, position: Int) {
                val root = holder.itemView
                val skinBeautyInfo = allShapeFaceInfo[position]

                root.iv_feature_icon.setOnClickListener {
                    selectPos = position
                    getSeekBar().progress = skinBeautyInfo.strength
                    if(skinBeautyInfo.isNone()) {
                        disable()
                    } else {
                        onSeekProgress(skinBeautyInfo.strength)
                    }
                    notifyDataSetChanged()
                }

                if(position == selectPos) {
                    root.iv_feature_icon.setColorFilter(resources.getColor(R.color.blue), PorterDuff.Mode.MULTIPLY)
                } else {
                    root.iv_feature_icon.clearColorFilter()
                }
                root.iv_feature_icon.setImageDrawable(ContextCompat.getDrawable(root.context,skinBeautyInfo.iconResId))
                root.tv_feature_tips.text = skinBeautyInfo.tips

            }

        }
    }
}