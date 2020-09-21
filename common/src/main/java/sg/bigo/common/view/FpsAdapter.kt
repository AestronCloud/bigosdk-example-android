package sg.bigo.common.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import sg.bigo.common.R
import java.util.*

class FpsAdapter(private val mContext: Context, var selected: Int) : RecyclerView.Adapter<FpsAdapter.FpsHolder>() {
    protected var mItems = ArrayList<FpsItem>()
    protected fun initData(context: Context) {
        val size = 6
        val labels = context.resources.getStringArray(R.array.string_array_fps)
        for (i in 0 until size) {
            val item = FpsItem(labels[i], 0)
            mItems.add(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FpsAdapter.FpsHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.dimension_item, parent, false)
        return FpsHolder(view)
    }

    override fun onBindViewHolder(holder: FpsHolder, position: Int) {
        val item = mItems[position]
        val content = holder.fps
        content.text = item.label
        content.setOnClickListener {
            selected = position
            notifyDataSetChanged()
        }
        content.isSelected = position == selected
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    inner class FpsHolder: ViewHolder {
        var fps: TextView

        constructor(itemView: View) : super(itemView) {
            fps = itemView.findViewById(R.id.resolution)
        }
    }

    class FpsItem internal constructor(var label: String, var dimension: Int)

    init {
        initData(mContext)
    }
}