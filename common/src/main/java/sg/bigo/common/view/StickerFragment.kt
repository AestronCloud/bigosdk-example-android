package sg.bigo.common.view

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_item.*
import kotlinx.android.synthetic.main.sticker_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import sg.bigo.common.LiveApplication
import sg.bigo.common.R
import sg.bigo.common.data.ModelUtils
import sg.bigo.common.data.StickerInfo
import sg.bigo.common.data.StickerUtils
import sg.bigo.common.utils.Decompress
import java.io.File
import java.nio.ByteBuffer
import java.util.*

class StickerFragment : BeautifyFragment() {

    companion object {
        private val allStickers = mutableListOf<StickerInfo>()
        private var selectPos = -1
    }
    private val mUIHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_sticker_item
                , container, false)
        return v
    }


    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlobalScope.launch(Dispatchers.IO) {

            if(allStickers.isEmpty()) {
                var stickers = StickerUtils.prase()

                stickers = stickers.filter {
                    it.resourceUrl.isNotEmpty()
                }
//            File("/sdcard/sticker/").mkdirs()
//            for (sticker in stickers) {
//                FilterFragment.downLoad(sticker.resourceUrl,"/sdcard/sticker/${sticker.effectId}.zip")
//            }

                if (stickers.isNotEmpty()) {
                    Log.e(TAG, "onViewCreated: 0")

                    val dir = File(stickers[0].getStoreDir())
                    if (!dir.exists()) {
                        dir.mkdirs()
                    }


                    Decompress.unzipFromAssets(activity,"stickers.zip",dir.absolutePath)
                    Decompress.unzipFromAssets(activity,"models.zip", ModelUtils.dir);
                    LiveApplication.avEngine().enableVenusEngine(ModelUtils.dir)

                    val childFiles = dir.listFiles()

                    Log.e(TAG, "onViewCreated: 1")
                    if(childFiles != null) {
                        for (childFile in childFiles) {
                            if(childFile.absolutePath.endsWith(".zip")) {
                                Decompress.unzip(childFile.absolutePath, childFile.absolutePath.substringBeforeLast(".zip"))
                            }
                        }
                    }
                    Log.e(TAG, "onViewCreated: 2")
                }

                if (stickers.isNotEmpty()) {
                    allStickers.clear()
                    allStickers.add(StickerInfo.createNone())
                    allStickers.addAll(stickers)
                }

            }


            mUIHandler.post{
                onDataInited()
            }

        }
    }

    override fun enable() {
        if (selectPos > 0) {
            allStickers[selectPos]?.let {
                LiveApplication.avEngine().setSticker(it.getUnzipedFileDir())
            }
        }
    }

    override fun disable() {
        LiveApplication.avEngine().setSticker(null)
    }

    private fun onDataInited() {
        if(isStoped) return

        rv_list.visibility = View.VISIBLE
        rv_list.setHasFixedSize(true)
        rv_list.layoutManager = GridLayoutManager(context,4).apply {
            orientation = RecyclerView.VERTICAL
        }

        rv_list.adapter = object : RecyclerView.Adapter<VH>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.sticker_item,parent,false)
                return VH(v)
            }

            override fun getItemCount(): Int {
                return allStickers.size
            }

            override fun onBindViewHolder(holder: VH, position: Int) {
                val root = holder.itemView
                val sticker = allStickers[position]

                root.iv_sticker_icon.setOnClickListener {
                    selectPos = position
                    if (!sticker.isNone()) {
                        LiveApplication.avEngine().setSticker(sticker.getUnzipedFileDir())

                        LiveApplication.avEngine().setMediaSideFlags(true, false, 0)
                        val test_uuid = byteArrayOf(0x3c.toByte(), 0x13.toByte(), 0x1e.toByte(), 0x19.toByte(), 0x16.toByte(), 0x18.toByte(), 0x49.toByte(), 0x1c.toByte(), 0x1a.toByte(), 0x33.toByte(), 0x15.toByte(), 0x1e.toByte(), 0x1f.toByte(), 0x12.toByte(), 0x53.toByte(), 0x4d.toByte())
                        val uuidStr = String(test_uuid)
                        var testSeiData = "click sticker,SendSeiInVideo."
                        testSeiData = uuidStr + testSeiData
                        val SeiData = ByteBuffer.wrap(testSeiData.toByteArray())
                        LiveApplication.avEngine().sendMediaSideInfo(SeiData)

                    } else {
                        LiveApplication.avEngine().setSticker(null)
                    }
                    notifyDataSetChanged()
                }

                GlobalScope.launch(Dispatchers.IO) {
                    val uri: Uri = Uri.fromFile(File(sticker.getIconPath()))

                    mUIHandler.post{

                        if(sticker.isNone()) {
                            root.iv_sticker_icon.setImageDrawable(ContextCompat.getDrawable(root.context,R.mipmap.sticker_none))
                        } else {
                            root.iv_sticker_icon.setImageURI(uri)
                        }


                        if(selectPos == position) {
                            root.iv_sticker_icon.setColorFilter(R.color.blue)
                        } else {
                            root.iv_sticker_icon.clearColorFilter()
                        }
                        root.iv_sticker_tips.text = sticker.name
                    }
                }

            }

        }.apply {
            setHasStableIds(true)
        }
    }


    private val TAG = "StickerFragment"
}