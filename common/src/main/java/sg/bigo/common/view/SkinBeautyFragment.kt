package sg.bigo.common.view

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_item.*
import kotlinx.android.synthetic.main.skin_beauty_item.view.*
import sg.bigo.common.LiveApplication
import sg.bigo.common.R
import sg.bigo.common.data.FilterUtils


data class SkinBeautyInfo(val tips:String, val iconResId:Int, var strength:Int = 50) {
    fun isNone(): Boolean {
        return iconResId == R.mipmap.sticker_none
    }
}

class SkinBeautyFragment : BeautifyFragment() {
    private val TYPE_SKIN_WHITE = 1
    private val TYPE_SKIN_SMOOTH = 2

    companion object {
        val allSkinBeauties = mutableListOf(SkinBeautyInfo("关闭",R.mipmap.sticker_none,-1),SkinBeautyInfo("美白",R.mipmap.icon_white_face,70), SkinBeautyInfo("磨皮",R.mipmap.icon_smooth_skin,30))
        var selectPos = -1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item
                , container, false)
    }


    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_list.layoutManager = LinearLayoutManager(view.context).apply {
            orientation = RecyclerView.HORIZONTAL
        }
        rv_list.adapter = object : RecyclerView.Adapter<VH>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.skin_beauty_item,parent,false)
                return VH(v)
            }

            override fun getItemCount(): Int {
                return allSkinBeauties.size
            }

            override fun onBindViewHolder(holder: VH, position: Int) {
                val root = holder.itemView
                val skinBeautyInfo = allSkinBeauties[position]


                root.iv_skin_icon.setOnClickListener {
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
                    root.iv_skin_icon.setColorFilter(resources.getColor(R.color.blue), PorterDuff.Mode.MULTIPLY)
                } else {
                    root.iv_skin_icon.clearColorFilter()
                }
                root.iv_skin_icon.setImageDrawable(ContextCompat.getDrawable(root.context,skinBeautyInfo.iconResId))
                root.tv_skin_tips.text = skinBeautyInfo.tips

            }
        }
    }

    override fun enable() {
        if (selectPos > 0) {
            val lutStrength = allSkinBeauties[TYPE_SKIN_WHITE].strength
            val smoothStrength = allSkinBeauties[TYPE_SKIN_SMOOTH].strength
            LiveApplication.avEngine().enableBeautyMode(true)
            LiveApplication.avEngine().setBeautifySmoothSkin(smoothStrength)
            LiveApplication.avEngine().setBeautifyWhiteSkin(FilterUtils.whiteSkinFilter.getResourcePath(),lutStrength)
        }
    }

    override fun disable() {
        LiveApplication.avEngine().enableBeautyMode(false)
        LiveApplication.avEngine().setBeautifyWhiteSkin(null,0)
        LiveApplication.avEngine().setBeautifySmoothSkin(0)
    }

    fun onSeekProgress(progress: Int) {
        if(!userVisibleHint) return

        if (selectPos > 0) {

            if(selectPos == TYPE_SKIN_WHITE) {
                val info = allSkinBeauties[selectPos]
                info.strength = progress
                LiveApplication.avEngine().enableBeautyMode(true)
                LiveApplication.avEngine().setBeautifyWhiteSkin(FilterUtils.whiteSkinFilter.getResourcePath(),info.strength)
            } else if(selectPos == TYPE_SKIN_SMOOTH) {
                val info = allSkinBeauties[selectPos]
                info.strength = progress
                LiveApplication.avEngine().enableBeautyMode(true)
                LiveApplication.avEngine().setBeautifySmoothSkin(info.strength)
            }
        }
    }
}

private const val TAG = "SkinBeautyFragment"